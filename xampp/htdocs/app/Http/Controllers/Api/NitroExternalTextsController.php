<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;
use RuntimeException;

class NitroExternalTextsController extends Controller
{
    public function __invoke(): JsonResponse
    {
        $path = public_path(
            'nitro-assets/gamedata/ExternalTexts.json'
        );

        if (! is_file($path)) {
            throw new RuntimeException(
                'No se encuentra el ExternalTexts base de Nitro.'
            );
        }

        $raw = file_get_contents($path);

        if (! is_string($raw)) {
            throw new RuntimeException(
                'No se pudo leer el ExternalTexts base de Nitro.'
            );
        }

        $texts = json_decode(
            $raw,
            true,
            512,
            JSON_THROW_ON_ERROR
        );

        if (
            ! is_array($texts) ||
            array_is_list($texts)
        ) {
            throw new RuntimeException(
                'ExternalTexts.json no tiene formato de objeto.'
            );
        }

        $badges = DB::table('creator_badges')
            ->join(
                'badge_submissions',
                'badge_submissions.id',
                '=',
                'creator_badges.badge_submission_id'
            )
            ->where(
                'badge_submissions.status',
                'approved'
            )
            ->get([
                'creator_badges.badge_code',
                'creator_badges.badge_name',
                'creator_badges.badge_description',
            ]);

        $websiteTexts = collect();

        if (Schema::hasTable('website_badges')) {
            $keys = $badges
                ->map(
                    fn ($badge) =>
                        'badge_desc_' .
                        strtoupper(
                            (string)
                            $badge->badge_code
                        )
                )
                ->values()
                ->all();

            if ($keys !== []) {
                $websiteTexts = DB::table(
                    'website_badges'
                )
                    ->whereIn(
                        'badge_key',
                        $keys
                    )
                    ->get([
                        'badge_key',
                        'badge_name',
                        'badge_description',
                    ])
                    ->keyBy('badge_key');
            }
        }

        foreach ($badges as $badge) {
            $code = strtoupper(
                trim(
                    (string)
                    $badge->badge_code
                )
            );

            if ($code === '') {
                continue;
            }

            $websiteKey =
                'badge_desc_' .
                $code;

            $website =
                $websiteTexts->get(
                    $websiteKey
                );

            $name =
                $website
                    ? (string)
                        $website->badge_name
                    : (string)
                        $badge->badge_name;

            $description =
                $website
                    ? (string)
                        $website->badge_description
                    : (string)
                        $badge->badge_description;

            $texts[
                'badge_name_' . $code
            ] = $name;

            $texts[
                'badge_desc_' . $code
            ] = $description;
        }

        return response()
            ->json(
                $texts,
                200,
                [],
                JSON_UNESCAPED_UNICODE |
                JSON_UNESCAPED_SLASHES
            )
            ->header(
                'Cache-Control',
                'no-store, no-cache, must-revalidate, max-age=0'
            )
            ->header(
                'Pragma',
                'no-cache'
            )
            ->header(
                'Expires',
                '0'
            );
    }
}
