package utils;

import driver.DriverManager;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitários específicos para iOS
 */
public class IOSUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(IOSUtils.class);
    
    /**
     * Navega para o app de contatos no iOS
     */
    public static void navegarParaContatos() {
        logger.info("Navegando para o app de contatos...");
        
        // Verificar se o app de contatos já está ativo
        By appContatos = By.xpath("//XCUIElementTypeApplication[@name='Contacts']");
        if (ElementUtils.isElementPresent(appContatos, 3)) {
            logger.info("App de contatos já está ativo");
            return;
        }
        
        // Tentar encontrar o ícone do app de contatos na home screen
        By iconeContatos = By.xpath("//XCUIElementTypeIcon[@name='Contacts']");
        if (ElementUtils.isElementPresent(iconeContatos, 5)) {
            ElementUtils.click(iconeContatos);
            logger.info("App de contatos aberto via ícone");
            return;
        }
        
        logger.warn("Não foi possível navegar para o app de contatos automaticamente");
    }
    
    /**
     * Verifica e trata popups iniciais comuns no iOS
     */
    public static void handleInitialPopups() {
        logger.info("Verificando popups iniciais do iOS...");
        
        // Popup de permissão de contatos
        By permitirContatos = By.xpath("//XCUIElementTypeButton[@name='Allow']");
        if (ElementUtils.isElementPresent(permitirContatos, 3)) {
            ElementUtils.click(permitirContatos);
            logger.info("Permissão de contatos concedida");
            return;
        }
        
        // Popup de "Don't Allow"
        By naoPermitir = By.xpath("//XCUIElementTypeButton[@name=\"Don't Allow\"]");
        if (ElementUtils.isElementPresent(naoPermitir, 3)) {
            // Se aparecer, vamos permitir mesmo assim clicando em Allow se estiver disponível
            if (ElementUtils.isElementPresent(permitirContatos, 1)) {
                ElementUtils.click(permitirContatos);
                logger.info("Permissão de contatos concedida após popup de não permitir");
            }
            return;
        }
        
        // Popup de tour/tutorial do app
        By botaoOK = By.xpath("//XCUIElementTypeButton[@name='OK']");
        if (ElementUtils.isElementPresent(botaoOK, 3)) {
            ElementUtils.click(botaoOK);
            logger.info("Popup de tutorial/OK fechado");
            return;
        }
        
        // Popup de "Continue"
        By botaoContinue = By.xpath("//XCUIElementTypeButton[@name='Continue']");
        if (ElementUtils.isElementPresent(botaoContinue, 3)) {
            ElementUtils.click(botaoContinue);
            logger.info("Popup de continue fechado");
            return;
        }
        
        // Popup de "Skip"
        By botaoSkip = By.xpath("//XCUIElementTypeButton[@name='Skip']");
        if (ElementUtils.isElementPresent(botaoSkip, 3)) {
            ElementUtils.click(botaoSkip);
            logger.info("Popup de skip fechado");
            return;
        }
        
        logger.info("Nenhum popup inicial detectado");
    }
    
    /**
     * Força o fechamento de teclado no iOS
     */
    public static void fecharTeclado() {
        try {
            // Tentar clicar no botão "Done" do teclado
            By botaoDone = By.xpath("//XCUIElementTypeButton[@name='Done']");
            if (ElementUtils.isElementPresent(botaoDone, 2)) {
                ElementUtils.click(botaoDone);
                logger.info("Teclado fechado via botão Done");
                return;
            }
            
            // Tentar clicar no botão "Return"
            By botaoReturn = By.xpath("//XCUIElementTypeButton[@name='Return']");
            if (ElementUtils.isElementPresent(botaoReturn, 2)) {
                ElementUtils.click(botaoReturn);
                logger.info("Teclado fechado via botão Return");
                return;
            }
            
            // Como último recurso, tentar usar o método do Appium para iOS
            try {
                ((io.appium.java_client.ios.IOSDriver) DriverManager.getDriver()).hideKeyboard();
                logger.info("Teclado fechado via hideKeyboard");
            } catch (Exception ex) {
                logger.warn("Método hideKeyboard não disponível ou não funcionou: {}", ex.getMessage());
            }
            
        } catch (Exception e) {
            logger.warn("Não foi possível fechar o teclado: {}", e.getMessage());
        }
    }
    
    /**
     * Verifica se está em um simulador iOS
     */
    public static boolean isSimulator() {
        try {
            String deviceName = DriverManager.getDriver().getCapabilities().getCapability("deviceName").toString();
            return deviceName.toLowerCase().contains("simulator");
        } catch (Exception e) {
            logger.warn("Não foi possível determinar se é simulador: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém a versão do iOS
     */
    public static String getIOSVersion() {
        try {
            return DriverManager.getDriver().getCapabilities().getCapability("platformVersion").toString();
        } catch (Exception e) {
            logger.warn("Não foi possível obter versão do iOS: {}", e.getMessage());
            return "unknown";
        }
    }
}
