<?php

namespace App\Services;

use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;
use Throwable;

class EmulatorPresenceService
{
    private const HEARTBEAT_KEY = 'morningstar';
    private const HEARTBEAT_TTL_SECONDS = 15;

    private ?bool $emulatorAlive = null;

    public function __construct(
        private readonly CreditBridgeClient $bridge
    ) {
    }

    public function isEmulatorAlive(): bool
    {
        if ($this->emulatorAlive !== null) {
            return $this->emulatorAlive;
        }

        if ($this->heartbeatIsFresh()) {
            return $this->emulatorAlive = true;
        }

        try {
            $response = $this->bridge->ping();

            if (
                (int) ($response['status'] ?? -1) === 0 &&
                str_starts_with(
                    (string) ($response['message'] ?? ''),
                    'creditbridge|'
                )
            ) {
                return $this->emulatorAlive = true;
            }
        } catch (Throwable) {
            // Heartbeat + ping ausentes: Morningstar se considera apagado.
        }

        return $this->emulatorAlive = false;
    }

    public function effectiveOnlineState(
        int $userId,
        mixed $reportedOnline
    ): bool {
        if ((string) $reportedOnline === '0') {
            return false;
        }

        if ($this->isEmulatorAlive()) {
            return true;
        }

        $this->clearStaleOnlineFlags();

        return false;
    }

    public function normalizeGlobalPresence(): int
    {
        if ($this->isEmulatorAlive()) {
            return 0;
        }

        return $this->clearStaleOnlineFlags();
    }

    public function onlineUserCount(): int
    {
        $this->normalizeGlobalPresence();

        return DB::table('users')
            ->where('online', '1')
            ->count();
    }

    private function heartbeatIsFresh(): bool
    {
        if (! Schema::hasTable('emulator_heartbeats')) {
            return false;
        }

        $lastSeen = DB::table('emulator_heartbeats')
            ->where(
                'heartbeat_key',
                self::HEARTBEAT_KEY
            )
            ->value('last_seen_at');

        if ($lastSeen === null) {
            return false;
        }

        $age = time() - (int) $lastSeen;

        return $age <= self::HEARTBEAT_TTL_SECONDS;
    }

    private function clearStaleOnlineFlags(): int
    {
        return DB::table('users')
            ->where('online', '!=', '0')
            ->update([
                'online' => '0',
            ]);
    }
}
