<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personajes</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background: #17191c;
            color: #ffffff;
            font-family: Arial, sans-serif;
        }

        .wrapper {
            max-width: 1000px;
            margin: 0 auto;
            padding: 50px 20px;
        }

        h1 {
            margin-bottom: 8px;
        }

        .subtitle {
            color: #aeb4bd;
            margin-bottom: 30px;
        }

        .notice {
            padding: 14px;
            margin-bottom: 25px;
            border-radius: 8px;
            background: #166534;
        }

        .errors {
            padding: 14px;
            margin-bottom: 25px;
            border-radius: 8px;
            background: #991b1b;
        }

        .characters {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 20px;
        }

        .character, .create-character {
            background: #24272c;
            border: 1px solid #343940;
            border-radius: 12px;
            padding: 22px;
        }

        .character h2, .create-character h2 {
            margin: 0 0 8px;
        }

        .motto {
            color: #aeb4bd;
            min-height: 40px;
        }

        .primary {
            display: inline-block;
            margin-bottom: 12px;
            padding: 4px 8px;
            border-radius: 6px;
            background: #3b82f6;
            font-size: 12px;
            font-weight: bold;
        }

        .online {
            color: #6ee7b7;
        }

        .offline {
            color: #9ca3af;
        }

        .enter {
            display: block;
            margin-top: 20px;
            padding: 12px;
            text-align: center;
            text-decoration: none;
            color: white;
            background: #2563eb;
            border-radius: 8px;
            font-weight: bold;
        }

        .enter:hover {
            background: #1d4ed8;
        }

        .create-character form {
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin-top: 18px;
        }

        .create-character input {
            box-sizing: border-box;
            width: 100%;
            padding: 12px;
            border: 1px solid #4b5563;
            border-radius: 8px;
            background: #17191c;
            color: white;
        }

        .create-character button {
            padding: 12px;
            border: 0;
            border-radius: 8px;
            background: #16a34a;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        .create-character button:hover {
            background: #15803d;
        }

        .limit {
            color: #9ca3af;
            font-size: 13px;
        }

        .back {
            display: inline-block;
            margin-bottom: 25px;
            color: #93c5fd;
            text-decoration: none;
        }
    </style>
</head>

<body>
    <div class="wrapper">
        <a class="back" href="{{ route('me.show') }}">← Volver al CMS</a>

        <h1>Personajes</h1>

        <div class="subtitle">
            {{ $characters->count() }} / {{ $maxCharacters }} personajes
        </div>

        @if (session('character-created'))
            <div class="notice">
                {{ session('character-created') }}
            </div>
        @endif

        @if ($errors->any())
            <div class="errors">
                @foreach ($errors->all() as $error)
                    <div>{{ $error }}</div>
                @endforeach
            </div>
        @endif

        <div class="characters">
            @foreach ($characters as $character)
                <div class="character">
                    @if ($character->is_primary)
                        <div class="primary">PRINCIPAL</div>
                    @endif

                    <h2>{{ $character->username }}</h2>

                    <div class="motto">
                        {{ $character->motto ?: 'Sin mision' }}
                    </div>

                    @if ((int) $character->online > 0)
                        <div class="online">Online</div>
                    @else
                        <div class="offline">Offline</div>
                    @endif

                    <a
                        class="enter"
                        href="{{ route('nitro-character', ['user' => $character->id]) }}"
                        target="_blank"
                    >
                        Entrar con {{ $character->username }}
                    </a>
                </div>
            @endforeach

            @if ($characters->count() < $maxCharacters)
                <div class="create-character">
                    <h2>+ Crear personaje</h2>

                    <div class="limit">
                        Se añadira directamente a esta cuenta.
                    </div>

                    <form method="POST" action="{{ route('character-create') }}">
                        @csrf

                        <input
                            type="text"
                            name="username"
                            maxlength="25"
                            value="{{ old('username') }}"
                            placeholder="Nombre del personaje"
                            required
                        >

                        <button type="submit">
                            Crear personaje
                        </button>
                    </form>
                </div>
            @endif
        </div>
    </div>
</body>
</html>