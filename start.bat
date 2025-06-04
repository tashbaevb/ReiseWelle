@echo off

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Java ist nicht installiert.
    set /p install_java=Java automatisch installieren mit winget? (y/n):
    if /i "%install_java%"=="y" (
        winget install --id EclipseAdoptium.Temurin.21.JDK -e
    ) else (
        exit /b
    )
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven ist nicht installiert.
    set /p install_maven=Maven automatisch installieren mit winget? (y/n):
    if /i "%install_maven%"=="y" (
        winget install --id Apache.Maven -e
    ) else (
        exit /b
    )
)

mvn clean javafx:run
pause
