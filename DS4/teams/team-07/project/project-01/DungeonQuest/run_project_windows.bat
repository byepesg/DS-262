@echo off
setlocal

set "PROJECT_DIR=%~dp0"
if not exist "%PROJECT_DIR%project.godot" (
    echo Error: No se encontro project.godot junto a este script.
    pause
    exit /b 1
)

set "GODOT_EXE="
if exist "%PROJECT_DIR%.godot-path" (
    set /p "GODOT_EXE="<"%PROJECT_DIR%.godot-path"
)

if not defined GODOT_EXE (
    where.exe godot.exe >nul 2>nul
    if not errorlevel 1 set "GODOT_EXE=godot.exe"
)

if not defined GODOT_EXE (
    echo Godot no se encontro en el PATH.
    set /p "GODOT_EXE=Escribe la ruta completa a Godot.exe, sin comillas: "
)

if not exist "%GODOT_EXE%" (
    echo Error: No se encontro Godot.exe en la ruta indicada.
    pause
    exit /b 1
)

echo Iniciando Dungeon Quest...
"%GODOT_EXE%" --path "%PROJECT_DIR%."
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" echo Godot termino con el codigo %EXIT_CODE%.
pause
exit /b %EXIT_CODE%
