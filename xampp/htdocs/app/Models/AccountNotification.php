<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AccountNotification extends Model
{
    protected $table = 'account_notifications';

    protected $fillable = [
        'account_id',
        'type',
        'title',
        'message',
        'url',
        'data',
        'dedupe_key',
        'read_at',
    ];

    protected $casts = [
        'account_id' => 'integer',
        'data' => 'array',
        'read_at' => 'datetime',
    ];
}
