<?php

namespace App\Models\Community\Staff;

use App\Models\User;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class WebsiteTeam extends Model
{
    protected $guarded = [];

    public function users(): BelongsToMany
    {
        return $this->belongsToMany(
            User::class,
            'user_website_team',
            'website_team_id',
            'user_id'
        )
            ->withPivot([
                'assigned_by_user_id',
                'created_at',
                'updated_at',
            ]);
    }

    public function getBadgePath(): string
    {
        return sprintf(
            '%s%s.gif',
            setting('badges_path'),
            $this->getBadgeName()
        );
    }

    public function getBadgeName(): string
    {
        return $this->badge ?: '';
    }
}
