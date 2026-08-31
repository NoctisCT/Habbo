<?php

namespace App\Services;

use Illuminate\Http\UploadedFile;
use RuntimeException;
use SplQueue;

class BadgePixelArtService
{
    public const CANVAS_SIZE = 40;
    private const WORKING_MAX_SIZE = 256;

    /**
     * @return array{
     *   image:string,
     *   width:int,
     *   height:int,
     *   mode:string,
     *   detail:int,
     *   mime:string,
     *   extension:string,
     *   background_removed:bool
     * }
     */
    public function convert(
        UploadedFile $file,
        string $mode,
        int $detail = 160,
        bool $removeBackground = true
    ): array {
        if (! extension_loaded('gd')) {
            throw new RuntimeException(
                'La extensión GD de PHP no está disponible.'
            );
        }

        $bytes = file_get_contents($file->getRealPath());

        if ($bytes === false || $bytes === '') {
            throw new RuntimeException(
                'No se pudo leer la imagen subida.'
            );
        }

        $source = @imagecreatefromstring($bytes);

        if ($source === false) {
            throw new RuntimeException(
                'El archivo no es una imagen compatible.'
            );
        }

        try {
            $mode = $mode === 'pixelart'
                ? 'pixelart'
                : 'auto';

            $detail = $this->normalizeDetail($detail);

            if ($mode === 'pixelart') {
                $result = $this->fitPixelArt($source);
                $backgroundRemoved = false;
            } else {
                [
                    $result,
                    $backgroundRemoved,
                ] = $this->convertAutomatic(
                    $source,
                    $detail,
                    $removeBackground
                );
            }

            $gif = $this->toGif($result);

            imagedestroy($result);

            return [
                'image' => $gif,
                'width' => self::CANVAS_SIZE,
                'height' => self::CANVAS_SIZE,
                'mode' => $mode,
                'detail' => $detail,
                'mime' => 'image/gif',
                'extension' => 'gif',
                'background_removed' => $backgroundRemoved,
            ];
        } finally {
            imagedestroy($source);
        }
    }

    /**
     * Pixelart propio:
     * conserva proporción, transparencia y nearest-neighbour.
     */
    private function fitPixelArt(\GdImage $source): \GdImage
    {
        $srcW = imagesx($source);
        $srcH = imagesy($source);

        if ($srcW < 1 || $srcH < 1) {
            throw new RuntimeException(
                'La imagen tiene dimensiones inválidas.'
            );
        }

        $canvas = $this->transparentCanvas(
            self::CANVAS_SIZE,
            self::CANVAS_SIZE
        );

        $scale = min(
            1,
            self::CANVAS_SIZE / $srcW,
            self::CANVAS_SIZE / $srcH
        );

        $dstW = max(1, (int) floor($srcW * $scale));
        $dstH = max(1, (int) floor($srcH * $scale));

        $dstX = (int) floor(
            (self::CANVAS_SIZE - $dstW) / 2
        );

        $dstY = (int) floor(
            (self::CANVAS_SIZE - $dstH) / 2
        );

        imagecopyresized(
            $canvas,
            $source,
            $dstX,
            $dstY,
            0,
            0,
            $dstW,
            $dstH,
            $srcW,
            $srcH
        );

        return $canvas;
    }

    /**
     * @return array{0:\GdImage,1:bool}
     */
    private function convertAutomatic(
        \GdImage $source,
        int $colors,
        bool $removeBackground
    ): array {
        $working = $this->workingCopy($source);

        try {
            $hadTransparency =
                $this->hasTransparency($working);

            $backgroundRemoved = false;

            if ($removeBackground) {
                $backgroundRemoved =
                    $this->removeUniformEdgeBackground(
                        $working
                    );
            }

            $useVisibleBounds =
                $hadTransparency ||
                $backgroundRemoved ||
                $this->hasTransparency($working);

            if ($useVisibleBounds) {
                $bounds =
                    $this->visibleBounds($working);
            } else {
                $bounds =
                    $this->centerSquareBounds(
                        $working
                    );
            }

            $canvas = $this->fitBoundsToCanvas(
                $working,
                $bounds
            );

            // Recupera algo de definición después del downscale,
            // sin alterar el contraste ni destruir gradientes.
            if (function_exists('imageconvolution')) {
                @imageconvolution(
                    $canvas,
                    [
                        [0, -1, 0],
                        [-1, 5, -1],
                        [0, -1, 0],
                    ],
                    1,
                    0
                );
            }

            // La paleta es amplia: 96/160/240 colores.
            // Sin dithering agresivo para evitar ruido visual.
            @imagetruecolortopalette(
                $canvas,
                false,
                $colors
            );

            if (! imageistruecolor($canvas)) {
                @imagepalettetotruecolor(
                    $canvas
                );

                imagealphablending(
                    $canvas,
                    true
                );

                imagesavealpha(
                    $canvas,
                    true
                );
            }

            return [
                $canvas,
                $backgroundRemoved,
            ];
        } finally {
            imagedestroy($working);
        }
    }

    private function workingCopy(
        \GdImage $source
    ): \GdImage {
        $srcW = imagesx($source);
        $srcH = imagesy($source);

        $scale = min(
            1,
            self::WORKING_MAX_SIZE / max(
                $srcW,
                $srcH
            )
        );

        $dstW = max(
            1,
            (int) round($srcW * $scale)
        );

        $dstH = max(
            1,
            (int) round($srcH * $scale)
        );

        $working = $this->transparentCanvas(
            $dstW,
            $dstH
        );

        imagecopyresampled(
            $working,
            $source,
            0,
            0,
            0,
            0,
            $dstW,
            $dstH,
            $srcW,
            $srcH
        );

        return $working;
    }

    /**
     * Elimina solo un fondo uniforme conectado a los bordes.
     *
     * Esto NO es segmentación semántica. Funciona bien para negro,
     * blanco u otros fondos planos; si las esquinas no parecen formar
     * el mismo fondo, no elimina nada.
     */
    private function removeUniformEdgeBackground(
        \GdImage $image
    ): bool {
        $width = imagesx($image);
        $height = imagesy($image);

        if ($width < 2 || $height < 2) {
            return false;
        }

        $corners = [
            $this->rgbaAt($image, 0, 0),
            $this->rgbaAt(
                $image,
                $width - 1,
                0
            ),
            $this->rgbaAt(
                $image,
                0,
                $height - 1
            ),
            $this->rgbaAt(
                $image,
                $width - 1,
                $height - 1
            ),
        ];

        // Si las esquinas ya son transparentes, no hay fondo opaco
        // que debamos detectar.
        $allTransparent = true;

        foreach ($corners as $corner) {
            if ($corner['alpha'] < 100) {
                $allTransparent = false;
                break;
            }
        }

        if ($allTransparent) {
            return false;
        }

        $reference = [
            'red' => (int) round(
                array_sum(
                    array_column(
                        $corners,
                        'red'
                    )
                ) / 4
            ),
            'green' => (int) round(
                array_sum(
                    array_column(
                        $corners,
                        'green'
                    )
                ) / 4
            ),
            'blue' => (int) round(
                array_sum(
                    array_column(
                        $corners,
                        'blue'
                    )
                ) / 4
            ),
        ];

        // Si las cuatro esquinas son muy distintas, probablemente
        // existe un fondo complejo/fotográfico. No arriesgamos.
        foreach ($corners as $corner) {
            if (
                $this->colorDistance(
                    $corner,
                    $reference
                ) > 42
            ) {
                return false;
            }
        }

        $tolerance = 30;
        $size = $width * $height;

        $visited = array_fill(
            0,
            $size,
            false
        );

        $queue = new SplQueue();

        $enqueueIfBackground =
            function (
                int $x,
                int $y
            ) use (
                $image,
                $width,
                $reference,
                $tolerance,
                &$visited,
                $queue
            ): void {
                $index = $y * $width + $x;

                if ($visited[$index]) {
                    return;
                }

                $rgba = $this->rgbaAt(
                    $image,
                    $x,
                    $y
                );

                if (
                    $rgba['alpha'] >= 100 ||
                    $this->colorDistance(
                        $rgba,
                        $reference
                    ) <= $tolerance
                ) {
                    $visited[$index] = true;
                    $queue->enqueue([
                        $x,
                        $y,
                    ]);
                }
            };

        for ($x = 0; $x < $width; $x++) {
            $enqueueIfBackground($x, 0);
            $enqueueIfBackground(
                $x,
                $height - 1
            );
        }

        for ($y = 0; $y < $height; $y++) {
            $enqueueIfBackground(0, $y);
            $enqueueIfBackground(
                $width - 1,
                $y
            );
        }

        $transparentColor =
            imagecolorallocatealpha(
                $image,
                0,
                0,
                0,
                127
            );

        $removed = 0;

        while (! $queue->isEmpty()) {
            [
                $x,
                $y,
            ] = $queue->dequeue();

            imagesetpixel(
                $image,
                $x,
                $y,
                $transparentColor
            );

            $removed++;

            foreach (
                [
                    [$x - 1, $y],
                    [$x + 1, $y],
                    [$x, $y - 1],
                    [$x, $y + 1],
                ] as [$nx, $ny]
            ) {
                if (
                    $nx < 0 ||
                    $ny < 0 ||
                    $nx >= $width ||
                    $ny >= $height
                ) {
                    continue;
                }

                $index =
                    $ny * $width + $nx;

                if ($visited[$index]) {
                    continue;
                }

                $rgba = $this->rgbaAt(
                    $image,
                    $nx,
                    $ny
                );

                if (
                    $rgba['alpha'] >= 100 ||
                    $this->colorDistance(
                        $rgba,
                        $reference
                    ) <= $tolerance
                ) {
                    $visited[$index] = true;

                    $queue->enqueue([
                        $nx,
                        $ny,
                    ]);
                }
            }
        }

        // Evita considerar como fondo eliminado cuatro píxeles
        // accidentales de las esquinas.
        return $removed >= max(
            16,
            (int) round($size * 0.01)
        );
    }

    /**
     * @return array{x:int,y:int,width:int,height:int}
     */
    private function visibleBounds(
        \GdImage $image
    ): array {
        $width = imagesx($image);
        $height = imagesy($image);

        $minX = $width;
        $minY = $height;
        $maxX = -1;
        $maxY = -1;

        for ($y = 0; $y < $height; $y++) {
            for ($x = 0; $x < $width; $x++) {
                $rgba = $this->rgbaAt(
                    $image,
                    $x,
                    $y
                );

                if ($rgba['alpha'] >= 100) {
                    continue;
                }

                $minX = min($minX, $x);
                $minY = min($minY, $y);
                $maxX = max($maxX, $x);
                $maxY = max($maxY, $y);
            }
        }

        if ($maxX < $minX || $maxY < $minY) {
            return $this->centerSquareBounds(
                $image
            );
        }

        return [
            'x' => $minX,
            'y' => $minY,
            'width' => $maxX - $minX + 1,
            'height' => $maxY - $minY + 1,
        ];
    }

    /**
     * @return array{x:int,y:int,width:int,height:int}
     */
    private function centerSquareBounds(
        \GdImage $image
    ): array {
        $width = imagesx($image);
        $height = imagesy($image);

        $size = min(
            $width,
            $height
        );

        return [
            'x' => (int) floor(
                ($width - $size) / 2
            ),
            'y' => (int) floor(
                ($height - $size) / 2
            ),
            'width' => $size,
            'height' => $size,
        ];
    }

    /**
     * @param array{x:int,y:int,width:int,height:int} $bounds
     */
    private function fitBoundsToCanvas(
        \GdImage $source,
        array $bounds
    ): \GdImage {
        $canvas = $this->transparentCanvas(
            self::CANVAS_SIZE,
            self::CANVAS_SIZE
        );

        $scale = min(
            self::CANVAS_SIZE /
                $bounds['width'],
            self::CANVAS_SIZE /
                $bounds['height']
        );

        $dstW = max(
            1,
            (int) floor(
                $bounds['width'] * $scale
            )
        );

        $dstH = max(
            1,
            (int) floor(
                $bounds['height'] * $scale
            )
        );

        $dstX = (int) floor(
            (self::CANVAS_SIZE - $dstW) / 2
        );

        $dstY = (int) floor(
            (self::CANVAS_SIZE - $dstH) / 2
        );

        imagecopyresampled(
            $canvas,
            $source,
            $dstX,
            $dstY,
            $bounds['x'],
            $bounds['y'],
            $dstW,
            $dstH,
            $bounds['width'],
            $bounds['height']
        );

        return $canvas;
    }

    private function hasTransparency(
        \GdImage $image
    ): bool {
        $width = imagesx($image);
        $height = imagesy($image);

        for ($y = 0; $y < $height; $y += 2) {
            for ($x = 0; $x < $width; $x += 2) {
                $rgba = $this->rgbaAt(
                    $image,
                    $x,
                    $y
                );

                if ($rgba['alpha'] >= 100) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return array{red:int,green:int,blue:int,alpha:int}
     */
    private function rgbaAt(
        \GdImage $image,
        int $x,
        int $y
    ): array {
        return imagecolorsforindex(
            $image,
            imagecolorat(
                $image,
                $x,
                $y
            )
        );
    }

    /**
     * @param array{red:int,green:int,blue:int} $a
     * @param array{red:int,green:int,blue:int} $b
     */
    private function colorDistance(
        array $a,
        array $b
    ): float {
        return sqrt(
            (($a['red'] - $b['red']) ** 2) +
            (($a['green'] - $b['green']) ** 2) +
            (($a['blue'] - $b['blue']) ** 2)
        );
    }

    private function transparentCanvas(
        int $width,
        int $height
    ): \GdImage {
        $canvas = imagecreatetruecolor(
            $width,
            $height
        );

        imagealphablending(
            $canvas,
            false
        );

        imagesavealpha(
            $canvas,
            true
        );

        $transparent = imagecolorallocatealpha(
            $canvas,
            0,
            0,
            0,
            127
        );

        imagefill(
            $canvas,
            0,
            0,
            $transparent
        );

        imagealphablending(
            $canvas,
            true
        );

        return $canvas;
    }

    /**
     * GIF final con transparencia binaria y hasta 255 colores reales.
     */
    private function toGif(
        \GdImage $image
    ): string {
        $width = imagesx($image);
        $height = imagesy($image);

        $gif = imagecreate(
            $width,
            $height
        );

        if ($gif === false) {
            throw new RuntimeException(
                'No se pudo crear el lienzo GIF.'
            );
        }

        $transparent =
            imagecolorallocate(
                $gif,
                255,
                0,
                255
            );

        imagefill(
            $gif,
            0,
            0,
            $transparent
        );

        imagecolortransparent(
            $gif,
            $transparent
        );

        $palette = [];

        try {
            for ($y = 0; $y < $height; $y++) {
                for ($x = 0; $x < $width; $x++) {
                    $rgba = $this->rgbaAt(
                        $image,
                        $x,
                        $y
                    );

                    if ($rgba['alpha'] >= 64) {
                        imagesetpixel(
                            $gif,
                            $x,
                            $y,
                            $transparent
                        );

                        continue;
                    }

                    $r = (int) $rgba['red'];
                    $g = (int) $rgba['green'];
                    $b = (int) $rgba['blue'];

                    $key =
                        $r . ':' .
                        $g . ':' .
                        $b;

                    if (
                        ! array_key_exists(
                            $key,
                            $palette
                        )
                    ) {
                        $index =
                            imagecolorallocate(
                                $gif,
                                $r,
                                $g,
                                $b
                            );

                        if ($index === false) {
                            $index =
                                imagecolorclosest(
                                    $gif,
                                    $r,
                                    $g,
                                    $b
                                );
                        }

                        $palette[$key] =
                            $index;
                    }

                    imagesetpixel(
                        $gif,
                        $x,
                        $y,
                        $palette[$key]
                    );
                }
            }

            ob_start();

            try {
                if (! imagegif($gif)) {
                    throw new RuntimeException(
                        'No se pudo generar el GIF de la placa.'
                    );
                }

                $bytes = ob_get_contents();

                if (
                    ! is_string($bytes) ||
                    $bytes === '' ||
                    ! str_starts_with(
                        $bytes,
                        'GIF8'
                    )
                ) {
                    throw new RuntimeException(
                        'El GIF generado no es válido.'
                    );
                }

                return $bytes;
            } finally {
                ob_end_clean();
            }
        } finally {
            imagedestroy($gif);
        }
    }

    private function normalizeDetail(
        int $detail
    ): int {
        return match (true) {
            $detail <= 96 => 96,
            $detail >= 240 => 240,
            default => 160,
        };
    }
}
