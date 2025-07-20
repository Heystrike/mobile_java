package factory;

import pages.android.AdicionarContatoPageAndroid;
import pages.android.ContatosMainPageAndroid;
import pages.android.DetalhesContatoPageAndroid;
import pages.ios.AdicionarContatoPageIOS;
import pages.ios.ContatosMainPageIOS;
import pages.ios.DetalhesContatoPageIOS;
import utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory para criação de páginas específicas por plataforma
 * Utiliza o padrão Factory Method para retornar a página correta
 */
public class PageFactory {
    private static final Logger logger = LoggerFactory.getLogger(PageFactory.class);
    private static final String ANDROID = "android";
    private static final String IOS = "ios";
    
    /**
     * Cria a página principal de contatos baseada na plataforma
     */
    public static Object createContatosMainPage() {
        String platform = ConfigManager.getInstance().getPlatform();
        
        switch (platform.toLowerCase()) {
            case ANDROID:
                logger.info("Criando ContatosMainPageAndroid");
                return new ContatosMainPageAndroid();
            case IOS:
                logger.info("Criando ContatosMainPageIOS");
                return new ContatosMainPageIOS();
            default:
                throw new IllegalArgumentException("Plataforma não suportada: " + platform);
        }
    }
    
    /**
     * Cria a página de adicionar contato baseada na plataforma
     */
    public static Object createAdicionarContatoPage() {
        String platform = ConfigManager.getInstance().getPlatform();
        
        switch (platform.toLowerCase()) {
            case ANDROID:
                logger.info("Criando AdicionarContatoPageAndroid");
                return new AdicionarContatoPageAndroid();
            case IOS:
                logger.info("Criando AdicionarContatoPageIOS");
                return new AdicionarContatoPageIOS();
            default:
                throw new IllegalArgumentException("Plataforma não suportada: " + platform);
        }
    }
    
    /**
     * Cria a página de detalhes do contato baseada na plataforma
     */
    public static Object createDetalhesContatoPage() {
        String platform = ConfigManager.getInstance().getPlatform();
        
        switch (platform.toLowerCase()) {
            case ANDROID:
                logger.info("Criando DetalhesContatoPageAndroid");
                return new DetalhesContatoPageAndroid();
            case IOS:
                logger.info("Criando DetalhesContatoPageIOS");
                return new DetalhesContatoPageIOS();
            default:
                throw new IllegalArgumentException("Plataforma não suportada: " + platform);
        }
    }
}