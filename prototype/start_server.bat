@echo off
echo ========================================
echo   Запуск локального сервера для прототипа
echo ========================================
echo.
echo Сервер будет доступен по адресу:
echo   http://localhost:8000/prototype/
echo.
echo Нажмите Ctrl+C для остановки сервера
echo.
cd /d "%~dp0.."
python -m http.server 8000
pause
