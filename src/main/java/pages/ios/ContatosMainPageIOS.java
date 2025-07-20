package pages.ios;

import pages.base.BasePage;
import utils.ElementUtils;
import utils.IOSUtils;
import org.openqa.selenium.By;
import java.util.List;

/**
 * Página principal de contatos - iOS
 */
public class ContatosMainPageIOS extends BasePage {
    
    // Seletores específicos do iOS
    private final By botaoAdicionar = By.xpath("//XCUIElementTypeButton[@name='Add']");
    private final By listaContatos = By.xpath("//XCUIElementTypeTable[@name='ContactsList']");
    private final By tituloContatos = By.xpath("//XCUIElementTypeNavigationBar[@name='Contacts']");
    private final By campoPesquisa = By.xpath("//XCUIElementTypeSearchField[@name='Search']");
    private final By botaoPesquisar = By.xpath("//XCUIElementTypeButton[@name='Search']");
    private final By botaoGrupos = By.xpath("//XCUIElementTypeButton[@name='Groups']");
    private final By botaoConfiguracao = By.xpath("//XCUIElementTypeButton[@name='Settings']");
    private final By mensagemSemContatos = By.xpath("//XCUIElementTypeStaticText[@name='No Contacts']");
    
    // Elementos da tela vazia (equivalentes ao Android)
    private final By botaoAdicionarConta = By.xpath("//XCUIElementTypeButton[@name='Add Account']");
    private final By botaoImportarContatos = By.xpath("//XCUIElementTypeButton[@name='Import Contacts']");
    
    public ContatosMainPageIOS() {
        super();
        
        // Verificar se há popup e lidar com ele
        IOSUtils.handleInitialPopups();
        
        // Navegar para o app se necessário
        IOSUtils.navegarParaContatos();
        
        waitForPageLoad(tituloContatos, "Contatos Main iOS");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(tituloContatos, 5) || 
               ElementUtils.isElementPresent(listaContatos, 5);
    }
    
    @Override
    public String getPageTitle() {
        if (ElementUtils.isElementPresent(tituloContatos, 2)) {
            return "Contacts";
        }
        return "Contatos";
    }
    
    /**
     * Clica no botão adicionar contato
     */
    public AdicionarContatoPageIOS clicarAdicionarContato() {
        ElementUtils.click(botaoAdicionar);
        logger.info("Clicado no botão adicionar contato");
        return new AdicionarContatoPageIOS();
    }
    
    /**
     * Pesquisa por um contato pelo nome (se disponível)
     */
    public ContatosMainPageIOS pesquisarContato(String nome) {
        if (!temContatos()) {
            logger.warn("Não é possível pesquisar - lista de contatos está vazia");
            return this;
        }
        
        ElementUtils.click(campoPesquisa);
        ElementUtils.clearAndType(campoPesquisa, nome);
        logger.info("Pesquisando contato: {}", nome);
        return this;
    }
    
    /**
     * Seleciona um contato da lista pelo nome
     */
    public DetalhesContatoPageIOS selecionarContato(String nome) {
        // Tentar múltiplas estratégias para encontrar o contato
        By[] seletoresContato = {
            By.xpath("//XCUIElementTypeCell[contains(@name, '" + nome + "')]"),
            By.xpath("//XCUIElementTypeStaticText[contains(@value, '" + nome + "')]/ancestor::XCUIElementTypeCell"),
            By.xpath("//XCUIElementTypeStaticText[contains(@name, '" + nome + "')]/ancestor::XCUIElementTypeCell"),
            By.xpath("//XCUIElementTypeCell//XCUIElementTypeStaticText[contains(text(), '" + nome + "')]/ancestor::XCUIElementTypeCell")
        };
        
        for (By seletor : seletoresContato) {
            if (ElementUtils.isElementPresent(seletor, 2)) {
                ElementUtils.click(seletor);
                logger.info("Contato '{}' selecionado com seletor: {}", nome, seletor);
                return new DetalhesContatoPageIOS();
            }
        }
        
        throw new RuntimeException("Não foi possível encontrar o contato: " + nome);
    }
    
    /**
     * Verifica se um contato existe na lista
     */
    public boolean contatoExiste(String nome) {
        // Tentar múltiplas estratégias para encontrar o contato
        By[] seletoresContato = {
            By.xpath("//XCUIElementTypeCell[contains(@name, '" + nome + "')]"),
            By.xpath("//XCUIElementTypeStaticText[contains(@value, '" + nome + "')]"),
            By.xpath("//XCUIElementTypeStaticText[contains(@name, '" + nome + "')]"),
            By.xpath("//XCUIElementTypeCell//XCUIElementTypeStaticText[contains(text(), '" + nome + "')]")
        };
        
        for (By seletor : seletoresContato) {
            if (ElementUtils.isElementPresent(seletor, 3)) {
                logger.info("Contato '{}' existe: true (encontrado com seletor: {})", nome, seletor);
                return true;
            }
        }
        
        logger.info("Contato '{}' existe: false", nome);
        return false;
    }
    
    /**
     * Verifica se há contatos na lista
     */
    public boolean temContatos() {
        return !listaSemContatos();
    }
    
    /**
     * Verifica se não há contatos
     */
    public boolean listaSemContatos() {
        boolean semContatos = ElementUtils.isElementPresent(mensagemSemContatos, 3);
        logger.info("Lista sem contatos: {}", semContatos);
        return semContatos;
    }
    
    /**
     * Obtém o número total de contatos visíveis
     */
    public int getNumeroTotalContatos() {
        By contatosLocator = By.xpath("//XCUIElementTypeCell[contains(@name, ',')]");
        return ElementUtils.getElementCount(contatosLocator);
    }
    
    /**
     * Abre a tela de grupos
     */
    public void abrirGrupos() {
        ElementUtils.click(botaoGrupos);
        logger.info("Tela de grupos aberta");
    }
    
    /**
     * Abre as configurações
     */
    public void abrirConfiguracoes() {
        ElementUtils.click(botaoConfiguracao);
        logger.info("Configurações abertas");
    }
    
    /**
     * Limpa a pesquisa
     */
    public ContatosMainPageIOS limparPesquisa() {
        if (ElementUtils.isElementPresent(campoPesquisa, 2)) {
            ElementUtils.clear(campoPesquisa);
        }
        logger.info("Pesquisa limpa");
        return this;
    }
    
    /**
     * Verifica se o campo de pesquisa está ativo
     */
    public boolean pesquisaAtiva() {
        return ElementUtils.isElementPresent(campoPesquisa, 2) && 
               ElementUtils.getText(campoPesquisa).length() > 0;
    }
}