@echo off
echo ========================================
echo Executando Testes - ACME Solutions
echo ========================================
echo.

echo Executando todos os testes...
call mvn test

echo.
echo ========================================
echo Testes concluidos!
echo ========================================
pause

