package utils;

import driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ElementUtils {
    private static final Logger logger = LoggerFactory.getLogger(ElementUtils.class);
    private static final ConfigManager config = ConfigManager.getInstance();
    
    private static WebDriverWait getWait() {
        int timeout = config.getIntProperty("explicit.wait", 20);
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeout));
    }
    
    public static boolean waitForElementPresent(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isElementPresent(By locator) {
        try {
            DriverManager.getDriver().findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static void clickElement(By locator, String elementName) {
        try {
            WebElement element = getWait().until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            logger.info("Clicou no elemento: {}", elementName);
        } catch (Exception e) {
            logger.error("Erro ao clicar no elemento: {}", elementName, e);
            throw new RuntimeException("Falha ao clicar em: " + elementName, e);
        }
    }
    
    public static void sendKeys(By locator, String text, String elementName) {
        try {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);
            logger.info("Inseriu texto '{}' no elemento: {}", text, elementName);
        } catch (Exception e) {
            logger.error("Erro ao inserir texto no elemento: {}", elementName, e);
            throw new RuntimeException("Falha ao inserir texto em: " + elementName, e);
        }
    }
    
    public static String getText(By locator, String elementName) {
        try {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            String text = element.getText();
            logger.debug("Obteve texto '{}' do elemento: {}", text, elementName);
            return text;
        } catch (Exception e) {
            logger.error("Erro ao obter texto do elemento: {}", elementName, e);
            return "";
        }
    }
    
    public static void waitForElementClickable(By locator, String elementName) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Elemento clicável: {}", elementName);
        } catch (Exception e) {
            logger.error("Elemento não ficou clicável: {}", elementName, e);
            throw new RuntimeException("Elemento não clicável: " + elementName, e);
        }
    }
    
    // Métodos adicionais para compatibilidade com as páginas
    
    public static void click(By locator) {
        clickElement(locator, "Elemento");
    }
    
    public static void clearAndType(By locator, String text) {
        sendKeys(locator, text, "Campo de texto");
    }
    
    public static void clear(By locator) {
        try {
            WebElement element = getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            element.clear();
            logger.info("Campo limpo");
        } catch (Exception e) {
            logger.error("Erro ao limpar campo", e);
            throw new RuntimeException("Falha ao limpar campo", e);
        }
    }
    
    public static String getText(By locator) {
        return getText(locator, "Elemento");
    }
    
    public static boolean isElementPresent(By locator, int timeoutSeconds) {
        return waitForElementPresent(locator, timeoutSeconds);
    }
    
    public static int getElementCount(By locator) {
        try {
            return DriverManager.getDriver().findElements(locator).size();
        } catch (Exception e) {
            logger.error("Erro ao contar elementos", e);
            return 0;
        }
    }
    
    public static java.util.List<WebElement> findElements(By locator) {
        try {
            return DriverManager.getDriver().findElements(locator);
        } catch (Exception e) {
            logger.error("Erro ao encontrar elementos", e);
            return java.util.Collections.emptyList();
        }
    }
    
    public static void swipeDown(By locator) {
        logger.info("Swipe down simulado para elemento");
        // Implementação específica de swipe seria necessária para cada plataforma
    }
    
    public static void swipeUp(By locator) {
        logger.info("Swipe up simulado para elemento");
        // Implementação específica de swipe seria necessária para cada plataforma
    }
}
