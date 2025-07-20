package tests.base;

import driver.DriverManager;
import utils.ConfigManager;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para todos os testes
 * Gerencia setup e teardown do driver
 */
public class BaseTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ConfigManager config = ConfigManager.getInstance();
    
    @Parameters({"module", "platform"})
    @BeforeMethod
    public void setUp(@Optional String module, @Optional String platform) {
        logger.info("Parâmetros recebidos - module: {}, platform: {}", module, platform);
        
        // Define a plataforma se fornecida como parâmetro
        if (platform != null && !platform.trim().isEmpty()) {
            System.setProperty("platform", platform);
            logger.info("Plataforma definida via parâmetro: {}", platform);
        }
        
        String currentPlatform = config.getPlatform();
        logger.info("Iniciando teste para plataforma: {}", currentPlatform);
        
        // Carrega configurações específicas do módulo se fornecido
        if (module != null && !module.trim().isEmpty()) {
            config.loadModuleConfig(module);
            logger.info("Módulo configurado: {}", module);
        } else {
            logger.info("Nenhum módulo específico configurado, usando apenas configurações globais");
        }
        
        try {
            DriverManager.createDriver();
            logger.info("Driver criado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao criar driver", e);
            throw new RuntimeException("Falha no setup do teste", e);
        }
    }
    
    @AfterMethod
    public void tearDown() {
        try {
            DriverManager.quitDriver();
            logger.info("Driver finalizado com sucesso");
        } catch (Exception e) {
            logger.warn("Erro ao finalizar driver", e);
        }
    }
}
