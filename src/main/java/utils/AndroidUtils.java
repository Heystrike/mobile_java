package utils;

import driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitários específicos para Android
 */
public class AndroidUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(AndroidUtils.class);
    
    // Seletores para popups comuns
    private static final By CANCEL_BUTTON = By.id("com.android.contacts:id/left_button");
    private static final By ADD_ACCOUNT_BUTTON = By.id("com.android.contacts:id/right_button");
    private static final By ACCOUNT_DIALOG_TEXT = By.id("com.android.contacts:id/text");
    
    /**
     * Lida com popups iniciais do Android (como criação de conta Google)
     */
    public static void handleInitialPopups() {
        logger.info("Verificando popups iniciais do Android...");
        
        try {
            // Verificar se existe o popup de conta Google
            if (ElementUtils.isElementPresent(ACCOUNT_DIALOG_TEXT, 3)) {
                logger.info("Popup de conta Google detectado, clicando em CANCEL...");
                
                if (ElementUtils.isElementPresent(CANCEL_BUTTON, 2)) {
                    ElementUtils.click(CANCEL_BUTTON);
                    logger.info("Popup de conta cancelado com sucesso");
                    
                    // Aguardar um pouco para a UI se estabilizar
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    logger.warn("Botão CANCEL não encontrado no popup");
                }
            } else {
                logger.info("Nenhum popup inicial detectado");
            }
        } catch (Exception e) {
            logger.warn("Erro ao verificar popups iniciais: {}", e.getMessage());
        }
    }
    
    /**
     * Navega para o app de contatos
     */
    public static void navigateToContacts() {
        logger.info("Navegando para o app de contatos...");
        
        try {
            // Primeiro verificar se já estamos no app de contatos
            By contactsTitle = By.xpath("//android.widget.TextView[@text='Contacts' or @text='Contatos']");
            
            if (!ElementUtils.isElementPresent(contactsTitle, 2)) {
                // Se não estamos no app, tentar abrir via startActivity
                logger.info("App de contatos não está ativo, tentando abrir...");
                
                // Usar AndroidDriver para iniciar a activity
                io.appium.java_client.android.AndroidDriver androidDriver = 
                    (io.appium.java_client.android.AndroidDriver) DriverManager.getDriver();
                androidDriver.startActivity(
                    new io.appium.java_client.android.Activity("com.android.contacts", ".activities.PeopleActivity")
                );
                
                // Aguardar o app carregar
                ElementUtils.waitForElementPresent(contactsTitle, 10);
                logger.info("App de contatos aberto com sucesso");
            } else {
                logger.info("App de contatos já está ativo");
            }
            
            // Lidar com popups após abrir o app
            handleInitialPopups();
            
        } catch (Exception e) {
            logger.error("Erro ao navegar para o app de contatos: {}", e.getMessage());
            throw new RuntimeException("Falha ao abrir app de contatos", e);
        }
    }
}
