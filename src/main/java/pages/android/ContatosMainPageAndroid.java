package pages.android;

import pages.base.BasePage;
import utils.AndroidUtils;
import utils.ElementUtils;
import org.openqa.selenium.By;
import java.util.List;

/**
 * Página principal de contatos - Android
 */
public class ContatosMainPageAndroid extends BasePage {
    
    // Seletores específicos do Android
    private final By botaoAdicionar = By.id("com.android.contacts:id/floating_action_button");
    private final By listaContatos = By.id("com.android.contacts:id/contact_list");
    // Seletor que funciona tanto para "Contacts" quanto "Contatos"
    private final By tituloContatos = By.xpath("//android.widget.TextView[@text='Contacts' or @text='Contatos']");
    private final By campoPesquisa = By.id("com.android.contacts:id/search_view");
    private final By botaoPesquisar = By.id("com.android.contacts:id/search_button");
    private final By menuOpcoes = By.id("com.android.contacts:id/menu_overflow");
    private final By opcaoImportar = By.xpath("//android.widget.TextView[@text='Import' or @text='Importar']");
    private final By opcaoExportar = By.xpath("//android.widget.TextView[@text='Export' or @text='Exportar']");
    
    // Elementos da tela vazia
    private final By mensagemListaVazia = By.id("com.android.contacts:id/message");
    private final By botaoAdicionarConta = By.id("com.android.contacts:id/add_account_button");
    private final By botaoImportarContatos = By.id("com.android.contacts:id/import_contacts_button");
    
    public ContatosMainPageAndroid() {
        super();
        
        // Navegar para o app de contatos e lidar com popups
        AndroidUtils.navigateToContacts();
        
        // Aguardar a página carregar
        waitForPageLoad(tituloContatos, "Contatos Main Android");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(tituloContatos, 5) || 
               ElementUtils.isElementPresent(listaContatos, 5) ||
               ElementUtils.isElementPresent(mensagemListaVazia, 5) ||
               ElementUtils.isElementPresent(botaoAdicionar, 5);
    }
    
    @Override
    public String getPageTitle() {
        if (ElementUtils.isElementPresent(tituloContatos, 2)) {
            return ElementUtils.getText(tituloContatos);
        }
        return "Contatos";
    }
    
    /**
     * Clica no botão adicionar contato
     */
    public AdicionarContatoPageAndroid clicarAdicionarContato() {
        ElementUtils.click(botaoAdicionar);
        logger.info("Clicado no botão adicionar contato");
        return new AdicionarContatoPageAndroid();
    }
    
    /**
     * Verifica se há contatos na lista
     */
    public boolean temContatos() {
        return !ElementUtils.isElementPresent(mensagemListaVazia, 2);
    }
    
    /**
     * Pesquisa por um contato pelo nome (se disponível)
     */
    public ContatosMainPageAndroid pesquisarContato(String nome) {
        if (!temContatos()) {
            logger.warn("Não é possível pesquisar - lista de contatos está vazia");
            return this;
        }
        
        if (ElementUtils.isElementPresent(botaoPesquisar, 3)) {
            ElementUtils.click(botaoPesquisar);
            ElementUtils.clearAndType(campoPesquisa, nome);
            logger.info("Pesquisando contato: {}", nome);
        } else {
            logger.warn("Função de pesquisa não disponível - interface simplificada");
        }
        return this;
    }
    
    /**
     * Seleciona um contato da lista pelo nome
     */
    public DetalhesContatoPageAndroid selecionarContato(String nome) {
        // Primeiro tentar pelo resource-id mais específico
        By contatoLocator = By.xpath("//android.widget.TextView[@resource-id='com.android.contacts:id/cliv_name_textview' and @text='" + nome + "']");
        
        if (!ElementUtils.isElementPresent(contatoLocator, 3)) {
            // Se não encontrar, tentar pelo texto simples
            contatoLocator = By.xpath("//android.widget.TextView[@text='" + nome + "']");
        }
        
        if (!ElementUtils.isElementPresent(contatoLocator, 3)) {
            // Se ainda não encontrar, tentar por contains para nomes parciais
            contatoLocator = By.xpath("//android.widget.TextView[contains(@text, '" + nome + "')]");
        }
        
        ElementUtils.click(contatoLocator);
        logger.info("Contato selecionado: {}", nome);
        return new DetalhesContatoPageAndroid();
    }
    
    /**
     * Verifica se um contato existe na lista
     */
    public boolean contatoExiste(String nome) {
        // Primeiro tentar pelo resource-id mais específico
        By contatoLocator = By.xpath("//android.widget.TextView[@resource-id='com.android.contacts:id/cliv_name_textview' and @text='" + nome + "']");
        
        if (!ElementUtils.isElementPresent(contatoLocator, 2)) {
            // Se não encontrar, tentar pelo texto simples
            contatoLocator = By.xpath("//android.widget.TextView[@text='" + nome + "']");
        }
        
        if (!ElementUtils.isElementPresent(contatoLocator, 2)) {
            // Se ainda não encontrar, tentar por contains para nomes parciais
            contatoLocator = By.xpath("//android.widget.TextView[contains(@text, '" + nome + "')]");
        }
        
        boolean existe = ElementUtils.isElementPresent(contatoLocator, 2);
        logger.info("Contato '{}' existe: {}", nome, existe);
        return existe;
    }
    
    /**
     * Obtém o número total de contatos
     */
    public int getNumeroTotalContatos() {
        List<By> contatos = List.of(By.xpath("//android.widget.TextView[contains(@resource-id, 'contact_name')]"));
        return ElementUtils.getElementCount(contatos.get(0));
    }
    
    /**
     * Abre o menu de opções
     */
    public ContatosMainPageAndroid abrirMenuOpcoes() {
        ElementUtils.click(menuOpcoes);
        logger.info("Menu de opções aberto");
        return this;
    }
    
    /**
     * Clica na opção importar contatos
     */
    public void importarContatos() {
        abrirMenuOpcoes();
        ElementUtils.click(opcaoImportar);
        logger.info("Opção importar contatos selecionada");
    }
    
    /**
     * Clica na opção exportar contatos
     */
    public void exportarContatos() {
        abrirMenuOpcoes();
        ElementUtils.click(opcaoExportar);
        logger.info("Opção exportar contatos selecionada");
    }
}