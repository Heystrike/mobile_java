#!/bin/bash

# Script para executar testes mobile Android/iOS
# Uso: ./run-tests.sh [android|ios] [suite-opcional]

set -e

# Função para mostrar ajuda
show_help() {
    echo "=== Mobile Test Runner ==="
    echo ""
    echo "Uso: $0 [PLATAFORMA] [MÓDULO]"
    echo ""
    echo "PLATAFORMA:"
    echo "  android    - Executa testes no Android"
    echo "  ios        - Executa testes no iOS"
    echo ""
    echo "MÓDULO (opcional):"
    echo "  contatos     - Testes de contatos (padrão)"
    echo "  [qualquer]   - Qualquer módulo na pasta suites/"
    echo ""
    echo "Exemplos:"
    echo "  $0 android"
    echo "  $0 android contatos"
    echo "  $0 ios contatos"
    echo ""
    echo "DEBUG:"
    echo "  --verbose    - Executar com logs detalhados"
    echo "  --debug      - Executar com debug máximo"
    echo ""
}

# Função para verificar conectividade com Appium
check_appium_connectivity() {
    echo "🔗 Testando conectividade com Appium..."
    
    # Teste básico de conectividade
    local status_response=$(curl -s -w "%{http_code}" http://localhost:4723/status)
    local http_code="${status_response: -3}"
    
    if [ "$http_code" = "200" ]; then
        echo "✅ Appium respondeu corretamente (HTTP 200)"
        echo "📋 Informações do servidor:"
        curl -s http://localhost:4723/status | jq . 2>/dev/null || echo "   (resposta recebida mas não em formato JSON)"
    else
        echo "❌ Appium não está respondendo corretamente (HTTP: $http_code)"
        echo "💡 Diagnóstico:"
        
        # Verificar se o processo está rodando
        if pgrep -f "appium" > /dev/null; then
            echo "   ✅ Processo Appium está rodando"
            echo "   📋 Processos Appium:"
            ps aux | grep appium | grep -v grep | sed 's/^/      /'
        else
            echo "   ❌ Processo Appium não encontrado"
            echo "   💡 Inicie com: appium --port 4723"
        fi
        
        # Verificar se a porta está ocupada
        echo "   🔍 Verificando porta 4723:"
        if lsof -i :4723 > /dev/null 2>&1; then
            echo "      Porta 4723 está em uso por:"
            lsof -i :4723 | sed 's/^/         /'
        else
            echo "      Porta 4723 não está em uso"
        fi
        
        return 1
    fi
}

# Função para verificar pré-requisitos
check_prerequisites() {
    echo "Verificando pré-requisitos..."
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven não encontrado. Instale o Maven primeiro."
        exit 1
    fi
    
    # Verificar Appium
    if ! command -v appium &> /dev/null; then
        echo "❌ Appium não encontrado. Instale o Appium primeiro."
        exit 1
    fi
    
    # Verificar se Appium está rodando
    echo "🔍 Verificando status do Appium server..."
    if ! check_appium_connectivity; then
        echo "❌ Appium server não está acessível"
        echo "   Inicie o Appium server: appium --port 4723"
        echo "   Ou verifique se está rodando em outra porta"
        read -p "Pressione Enter para continuar ou Ctrl+C para cancelar..."
    fi
    
    echo "✅ Pré-requisitos verificados"
}

# Função para verificar dispositivos Android
check_android_devices() {
    echo "📱 Verificando dispositivos Android..."
    
    if ! command -v adb &> /dev/null; then
        echo "❌ ADB não encontrado"
        echo "💡 Instale Android SDK e adicione platform-tools ao PATH"
        exit 1
    fi
    
    local devices=$(adb devices | grep -E "device$|emulator")
    
    if [ -z "$devices" ]; then
        echo "❌ Nenhum dispositivo Android conectado"
        echo "💡 Conecte um dispositivo ou inicie um emulador"
        echo "📋 Para listar emuladores: emulator -list-avds"
        echo "🚀 Para iniciar emulador: emulator @<nome_do_avd>"
        exit 1
    fi
    
    echo "✅ Dispositivos Android conectados:"
    echo "$devices"
    
    # Verificar se dispositivos estão responsivos
    echo "� Testando responsividade dos dispositivos..."
    while IFS= read -r line; do
        if [[ $line =~ ^([a-zA-Z0-9._:-]+)[[:space:]]+device$ ]]; then
            local device_id="${BASH_REMATCH[1]}"
            if adb -s "$device_id" shell echo "test" >/dev/null 2>&1; then
                echo "✅ $device_id está responsivo"
            else
                echo "⚠️  $device_id não está responsivo"
            fi
        fi
    done <<< "$devices"
    
    echo ""
}

# Função para verificar simuladores iOS
# Verificar se há simuladores iOS disponíveis  
check_ios_simulators() {
    echo "📱 Verificando simuladores iOS..."
    
    if ! command -v xcrun &> /dev/null; then
        echo "❌ Xcode Command Line Tools não encontradas"
        echo "� Para instalar: xcode-select --install"
        exit 1
    fi
    
    local simulators=$(xcrun simctl list devices | grep -E "(iPhone|iPad)" | grep "Booted\|Shutdown")
    
    if [ -z "$simulators" ]; then
        echo "❌ Nenhum simulador iOS encontrado"
        echo "💡 Para listar simuladores: xcrun simctl list devices"
        exit 1
    fi
    
    echo "✅ Simuladores iOS disponíveis:"
    echo "$simulators" | head -5
    echo ""
}

# Verificar dependências gerais
check_prerequisites() {
    echo "🔧 Verificando pré-requisitos..."
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        echo "❌ Java não encontrado"
        echo "💡 Instale Java 11+ e configure JAVA_HOME"
        exit 1
    fi
    echo "✅ Java: $(java -version 2>&1 | head -1)"
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven não encontrado"
        echo "� Instale Maven 3.6+ e adicione ao PATH"
        exit 1
    fi
    echo "✅ Maven: $(mvn -version | head -1 | cut -d' ' -f3)"
    
    # Verificar conectividade com Appium
    check_appium_connectivity
    
    echo ""
}

# Função principal para executar testes
run_tests() {
    local platform=$1
    local module=${2:-"contatos"}
    local debug_args="$3 $4 $5"  # Capturar argumentos extras de debug
    
    echo "=== Executando testes $platform - módulo: $module ==="
    
    # Verificar pré-requisitos comuns
    check_prerequisites
    
    # Verificar dispositivos específicos da plataforma
    case $platform in
        android)
            check_android_devices
            ;;
        ios)
            check_ios_simulators
            ;;
    esac
    
    # Mapeamento de compatibilidade (inglês -> português)
    case $module in
        contacts)
            module="contatos"
            ;;
    esac
    
    # Definir suite TestNG baseada na plataforma e módulo
    local testng_suite="${platform}-${module}-suite.xml"
    local suite_path="suites/${module}/${testng_suite}"
    
    # Verificar se a suite existe
    if [ ! -f "src/test/resources/${suite_path}" ]; then
        echo "❌ Suite não encontrada: ${suite_path}"
        echo "📁 Estrutura atual do projeto:"
        if [ -d "src/test/resources/suites" ]; then
            echo "   Módulos disponíveis:"
            ls -la src/test/resources/suites/ | grep "^d" | awk '{print "     " $9}' | grep -v "^\s*$"
            echo ""
            echo "   Suites por módulo:"
            for dir in src/test/resources/suites/*/; do
                if [ -d "$dir" ]; then
                    module_name=$(basename "$dir")
                    echo "     $module_name:"
                    ls "$dir"*.xml 2>/dev/null | sed 's|.*/||; s/^/       /' || echo "       (nenhuma suite encontrada)"
                fi
            done
        else
            echo "     Pasta suites/ não encontrada!"
        fi
        echo ""
        echo "💡 Para criar o módulo '$module':"
        echo "   1. mkdir -p src/test/resources/suites/$module"
        echo "   2. Crie o arquivo: src/test/resources/suites/$module/$testng_suite"
        echo "   3. Crie o arquivo: src/test/resources/suites/$module/$module.properties"
        exit 1
    fi
    
    # Executar testes
    echo "🚀 Iniciando execução dos testes..."
    echo "Plataforma: $platform"
    echo "Módulo: $module"
    echo "TestNG Suite: $suite_path"
    echo ""
    
    # Verificar configurações antes de executar
    echo "🔧 Verificando configurações..."
    if [ -f "src/test/resources/config-global.properties" ]; then
        echo "✅ Configuração global encontrada"
    else
        echo "❌ config-global.properties não encontrado"
    fi
    
    if [ -f "src/test/resources/suites/$module/$module.properties" ]; then
        echo "✅ Configuração do módulo '$module' encontrada"
    else
        echo "⚠️  Configuração específica do módulo '$module' não encontrada"
    fi
    
    echo ""
    echo "⏳ Executando Maven com logs detalhados..."
    echo "================================================================"
    
    # Definir nível de logging baseado nos parâmetros
    local maven_args=""
    if [[ "$debug_args" == *"--debug"* ]]; then
        maven_args="-X -e"
        echo "🐛 Modo DEBUG ativado - logs máximos"
    elif [[ "$debug_args" == *"--verbose"* ]]; then
        maven_args="-e"
        echo "📝 Modo VERBOSE ativado - logs detalhados"
    else
        maven_args="-q"
        echo "📋 Modo padrão - logs resumidos"
    fi
    
    # Executar Maven com logging apropriado
    set +e  # Temporariamente desabilitar exit no erro para capturar o código
    mvn clean test \
        -Dplatform=$platform \
        -Dmodule=$module \
        -Dsuite=$suite_path \
        -Dmaven.test.failure.ignore=true \
        $maven_args \
        2>&1 | tee target/maven-execution.log
    
    local exit_code=$?
    set -e  # Reabilitar exit no erro
    
    echo "================================================================"
    echo ""
    
    # Mostrar resultados
    echo ""
    echo "=== Resultados dos Testes ==="
    
    # Verificar falhas nos logs do Maven primeiro
    local maven_failed=false
    if [ -f "target/maven-execution.log" ]; then
        if grep -q "BUILD FAILURE\|ERROR.*Tests run.*Failures.*[1-9]" target/maven-execution.log; then
            maven_failed=true
        fi
    fi
    
    # Verificar falhas nos relatórios do TestNG
    local testng_failed=false
    if [ -f "target/surefire-reports/testng-results.xml" ]; then
        if grep -q 'failed="[1-9]"\|errors="[1-9]"' target/surefire-reports/testng-results.xml; then
            testng_failed=true
        fi
    fi
    
    # Determinar se houve falhas reais
    if [ $exit_code -eq 0 ] && [ "$maven_failed" = false ] && [ "$testng_failed" = false ]; then
        echo "✅ Testes executados com sucesso!"
    else
        echo "❌ Testes falharam"
        if [ $exit_code -ne 0 ]; then
            echo "   Exit code do Maven: $exit_code"
        fi
        if [ "$maven_failed" = true ]; then
            echo "   Falhas detectadas nos logs do Maven"
        fi
        if [ "$testng_failed" = true ]; then
            echo "   Falhas detectadas nos relatórios do TestNG"
        fi
        
        # Forçar exit code de falha
        exit_code=1
        echo ""
        echo "🔍 Análise de Falhas:"
        
        # Verificar se há logs do Maven
        if [ -f "target/maven-execution.log" ]; then
            echo "📋 Últimas linhas do log do Maven:"
            tail -20 target/maven-execution.log | grep -E "(ERROR|FAILURE|Exception|at )" || echo "   Nenhum erro específico encontrado nos logs"
            echo ""
        fi
        
        # Verificar se há relatórios do TestNG
        if [ -f "target/surefire-reports/testng-results.xml" ]; then
            echo "📊 Resumo dos testes do TestNG:"
            grep -E "(failed|error|skip)" target/surefire-reports/testng-results.xml | head -5 || echo "   Arquivo de resultados encontrado mas sem detalhes de falhas"
            echo ""
        fi
        
        # Verificar logs de falhas específicas
        if [ -d "target/surefire-reports" ]; then
            echo "📁 Arquivos de relatório disponíveis:"
            ls -la target/surefire-reports/ | grep -E "\.(txt|xml)$" | awk '{print "   " $9}'
            echo ""
            
            # Mostrar conteúdo dos arquivos .txt (stacktraces)
            echo "🔥 Stacktraces das falhas:"
            for txt_file in target/surefire-reports/*.txt; do
                if [ -f "$txt_file" ] && [ -s "$txt_file" ]; then
                    echo "   Arquivo: $(basename "$txt_file")"
                    cat "$txt_file" | head -15
                    echo "   ..."
                    echo ""
                fi
            done
        fi
        
        # Sugestões para resolução
        echo "💡 Sugestões para resolução:"
        echo "   1. Verifique se o Appium server está rodando: curl http://localhost:4723/status"
        echo "   2. Verifique se o dispositivo/emulador está conectado: adb devices"
        echo "   3. Verifique os logs completos: cat target/maven-execution.log"
        echo "   4. Verifique as configurações em: src/test/resources/config-global.properties"
        echo "   5. Verifique as configurações do módulo: src/test/resources/suites/$module/$module.properties"
    fi
    
    # Mostrar localização dos relatórios
    echo ""
    echo "📊 Relatórios disponíveis em:"
    echo "   target/surefire-reports/index.html"
    echo "   target/surefire-reports/testng-results.xml"
    echo "   target/maven-execution.log (log completo da execução)"
    
    if [ $exit_code -ne 0 ]; then
        echo ""
        echo "🚨 Para debug detalhado, execute:"
        echo "   tail -50 target/maven-execution.log"
        echo "   cat target/surefire-reports/*.txt"
    fi
    
    # Sempre retornar o exit code correto baseado nas falhas detectadas
    return $exit_code
}

# Função principal
main() {
    # Verificar argumentos
    if [ $# -eq 0 ] || [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_help
        exit 0
    fi
    
    local platform=$1
    local suite=$2
    
    # Validar plataforma
    case $platform in
        android|ios)
            run_tests $platform $suite
            ;;
        *)
            echo "❌ Plataforma inválida: $platform"
            echo "Use: android ou ios"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Executar função principal com todos os argumentos
main "$@"
