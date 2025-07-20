package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gerenciador de configurações
 * Carrega configurações globais e específicas por módulo
 */
public class ConfigManager {
    private static ConfigManager instance;
    private Properties globalProperties;
    private Properties moduleProperties;
    private String platform;
    private String currentModule;
    
    private ConfigManager() {
        loadGlobalConfig();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadGlobalConfig() {
        // Pega plataforma do system property ou padrão
        platform = System.getProperty("platform", "android").toLowerCase();
        
        globalProperties = new Properties();
        // Carrega o arquivo de configuração global
        String globalConfigFile = "/config-global.properties";
        
        try (InputStream input = getClass().getResourceAsStream(globalConfigFile)) {
            if (input != null) {
                globalProperties.load(input);
            } else {
                throw new RuntimeException("Arquivo de configuração global não encontrado: " + globalConfigFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar configurações globais", e);
        }
    }
    
    /**
     * Carrega configurações específicas de um módulo
     * @param moduleName Nome do módulo (ex: "contatos")
     */
    public void loadModuleConfig(String moduleName) {
        if (moduleName == null || moduleName.trim().isEmpty()) {
            return;
        }
        
        currentModule = moduleName;
        moduleProperties = new Properties();
        String moduleConfigFile = "/suites/" + moduleName + "/" + moduleName + ".properties";
        
        try (InputStream input = getClass().getResourceAsStream(moduleConfigFile)) {
            if (input != null) {
                moduleProperties.load(input);
                System.out.println("Configurações do módulo '" + moduleName + "' carregadas com sucesso");
            } else {
                System.out.println("Arquivo de configuração do módulo não encontrado: " + moduleConfigFile);
                moduleProperties = null;
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações do módulo " + moduleName + ": " + e.getMessage());
            moduleProperties = null;
        }
    }
    
    public String getPlatform() {
        // Sempre verifica o system property para mudanças em runtime
        String currentPlatform = System.getProperty("platform", "android").toLowerCase();
        if (!currentPlatform.equals(platform)) {
            platform = currentPlatform;
        }
        return platform;
    }
    
    public String getCurrentModule() {
        return currentModule;
    }
    
    /**
     * Busca uma propriedade primeiro no módulo atual, depois no global
     * @param key Chave da propriedade
     * @return Valor da propriedade ou null se não encontrada
     */
    public String getProperty(String key) {
        // Primeiro tenta no módulo atual
        if (moduleProperties != null && moduleProperties.containsKey(key)) {
            return moduleProperties.getProperty(key);
        }
        // Se não encontrou, busca no global
        return globalProperties.getProperty(key);
    }
    
    /**
     * Busca uma propriedade com valor padrão
     * @param key Chave da propriedade
     * @param defaultValue Valor padrão se não encontrada
     * @return Valor da propriedade ou valor padrão
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Busca propriedade especificamente do módulo atual
     * @param key Chave da propriedade
     * @return Valor da propriedade do módulo ou null
     */
    public String getModuleProperty(String key) {
        return moduleProperties != null ? moduleProperties.getProperty(key) : null;
    }
    
    /**
     * Busca propriedade especificamente do arquivo global
     * @param key Chave da propriedade
     * @return Valor da propriedade global ou null
     */
    public String getGlobalProperty(String key) {
        return globalProperties.getProperty(key);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}
