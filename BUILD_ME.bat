@echo off
setlocal
cd /d "%~dp0"
set GRADLE_VERSION=9.2.1
set CACHE_DIR=%CD%\.local-gradle
set GRADLE_DIR=%CACHE_DIR%\gradle-%GRADLE_VERSION%
set ZIP=%CACHE_DIR%\gradle-%GRADLE_VERSION%-bin.zip

where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found. Install/use Java 21, then run this again.
  exit /b 1
)

if not exist "%GRADLE_DIR%\bin\gradle.bat" (
  echo Downloading Gradle %GRADLE_VERSION% locally ^(no admin install needed^)...
  if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
  powershell -NoProfile -Command "Invoke-WebRequest -UseBasicParsing 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
  if errorlevel 1 exit /b 1
  powershell -NoProfile -Command "Expand-Archive -Force '%ZIP%' '%CACHE_DIR%'"
  if errorlevel 1 exit /b 1
)

call "%GRADLE_DIR%\bin\gradle.bat" build
if errorlevel 1 exit /b 1

echo.
echo Build finished. Open build\libs\ for underworld-0.1.0.jar
endlocal
