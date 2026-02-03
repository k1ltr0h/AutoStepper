@echo off
setlocal

REM --- Configuracion de carpetas (relativas al repo) ---
set "projectRoot=%~dp0"
set "sourceFolder=%projectRoot%src"
set "libraryFolder=%projectRoot%lib"
set "buildFolder=%projectRoot%build"
set "classesFolder=%buildFolder%\classes"
set "sourcesListFile=%buildFolder%\sources.txt"

REM --- Validar que exista javac (JDK) ---
where javac >nul 2>nul
if errorlevel 1 (
  echo [ERROR] No se encontro javac. Instala un JDK y asegurete de tenerlo en el PATH.
  exit /b 1
)

REM --- Limpiar build previo ---
if exist "%buildFolder%" rmdir /s /q "%buildFolder%"

REM --- Crear carpetas de salida ---
mkdir "%classesFolder%" >nul 2>nul

REM --- Listar fuentes Java ---
dir /s /b "%sourceFolder%\*.java" > "%sourcesListFile%"

REM --- Compilar ---
javac -encoding UTF-8 -cp "%libraryFolder%\*" -d "%classesFolder%" @"%sourcesListFile%"
if errorlevel 1 (
  echo [ERROR] Fallo la compilacion.
  exit /b 1
)

echo [OK] Compilado en: %classesFolder%
exit /b 0
