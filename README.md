# 📱 Mobile Contacts Automation

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Appium](https://img.shields.io/badge/Appium-8.6.0-purple.svg)](https://appium.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Automação de testes mobile para aplicativos de contatos Android e iOS utilizando **Appium**, **Java 11** e **TestNG**.

## 🎯 Funcionalidades

- ✅ **Cross-Platform**: Suporte para Android e iOS
- ✅ **Page Object Model**: Arquitetura organizada e escalável  
- ✅ **Factory Pattern**: Criação dinâmica de páginas por plataforma
- ✅ **Builder Pattern**: Construção flexível de objetos de teste
- ✅ **Fluent Interface**: APIs encadeáveis para melhor legibilidade
- ✅ **Logging Estruturado**: Logs detalhados com SLF4J + Logback
- ✅ **Configuração Flexível**: Suporte a múltiplos ambientes e devices
- ✅ **Script Inteligente**: Runner bash com validações automáticas

## 📋 Funcionalidades de Teste

### 📱 Android
- Adicionar novos contatos
- Cancelar operações de adição
- Pesquisar contatos existentes
- Visualizar detalhes de contatos
- Editar e excluir contatos

### 🍎 iOS  
- Adicionar novos contatos (Nome/Sobrenome separados)
- Cancelar operações de adição
- Pesquisar contatos existentes
- Visualizar detalhes de contatos
- Integração com FaceTime e Messages

## 🏗️ Arquitetura do Projeto

```
src/
├── main/java/
│   ├── driver/           # Gerenciamento de drivers Appium
│   ├── factory/          # Factory para criação de páginas
│   ├── model/            # Modelos de dados (Contato)
│   ├── pages/            # Page Objects
│   │   ├── android/      # Páginas específicas Android
│   │   ├── ios/          # Páginas específicas iOS
│   │   └── base/         # Classe base para páginas
│   └── utils/            # Utilitários (ElementUtils, ConfigManager)
└── test/
    ├── java/tests/       # Classes de teste
    │   ├── android/      # Testes Android
    │   ├── ios/          # Testes iOS
    │   └── base/         # Classe base para testes
    └── resources/
        ├── config-global.properties  # Configurações globais
        └── suites/       # Suites TestNG organizadas por módulo
            └── contatos/ # Módulo de contatos
                ├── android-contacts-suite.xml
                ├── ios-contacts-suite.xml
                └── contatos.properties

```

## 🚀 Começando

### 📋 Pré-requisitos

#### Requisitos Básicos
- **Java 11+** - [Download](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 16+** - [Download](https://nodejs.org/)

#### Para Android
- **Android SDK** - [Download](https://developer.android.com/studio)
- **Android Emulator** ou dispositivo físico
- **ADB** configurado no PATH

#### Para iOS (macOS apenas)
- **Xcode 14+** - [App Store](https://apps.apple.com/us/app/xcode/id497799835)
- **iOS Simulator** ou dispositivo físico
- **Xcode Command Line Tools**

### 📦 Instalação

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/mobile_java.git
   cd mobile_java
   ```

2. **Instale Appium:**
   ```bash
   npm install -g appium
   npm install -g appium-doctor
   
   # Verificar instalação
   appium-doctor --android  # Para Android
   appium-doctor --ios      # Para iOS
   ```

3. **Instale dependências Maven:**
   ```bash
   mvn clean compile
   ```

4. **Configure dispositivos:**
   
   **Android:**
   ```bash
   # Listar dispositivos/emuladores
   adb devices
   
   # Iniciar emulador (se necessário)
   emulator -avd [nome_do_avd]
   ```
   
   **iOS:**
   ```bash
   # Listar simuladores
   xcrun simctl list devices
   
   # Iniciar simulador (se necessário)
   open -a Simulator
   ```

## 🎮 Execução dos Testes

### 🚀 Método Rápido (Script Bash)

```bash
# Tornar script executável
chmod +x run-tests.sh

# Android
./run-tests.sh android

# iOS  
./run-tests.sh ios

# Ver ajuda
./run-tests.sh --help
```

### 🔧 Método Avançado (Maven)

```bash
# Iniciar Appium Server
appium --port 4723

# Android
mvn clean test -Pandroid

# iOS
mvn clean test -Pios

# Suite específica
mvn test -Dsuite=android-contacts-suite.xml -Dplatform=android
```

### ⚙️ Configurações Personalizadas

```bash
# Device específico
mvn test -Dplatform=android -Ddevice.name="Pixel_7_API_33"

# Versão específica
mvn test -Dplatform=ios -Dplatform.version="17.2"

# Modo debug
mvn test -Dplatform=android -Dlog.level=DEBUG
```

## 📊 Relatórios

Após a execução, os relatórios estarão disponíveis em:

- **TestNG HTML**: `target/surefire-reports/index.html`
- **TestNG XML**: `target/surefire-reports/testng-results.xml`
- **Logs**: `target/logs/test-execution.log`
- **Screenshots**: `target/screenshots/` (em caso de falhas)

## 🔧 Configuração

### Estrutura de Configurações

O projeto utiliza um sistema de configuração em duas camadas:

1. **Configurações Globais** (`config-global.properties`): Configurações que se aplicam a todos os testes
2. **Configurações por Módulo** (`suites/{modulo}/{modulo}.properties`): Configurações específicas de cada módulo

### config-global.properties
```properties
# Timeouts
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Appium Server
appium.server.url=http://127.0.0.1:4723

# Screenshots
screenshot.on.failure=true
screenshot.path=target/screenshots

# Plataforma padrão
default.platform=android
```

### Configurações por Módulo

**Contatos** (`suites/contatos/contatos.properties`):
```properties
# Configurações específicas para testes de contatos
app.package.android=com.android.contacts
app.activity.android=.activities.PeopleActivity
bundle.id.ios=com.apple.MobileAddressBook

# Dados de teste
test.contact.name=Fábio Fernandes
test.contact.phone=1199999999
```

### Suites TestNG

**Android Suite** (`android-contacts-suite.xml`):
```xml
<suite name="Android Contacts Test Suite">
    <parameter name="platform" value="android"/>
    <parameter name="deviceName" value="Android Emulator"/>
    <parameter name="platformVersion" value="13.0"/>
    <!-- Testes incluídos -->
</suite>
```

**iOS Suite** (`ios-contacts-suite.xml`):
```xml
<suite name="iOS Contacts Test Suite">
    <parameter name="platform" value="ios"/>
    <parameter name="deviceName" value="iPhone 14"/>
    <parameter name="platformVersion" value="16.0"/>
    <!-- Testes incluídos -->
</suite>
```

## 🧪 Exemplos de Teste

### Criar um Contato
```java
@Test
public void testeAdicionarContato() {
    // Criar contato com Builder Pattern
    Contato novoContato = new Contato.Builder()
        .nome("Fábio Fernandes")
        .telefone("(11) 99999-9999") 
        .email("fabio@teste.com")
        .empresa("Empresa Teste")
        .build();
    
    // Factory Pattern para criação de páginas
    ContatosMainPageAndroid mainPage = 
        (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
    
    // Fluent Interface para ações
    mainPage.clicarAdicionarContato()
           .preencherContato(novoContato)
           .salvarContato();
    
    // Validação
    Assert.assertTrue(mainPage.contatoExiste(novoContato.getNome()));
}
```

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| **Java** | 11+ | Linguagem principal |
| **Maven** | 3.6+ | Gerenciamento de dependências |
| **Appium** | 8.6.0 | Automação mobile |
| **Selenium** | 4.15.0 | WebDriver base |
| **TestNG** | 7.8.0 | Framework de testes |
| **SLF4J** | 2.0.9 | Logging API |
| **Logback** | 1.4.11 | Implementação de logs |

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### 📝 Padrões de Código

- Seguir convenções Java
- Documentar métodos públicos
- Usar Page Object Model
- Implementar logs estruturados
- Escrever testes limpos e organizados

## 🐛 Troubleshooting

### Problemas Comuns

**Appium não conecta:**
```bash
# Verificar se o servidor está rodando
curl http://localhost:4723/wd/hub/status

# Reiniciar Appium
pkill -f appium
appium --port 4723
```

**Device não encontrado:**
```bash
# Android
adb devices
adb kill-server && adb start-server

# iOS
xcrun simctl list devices
```

**Testes falham:**
- Verificar logs em `target/logs/`
- Conferir screenshots em `target/screenshots/`
- Validar seletores dos elementos
