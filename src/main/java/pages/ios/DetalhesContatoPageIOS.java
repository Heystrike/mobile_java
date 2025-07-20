package pages.ios;

import model.Contato;
import pages.base.BasePage;
import utils.ElementUtils;
import org.openqa.selenium.By;

/**
 * Página de detalhes do contato - iOS
 */
public class DetalhesContatoPageIOS extends BasePage {
    
    // Seletores específicos do iOS
    private final By nomeContato = By.xpath("//XCUIElementTypeStaticText[@name='contact name']");
    private final By telefoneContato = By.xpath("//XCUIElementTypeStaticText[contains(@name, 'phone')]");
    private final By emailContato = By.xpath("//XCUIElementTypeStaticText[contains(@name, 'email')]");
    private final By empresaContato = By.xpath("//XCUIElementTypeStaticText[contains(@name, 'company')]");
    private final By botaoEditar = By.xpath("//XCUIElementTypeButton[@name='Edit']");
    private final By botaoCompartilhar = By.xpath("//XCUIElementTypeButton[@name='Share Contact']");
    private final By botaoVoltar = By.xpath("//XCUIElementTypeButton[@name='Contacts']");
    private final By botaoLigar = By.xpath("//XCUIElementTypeButton[contains(@name, 'Call')]");
    private final By botaoMensagem = By.xpath("//XCUIElementTypeButton[contains(@name, 'Message')]");
    private final By botaoFacetime = By.xpath("//XCUIElementTypeButton[@name='FaceTime']");
    private final By botaoEmail = By.xpath("//XCUIElementTypeButton[contains(@name, 'Mail')]");
    // Seletores para exclusão - iOS específicos
    private final By botaoMaisOpcoes = By.xpath("//XCUIElementTypeButton[@name='More']");
    private final By opcaoExcluir = By.xpath("//XCUIElementTypeButton[@name='Delete Contact']");
    private final By confirmarExclusao = By.xpath("//XCUIElementTypeButton[@name='Delete Contact']");
    private final By cancelarExclusao = By.xpath("//XCUIElementTypeButton[@name='Cancel']");
    
    public DetalhesContatoPageIOS() {
        super();
        waitForPageLoad(nomeContato, "Detalhes Contato iOS");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(nomeContato, 5) ||
               ElementUtils.isElementPresent(botaoEditar, 5);
    }
    
    @Override
    public String getPageTitle() {
        if (ElementUtils.isElementPresent(nomeContato, 2)) {
            return "Details - " + ElementUtils.getText(nomeContato);
        }
        return "Contact Details";
    }
    
    /**
     * Obtém o nome do contato
     */
    public String getNome() {
        String nome = ElementUtils.getText(nomeContato);
        logger.info("Nome obtido: {}", nome);
        return nome;
    }
    
    /**
     * Obtém o telefone do contato
     */
    public String getTelefone() {
        String telefone = ElementUtils.getText(telefoneContato);
        logger.info("Telefone obtido: {}", telefone);
        return telefone;
    }
    
    /**
     * Obtém o email do contato
     */
    public String getEmail() {
        String email = ElementUtils.getText(emailContato);
        logger.info("Email obtido: {}", email);
        return email;
    }
    
    /**
     * Obtém a empresa do contato
     */
    public String getEmpresa() {
        String empresa = ElementUtils.getText(empresaContato);
        logger.info("Empresa obtida: {}", empresa);
        return empresa;
    }
    
    /**
     * Obtém todos os dados do contato
     */
    public Contato obterDadosContato() {
        return new Contato.Builder()
            .nome(getNome())
            .telefone(getTelefone())
            .email(getEmail())
            .empresa(getEmpresa())
            .build();
    }
    
    /**
     * Clica no botão editar
     */
    public void editarContato() {
        ElementUtils.click(botaoEditar);
        logger.info("Botão editar clicado");
    }
    
    /**
     * Exclui o contato
     */
    public ContatosMainPageIOS excluirContato() {
        // Clicar no menu "More options"
        ElementUtils.click(botaoMaisOpcoes);
        logger.info("Menu de opções aberto");
        
        // Aguardar e clicar na opção Delete
        ElementUtils.click(opcaoExcluir);
        logger.info("Opção Delete selecionada");
        
        // Confirmar a exclusão no diálogo
        ElementUtils.click(confirmarExclusao);
        logger.info("Contato excluído com sucesso");
        
        return new ContatosMainPageIOS();
    }
    
    /**
     * Cancela a exclusão do contato
     */
    public DetalhesContatoPageIOS cancelarExclusao() {
        ElementUtils.click(botaoMaisOpcoes);
        ElementUtils.click(opcaoExcluir);
        ElementUtils.click(cancelarExclusao);
        logger.info("Exclusão cancelada");
        return this;
    }
    
    /**
     * Compartilha o contato
     */
    public void compartilharContato() {
        ElementUtils.click(botaoCompartilhar);
        logger.info("Contato compartilhado");
    }
    
    /**
     * Liga para o contato
     */
    public void ligarParaContato() {
        ElementUtils.click(botaoLigar);
        logger.info("Ligação iniciada para o contato");
    }
    
    /**
     * Envia mensagem para o contato
     */
    public void enviarMensagem() {
        ElementUtils.click(botaoMensagem);
        logger.info("Aplicativo de mensagem aberto");
    }
    
    /**
     * Inicia FaceTime com o contato
     */
    public void iniciarFacetime() {
        ElementUtils.click(botaoFacetime);
        logger.info("FaceTime iniciado");
    }
    
    /**
     * Envia email para o contato
     */
    public void enviarEmail() {
        ElementUtils.click(botaoEmail);
        logger.info("Aplicativo de email aberto");
    }
    
    /**
     * Volta para a lista de contatos
     */
    public ContatosMainPageIOS voltarParaLista() {
        ElementUtils.click(botaoVoltar);
        logger.info("Voltando para lista de contatos");
        return new ContatosMainPageIOS();
    }
}