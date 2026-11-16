param(
    [string]$Jdk = $env:JAVA_HOME,
    [switch]$SoloMotor
)

$ErrorActionPreference = 'Stop'
Push-Location -LiteralPath $PSScriptRoot
try {
    $javac = if ($Jdk) { Join-Path $Jdk 'bin/javac.exe' } else { (Get-Command javac.exe).Source }
    $binJdk = Split-Path -Parent $javac
    $build = Join-Path $PSScriptRoot 'build'
    $classes = Join-Path $build ('classes-' + [Guid]::NewGuid().ToString('N'))
    $sources = Get-ChildItem -LiteralPath src -Filter '*.java' -Recurse -File | ForEach-Object FullName

    & $javac --release 17 -encoding UTF-8 -d $classes $sources
    if ($LASTEXITCODE) { throw 'Falló la compilación del motor.' }
    & (Join-Path $binJdk 'jar.exe') --create --file motor.jar -C $classes .
    if ($LASTEXITCODE) { throw 'Falló el empaquetado del motor.' }

    if (-not (Test-Path -LiteralPath runtime/bin/java.exe)) {
        & (Join-Path $binJdk 'jlink.exe') --add-modules java.base --strip-debug --no-header-files --no-man-pages --compress=zip-6 --output runtime
        if ($LASTEXITCODE) { throw 'Falló la preparación del entorno Java.' }
    }

    if (-not $SoloMotor) {
        python -m PyInstaller --noconfirm --noconsole --onefile --name Pandemic --icon "$PSScriptRoot/pandemic.ico" --add-data "$PSScriptRoot/motor.jar:." --add-data "$PSScriptRoot/runtime:runtime" --add-data "$PSScriptRoot/pandemic.ico:." --add-data "$PSScriptRoot/musica_menu.mp3:." --specpath $build --workpath $build/pyinstaller --distpath dist pandemic.py
        if ($LASTEXITCODE) { throw 'Falló el empaquetado del ejecutable.' }
    }
}
finally {
    Pop-Location
}
