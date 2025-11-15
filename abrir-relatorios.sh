#!/bin/bash

echo "========================================"
echo "Abrindo Relatórios de Teste"
echo "========================================"
echo ""

if [ -f "target/test-reports/index.html" ]; then
    echo "Abrindo índice de relatórios..."
    
    # Detecta o sistema operacional
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        open target/test-reports/index.html
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        xdg-open target/test-reports/index.html 2>/dev/null || \
        gnome-open target/test-reports/index.html 2>/dev/null || \
        echo "Abra manualmente: target/test-reports/index.html"
    else
        echo "Abra manualmente: target/test-reports/index.html"
    fi
else
    echo "Nenhum relatório encontrado. Execute os testes primeiro com: mvn test"
    echo ""
    read -p "Deseja executar os testes agora? (S/N) " resposta
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        mvn test
        if [ -f "target/test-reports/index.html" ]; then
            if [[ "$OSTYPE" == "darwin"* ]]; then
                open target/test-reports/index.html
            elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
                xdg-open target/test-reports/index.html 2>/dev/null
            fi
        fi
    fi
fi

