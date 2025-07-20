package pages.base;

import driver.DriverManager;
import utils.ElementUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para todas as páginas
 * Contém funcionalidades comuns para Android e iOS
 */
public abstract class BasePage {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public BasePage() {
        if (!isCurrentPage()) {
            logger.warn("Página {} pode não ter carregado corretamente", getClass().getSimpleName());
        }
    }
    
    /**
     * Verifica se está na página atual
     * Deve ser implementado pelas páginas específicas
     */
    public abstract boolean isCurrentPage();
    
    /**
     * Obtém o título da página
     * Deve ser implementado pelas páginas específicas
     */
    public abstract String getPageTitle();
    
    /**
     * Aguarda um elemento carregar para validar que a página carregou
     */
    protected void waitForPageLoad(By locator, String pageName) {
        if (ElementUtils.waitForElementPresent(locator, 10)) {
            logger.info("Página {} carregada com sucesso", pageName);
        } else {
            logger.error("Timeout ao aguardar carregamento da página: {}", pageName);
            throw new RuntimeException("Página não carregou: " + pageName);
        }
    }
    
    /**
     * Aguarda múltiplos elementos (pelo menos um deve estar presente)
     */
    protected boolean waitForAnyElement(By... locators) {
        for (By locator : locators) {
            if (ElementUtils.waitForElementPresent(locator, 5)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica se o driver está ativo
     */
    protected void checkDriverState() {
        if (!DriverManager.isDriverActive()) {
            throw new IllegalStateException("Driver não está ativo");
        }
    }
}
