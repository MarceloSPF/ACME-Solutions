# 📊 Sistema de Relatórios de Teste - Guia Rápido

## 🚀 Como Gerar Relatórios

### 1. Execute os Testes
```bash
mvn test
```

### 2. Visualize os Relatórios

**Windows:**
```bash
abrir-relatorios.bat
```

**Linux/Mac:**
```bash
./abrir-relatorios.sh
```

**Manual:**
- Abra `target/test-reports/index.html` no navegador

---

## 📁 Onde Encontrar

**Diretório:** `target/test-reports/`

**Arquivos Gerados:**
- `index.html` - Índice com links para todos os relatórios
- `{ClasseTest}-report.html` - Relatório HTML visual
- `{ClasseTest}-report.txt` - Relatório texto

---

## ✅ Status

✅ Sistema implementado e funcionando!
✅ Todas as classes de teste configuradas
✅ Relatórios HTML e TXT gerados automaticamente

---

**Execute `mvn test` e depois `abrir-relatorios.bat` para ver os resultados!** 🎉

