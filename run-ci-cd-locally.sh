#!/bin/bash

# Script para simular execução do CI/CD localmente
# Uso: ./run-ci-cd-locally.sh

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log colorido
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

log_info "Iniciando simulação do CI/CD Android Tests localmente..."

# Verificar pré-requisitos
log_info "Verificando pré-requisitos..."

# Verificar Java
if ! command -v java &> /dev/null; then
    log_error "Java não encontrado. Instale Java 11+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    log_error "Java 11+ é necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi
log_success "Java $JAVA_VERSION encontrado"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    log_error "Maven não encontrado"
    exit 1
fi
log_success "Maven encontrado"

# Verificar Android SDK
if [ -z "$ANDROID_HOME" ]; then
    log_error "ANDROID_HOME não configurado"
    log_info "Configure com: export ANDROID_HOME=/path/to/android/sdk"
    exit 1
fi
log_success "ANDROID_HOME configurado: $ANDROID_HOME"

# Verificar ADB
if ! command -v adb &> /dev/null; then
    log_error "ADB não encontrado. Verifique se \$ANDROID_HOME/platform-tools está no PATH"
    exit 1
fi
log_success "ADB encontrado"

# Verificar Node.js
if ! command -v node &> /dev/null; then
    log_error "Node.js não encontrado"
    exit 1
fi
log_success "Node.js encontrado"

# Verificar/Instalar Appium
if ! command -v appium &> /dev/null; then
    log_warning "Appium não encontrado. Instalando..."
    npm install -g appium@2.0.0
    appium driver install uiautomator2
    log_success "Appium instalado"
else
    log_success "Appium encontrado"
fi

# Verificar dispositivos/emuladores
log_info "Verificando dispositivos Android..."
DEVICES=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)
if [ "$DEVICES" -eq 0 ]; then
    log_warning "Nenhum dispositivo/emulador encontrado"
    log_info "Inicie um emulador ou conecte um dispositivo antes de continuar"
    
    # Tentar listar AVDs disponíveis
    if command -v avdmanager &> /dev/null; then
        log_info "AVDs disponíveis:"
        avdmanager list avd | grep "Name:" || log_warning "Nenhum AVD encontrado"
    fi
    
    read -p "Pressione Enter quando um dispositivo estiver conectado..."
    
    # Verificar novamente
    DEVICES=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)
    if [ "$DEVICES" -eq 0 ]; then
        log_error "Ainda nenhum dispositivo encontrado"
        exit 1
    fi
fi

log_success "$DEVICES dispositivo(s) encontrado(s)"
adb devices

# Iniciar Appium se não estiver rodando
log_info "Verificando se Appium está rodando..."
if ! pgrep -f "appium" > /dev/null; then
    log_info "Iniciando Appium..."
    appium --port 4723 --log-level info --log appium-local.log &
    APPIUM_PID=$!
    sleep 5
    log_success "Appium iniciado (PID: $APPIUM_PID)"
else
    log_success "Appium já está rodando"
fi

# Compilar projeto
log_info "Compilando projeto..."
mvn clean compile test-compile
log_success "Compilação concluída"

# Executar testes
log_info "Executando testes Android..."
mvn test -Pandroid \
    -Dplatform=android \
    -Dmodule=contatos \
    -Dsuite=suites/contatos/android-contatos-suite.xml \
    -DfailIfNoTests=false

TEST_RESULT=$?

# Gerar relatório
log_info "Verificando resultados dos testes..."
if [ -d "target/surefire-reports" ]; then
    log_success "Relatórios de teste encontrados:"
    ls -la target/surefire-reports/
    
    # Contar testes
    if [ -f "target/surefire-reports/testng-results.xml" ]; then
        TOTAL=$(grep -o 'total="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        PASSED=$(grep -o 'passed="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        FAILED=$(grep -o 'failed="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        SKIPPED=$(grep -o 'skipped="[0-9]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
        
        log_info "Resumo dos testes:"
        log_info "  Total: $TOTAL"
        log_success "  Passou: $PASSED"
        if [ "$FAILED" -gt 0 ]; then
            log_error "  Falhou: $FAILED"
        else
            log_info "  Falhou: $FAILED"
        fi
        log_info "  Ignorado: $SKIPPED"
    fi
else
    log_warning "Nenhum relatório de teste encontrado"
fi

# Cleanup
if [ ! -z "$APPIUM_PID" ]; then
    log_info "Parando Appium..."
    kill $APPIUM_PID 2>/dev/null || true
fi

# Resultado final
echo ""
if [ $TEST_RESULT -eq 0 ]; then
    log_success "🎉 Execução local do CI/CD concluída com sucesso!"
    log_info "Os testes passaram como esperado no ambiente local"
else
    log_error "💥 Execução local do CI/CD falhou"
    log_info "Verifique os logs acima e os relatórios em target/surefire-reports/"
    exit 1
fi

log_info "Logs do Appium salvos em: appium-local.log"
log_info "Relatórios dos testes em: target/surefire-reports/"
