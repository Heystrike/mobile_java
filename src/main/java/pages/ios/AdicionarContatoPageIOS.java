package pages.ios;

import model.Contato;
import pages.base.BasePage;
import utils.ElementUtils;
import utils.IOSUtils;
import org.openqa.selenium.By;

/**
 * Página para adicionar contato - iOS
 */
public class AdicionarContatoPageIOS extends BasePage {
    
    // Seletores específicos do iOS
    private final By campoNome = By.xpath("//XCUIElementTypeTextField[@name='First name']");
    private final By campoSobrenome = By.xpath("//XCUIElementTypeTextField[@name='Last name']");
    private final By campoTelefone = By.xpath("//XCUIElementTypeTextField[@name='Phone']");
    private final By campoEmail = By.xpath("//XCUIElementTypeTextField[@name='Email']");
    private final By campoEmpresa = By.xpath("//XCUIElementTypeTextField[@name='Company']");
    private final By botaoSalvar = By.xpath("//XCUIElementTypeButton[@name='Done']");
    private final By botaoCancelar = By.xpath("//XCUIElementTypeButton[@name='Cancel']");
    private final By tituloAdicionar = By.xpath("//XCUIElementTypeNavigationBar[@name='New Contact']");
    private final By botaoAdicionarCampo = By.xpath("//XCUIElementTypeButton[@name='add field']");
    
    public AdicionarContatoPageIOS() {
        super();
        
        // Verificar se há popup de conta e lidar com ele
        IOSUtils.handleInitialPopups();
        
        // Aguardar a página de adicionar contato carregar
        waitForPageLoad(tituloAdicionar, "Adicionar Contato iOS");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(tituloAdicionar, 5);
    }
    
    @Override
    public String getPageTitle() {
        return "New Contact";
    }
    
    /**
     * Preenche o nome do contato
     */
    public AdicionarContatoPageIOS preencherNome(String nome) {
        ElementUtils.clearAndType(campoNome, nome);
        logger.info("Nome preenchido: {}", nome);
        return this;
    }
    
    /**
     * Preenche o sobrenome do contato
     */
    public AdicionarContatoPageIOS preencherSobrenome(String sobrenome) {
        ElementUtils.clearAndType(campoSobrenome, sobrenome);
        logger.info("Sobrenome preenchido: {}", sobrenome);
        return this;
    }
    
    /**
     * Preenche o telefone do contato
     */
    public AdicionarContatoPageIOS preencherTelefone(String telefone) {
        ElementUtils.clearAndType(campoTelefone, telefone);
        logger.info("Telefone preenchido: {}", telefone);
        return this;
    }
    
    /**
     * Preenche o email do contato
     */
    public AdicionarContatoPageIOS preencherEmail(String email) {
        ElementUtils.clearAndType(campoEmail, email);
        logger.info("Email preenchido: {}", email);
        return this;
    }
    
    /**
     * Preenche a empresa do contato
     */
    public AdicionarContatoPageIOS preencherEmpresa(String empresa) {
        ElementUtils.clearAndType(campoEmpresa, empresa);
        logger.info("Empresa preenchida: {}", empresa);
        return this;
    }
    
    /**
     * Preenche todos os dados do contato
     * No iOS, o nome completo é dividido em nome e sobrenome
     */
    public AdicionarContatoPageIOS preencherContato(Contato contato) {
        String[] nomeCompleto = contato.getNome().split(" ", 2);
        String nome = nomeCompleto[0];
        String sobrenome = nomeCompleto.length > 1 ? nomeCompleto[1] : "";
        
        preencherNome(nome);
        if (!sobrenome.isEmpty()) {
            preencherSobrenome(sobrenome);
        }
        preencherTelefone(contato.getTelefone());
        preencherEmail(contato.getEmail());
        preencherEmpresa(contato.getEmpresa());
        return this;
    }
    
    /**
     * Salva o contato
     */
    public void salvarContato() {
        ElementUtils.click(botaoSalvar);
        logger.info("Contato salvo com sucesso");
    }
    
    /**
     * Cancela a adição do contato
     */
    public void cancelarAdicao() {
        ElementUtils.click(botaoCancelar);
        logger.info("Adição de contato cancelada");
    }
    
    /**
     * Adiciona um campo personalizado
     */
    public AdicionarContatoPageIOS adicionarCampo() {
        ElementUtils.click(botaoAdicionarCampo);
        logger.info("Campo adicional adicionado");
        return this;
    }
}