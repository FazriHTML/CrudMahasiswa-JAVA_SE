@echo off
echo ===============================
echo   RUNNING FROM VS CODE TERMINAL
echo ===============================
echo.

echo Compiling...
javac -cp ".;lib/mysql-connector-j-9.4.0.jar" *.java

if %errorlevel% equ 0 (
    echo.
    echo Running program...
    java -cp ".;lib/mysql-connector-j-9.4.0.jar" CrudMahasiswa
) else (
    echo.
    echo COMPILE FAILED!
)

pause