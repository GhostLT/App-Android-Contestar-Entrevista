@echo off
chcp 65001 >nul
title Subir Copiloto Nativo Android a GitHub
color 0A

echo ===================================================================
echo     Subiendo Copiloto Nativo Android a GitHub (GhostLT)
echo ===================================================================
echo.

cd /d "%~dp0"

echo [+] Preparando archivos para commit...
git add .
git commit -m "feat: Aplicacion nativa Android completa (SDK 34) con Teleprompter, Speech-to-Text y Gemini" >nul 2>&1

echo [+] Subiendo cambios a GitHub (git@github.com:GhostLT/App-Android-Contestar-Entrevista.git)...
git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================================
    echo  ✅ ¡Proyecto subido exitosamente a GitHub!
    echo  🔗 URL: https://github.com/GhostLT/App-Android-Contestar-Entrevista
    echo ===================================================================
) else (
    echo.
    echo Intentando push mediante HTTPS...
    git push -u https://github.com/GhostLT/App-Android-Contestar-Entrevista.git main
)

echo.
pause
