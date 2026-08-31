<?php

namespace App\Services;

use App\Exceptions\CreditBridgeException;
use JsonException;
use Socket;

class CreditBridgeClient
{
    public function ping(): array
    {
        return $this->send('creditbridgeping', [
            'ping' => 1,
        ]);
    }

    public function debit(
        int $userId,
        int $amount,
        string $transactionId
    ): array {
        return $this->send('debitcredits', [
            'user_id' => $userId,
            'amount' => $amount,
            'transaction_id' => $transactionId,
        ]);
    }

    public function credit(
        int $userId,
        int $amount,
        string $transactionId
    ): array {
        return $this->send('creditcredits', [
            'user_id' => $userId,
            'amount' => $amount,
            'transaction_id' => $transactionId,
        ]);
    }

    /**
     * @throws CreditBridgeException
     */
    private function send(string $command, array $data): array
    {
        $socket = @socket_create(AF_INET, SOCK_STREAM, SOL_TCP);

        if (! $socket instanceof Socket) {
            throw new CreditBridgeException('No se pudo crear el socket RCON.');
        }

        try {
            @socket_set_option($socket, SOL_SOCKET, SO_RCVTIMEO, [
                'sec' => 3,
                'usec' => 0,
            ]);

            @socket_set_option($socket, SOL_SOCKET, SO_SNDTIMEO, [
                'sec' => 3,
                'usec' => 0,
            ]);

            $ip = (string) setting('rcon_ip');
            $port = (int) setting('rcon_port');

            if (! @socket_connect($socket, $ip, $port)) {
                $error = socket_last_error($socket);

                throw new CreditBridgeException(
                    'No se pudo conectar a RCON: ' . socket_strerror($error)
                );
            }

            try {
                $payload = json_encode([
                    'key' => $command,
                    'data' => $data,
                ], JSON_THROW_ON_ERROR);
            } catch (JsonException $exception) {
                throw new CreditBridgeException(
                    'No se pudo serializar el comando RCON.',
                    previous: $exception
                );
            }

            $length = strlen($payload);
            $writtenTotal = 0;

            while ($writtenTotal < $length) {
                $written = @socket_write(
                    $socket,
                    substr($payload, $writtenTotal),
                    $length - $writtenTotal
                );

                if ($written === false || $written === 0) {
                    $error = socket_last_error($socket);

                    throw new CreditBridgeException(
                        'No se pudo enviar el comando RCON: ' . socket_strerror($error)
                    );
                }

                $writtenTotal += $written;
            }

            $response = '';

            while (true) {
                $chunk = @socket_read($socket, 2048, PHP_BINARY_READ);

                if ($chunk === false) {
                    if ($response === '') {
                        $error = socket_last_error($socket);

                        throw new CreditBridgeException(
                            'No se recibio respuesta RCON: ' . socket_strerror($error)
                        );
                    }

                    break;
                }

                if ($chunk === '') {
                    break;
                }

                $response .= $chunk;
            }

            if ($response === '') {
                throw new CreditBridgeException(
                    'Morningstar no devolvio confirmacion RCON.'
                );
            }

            try {
                $decoded = json_decode(
                    $response,
                    true,
                    512,
                    JSON_THROW_ON_ERROR
                );
            } catch (JsonException $exception) {
                throw new CreditBridgeException(
                    'Morningstar devolvio una respuesta RCON invalida.',
                    previous: $exception
                );
            }

            if (! is_array($decoded) || ! array_key_exists('status', $decoded)) {
                throw new CreditBridgeException(
                    'La respuesta RCON no contiene un estado valido.'
                );
            }

            return [
                'status' => (int) $decoded['status'],
                'message' => (string) ($decoded['message'] ?? ''),
            ];
        } finally {
            @socket_close($socket);
        }
    }
}