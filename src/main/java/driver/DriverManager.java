package driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;

/**
 * Gerenciador de drivers Appium
 * Cria drivers para Android e iOS baseado na configuração
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();
    
    public static void createDriver() {
        String platform = config.getPlatform();
        logger.info("Criando driver para plataforma: {}", platform);
        
        try {
            AppiumDriver appiumDriver;
            
            if ("android".equalsIgnoreCase(platform)) {
                appiumDriver = createAndroidDriver();
            } else if ("ios".equalsIgnoreCase(platform)) {
                appiumDriver = createIOSDriver();
            } else {
                throw new IllegalArgumentException("Plataforma não suportada: " + platform);
            }
            
            // Configurações globais
            int implicitWait = config.getIntProperty("implicit.wait", 10);
            appiumDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            
            driver.set(appiumDriver);
            logger.info("Driver criado com sucesso para {}", platform);
            
        } catch (Exception e) {
            logger.error("Erro ao criar driver para {}", platform, e);
            throw new RuntimeException("Falha ao criar driver", e);
        }
    }
    
    private static AndroidDriver createAndroidDriver() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        
        caps.setCapability("platformName", config.getProperty("platform.name.android", "Android"));
        caps.setCapability("platformVersion", config.getProperty("platform.version.android", "9.0"));
        caps.setCapability("deviceName", config.getProperty("device.name.android", "emulator-5556"));
        caps.setCapability("automationName", config.getProperty("automation.name.android", "UiAutomator2"));
        caps.setCapability("appPackage", config.getProperty("app.package.android"));
        caps.setCapability("appActivity", config.getProperty("app.activity.android"));
        
        // Configurações opcionais
        caps.setCapability("noReset", config.getBooleanProperty("no.reset.android", true));
        caps.setCapability("autoGrantPermissions", config.getBooleanProperty("auto.grant.permissions.android", true));
        
        String serverUrl = config.getProperty("appium.server.url", "http://127.0.0.1:4723");
        return new AndroidDriver(new URL(serverUrl), caps);
    }
    
    private static IOSDriver createIOSDriver() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        
        caps.setCapability("platformName", config.getProperty("platform.name.ios", "iOS"));
        caps.setCapability("platformVersion", config.getProperty("platform.version.ios", "17.2"));
        caps.setCapability("deviceName", config.getProperty("device.name.ios", "iPhone 15 Pro"));
        caps.setCapability("automationName", config.getProperty("automation.name.ios", "XCUITest"));
        caps.setCapability("bundleId", config.getProperty("bundle.id.ios"));
        
        // Configurações opcionais
        caps.setCapability("noReset", config.getBooleanProperty("no.reset.ios", true));
        caps.setCapability("autoAcceptAlerts", config.getBooleanProperty("auto.accept.alerts.ios", true));
        
        String serverUrl = config.getProperty("appium.server.url", "http://127.0.0.1:4723");
        return new IOSDriver(new URL(serverUrl), caps);
    }
    
    public static AppiumDriver getDriver() {
        AppiumDriver currentDriver = driver.get();
        if (currentDriver == null) {
            throw new IllegalStateException("Driver não foi inicializado. Chame createDriver() primeiro.");
        }
        return currentDriver;
    }
    
    public static void quitDriver() {
        AppiumDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
            logger.info("Driver finalizado");
        }
    }
    
    public static boolean isDriverActive() {
        return driver.get() != null;
    }
}
