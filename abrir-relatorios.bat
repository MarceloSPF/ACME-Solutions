@echo off
echo ========================================
echo Abrindo Relatorios de Teste
echo ========================================
echo.

if exist "target\test-reports\index.html" (
    echo Abrindo indice de relatorios...
    start target\test-reports\index.html
) else (
    echo Nenhum relatorio encontrado. Execute os testes primeiro com: mvn test
    echo.
    echo Deseja executar os testes agora? (S/N)
    set /p resposta=
    if /i "%resposta%"=="S" (
        call mvn test
        if exist "target\test-reports\index.html" (
            start target\test-reports\index.html
        )
    )
)

pause

