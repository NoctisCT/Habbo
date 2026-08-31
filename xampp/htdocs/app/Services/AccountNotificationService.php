<?php

namespace App\Services;

use App\Models\AccountNotification;
use InvalidArgumentException;

class AccountNotificationService
{
    public function send(
        int $accountId,
        string $type,
        string $title,
        string $message,
        ?string $url = null,
        array $data = [],
        ?string $dedupeKey = null
    ): AccountNotification {
        $type = trim($type);
        $title = trim($title);
        $message = trim($message);
        $url = $url !== null ? trim($url) : null;
        $dedupeKey = $dedupeKey !== null
            ? trim($dedupeKey)
            : null;

        if ($accountId <= 0) {
            throw new InvalidArgumentException(
                'La cuenta destinataria no es válida.'
            );
        }

        if ($type === '') {
            throw new InvalidArgumentException(
                'El tipo de notificación no puede estar vacío.'
            );
        }

        if ($title === '') {
            throw new InvalidArgumentException(
                'El título de la notificación no puede estar vacío.'
            );
        }

        if ($message === '') {
            throw new InvalidArgumentException(
                'El mensaje de la notificación no puede estar vacío.'
            );
        }

        $attributes = [
            'account_id' => $accountId,
            'type' => mb_substr($type, 0, 100),
            'title' => mb_substr($title, 0, 160),
            'message' => $message,
            'url' => $url !== '' ? $url : null,
            'data' => $data !== [] ? $data : null,
            'dedupe_key' => $dedupeKey !== ''
                ? $dedupeKey
                : null,
        ];

        if ($attributes['dedupe_key'] !== null) {
            return AccountNotification::query()->firstOrCreate(
                [
                    'account_id' => $accountId,
                    'dedupe_key' => $attributes['dedupe_key'],
                ],
                $attributes
            );
        }

        return AccountNotification::query()->create($attributes);
    }
}
