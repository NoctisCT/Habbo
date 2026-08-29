<?php

namespace App\Http\Controllers\Shop;

use App\Http\Controllers\Controller;
use Illuminate\Http\Response;

class LegacyEconomyDisabledController extends Controller
{
    public function __invoke(): Response
    {
        return response(
            'La tienda economica heredada esta desactivada.',
            410
        );
    }
}