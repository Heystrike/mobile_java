package pages.android;

import model.Contato;
import pages.base.BasePage;
import utils.ElementUtils;
import org.openqa.selenium.By;

/**
 * Página de detalhes do contato - Android
 */
public class DetalhesContatoPageAndroid extends BasePage {
    
    // Seletores específicos do Android (baseados na interface real)
    private final By nomeContato = By.xpath("//android.widget.TextView[contains(@text, ' ')]"); // Nome aparece como texto principal
    private final By telefoneContato = By.xpath("//android.widget.TextView[contains(@text, '(') or contains(@text, '+') or contains(@text, '-')]");
    private final By emailContato = By.xpath("//android.widget.TextView[contains(@text, '@')]");
    private final By empresaContato = By.id("com.android.contacts:id/company_name");
    private final By botaoEditar = By.id("com.android.contacts:id/menu_edit");
    private final By botaoMaisOpcoes = By.xpath("//android.widget.ImageButton[@content-desc='More options']");
    private final By opcaoExcluir = By.xpath("//android.widget.TextView[@text='Delete']");
    private final By botaoCompartilhar = By.xpath("//android.widget.TextView[@text='Share']");
    private final By botaoVoltar = By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']");
    private final By botaoLigar = By.id("com.android.contacts:id/call_button");
    private final By botaoMensagem = By.id("com.android.contacts:id/message_button");
    private final By confirmarExclusao = By.id("android:id/button1"); // Botão DELETE
    private final By cancelarExclusao = By.id("android:id/button2"); // Botão CANCEL
    
    public DetalhesContatoPageAndroid() {
        super();
        // Na tela de detalhes, o mais confiável é aguardar pelo botão de editar
        waitForPageLoad(botaoEditar, "Detalhes Contato Android");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(botaoEditar, 5) || 
               ElementUtils.isElementPresent(botaoMaisOpcoes, 5);
    }
    
    @Override
    public String getPageTitle() {
        if (ElementUtils.isElementPresent(nomeContato, 2)) {
            return "Detalhes - " + ElementUtils.getText(nomeContato);
        }
        return "Detalhes do Contato";
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
    public ContatosMainPageAndroid excluirContato() {
        // Clicar no menu "More options"
        ElementUtils.click(botaoMaisOpcoes);
        logger.info("Menu de opções aberto");
        
        // Aguardar e clicar na opção Delete
        ElementUtils.click(opcaoExcluir);
        logger.info("Opção Delete selecionada");
        
        // Confirmar a exclusão no diálogo
        ElementUtils.click(confirmarExclusao);
        logger.info("Contato excluído com sucesso");
        
        return new ContatosMainPageAndroid();
    }
    
    /**
     * Cancela a exclusão do contato
     */
    public DetalhesContatoPageAndroid cancelarExclusao() {
        // Clicar no menu "More options"
        ElementUtils.click(botaoMaisOpcoes);
        logger.info("Menu de opções aberto");
        
        // Aguardar e clicar na opção Delete
        ElementUtils.click(opcaoExcluir);
        logger.info("Opção Delete selecionada");
        
        // Cancelar a exclusão no diálogo
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
     * Volta para a lista de contatos
     */
    public ContatosMainPageAndroid voltarParaLista() {
        ElementUtils.click(botaoVoltar);
        logger.info("Voltando para lista de contatos");
        return new ContatosMainPageAndroid();
    }
}