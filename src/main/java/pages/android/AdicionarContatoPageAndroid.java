package pages.android;

import model.Contato;
import pages.base.BasePage;
import utils.AndroidUtils;
import utils.ElementUtils;
import org.openqa.selenium.By;

/**
 * Página para adicionar contato - Android
 */
public class AdicionarContatoPageAndroid extends BasePage {
    
    // Seletores específicos do Android - Baseados na análise da UI real
    private final By campoNome = By.xpath("//android.widget.EditText[@text='First name']");
    private final By campoSobrenome = By.xpath("//android.widget.EditText[@text='Last name']");
    private final By campoTelefone = By.xpath("//android.widget.EditText[@text='Phone']");
    private final By campoEmail = By.xpath("//android.widget.EditText[@text='Email']");
    private final By botaoSalvar = By.id("com.android.contacts:id/editor_menu_save_button");
    private final By botaoCancelar = By.xpath("//android.widget.ImageButton[@content-desc='Cancel']");
    private final By tituloAdicionar = By.xpath("//android.widget.TextView[@text='Create new contact']");
    
    public AdicionarContatoPageAndroid() {
        super();
        
        // Verificar se há popup de conta e lidar com ele
        AndroidUtils.handleInitialPopups();
        
        // Aguardar a página de adicionar contato carregar
        waitForPageLoad(tituloAdicionar, "Adicionar Contato Android");
    }
    
    @Override
    public boolean isCurrentPage() {
        return ElementUtils.isElementPresent(tituloAdicionar, 5);
    }
    
    @Override
    public String getPageTitle() {
        return ElementUtils.getText(tituloAdicionar);
    }
    
    /**
     * Preenche o nome do contato (primeiro nome)
     */
    public AdicionarContatoPageAndroid preencherNome(String nome) {
        ElementUtils.clearAndType(campoNome, nome);
        logger.info("Nome preenchido: {}", nome);
        return this;
    }
    
    /**
     * Preenche o sobrenome do contato
     */
    public AdicionarContatoPageAndroid preencherSobrenome(String sobrenome) {
        ElementUtils.clearAndType(campoSobrenome, sobrenome);
        logger.info("Sobrenome preenchido: {}", sobrenome);
        return this;
    }
    
    /**
     * Preenche o telefone do contato
     */
    public AdicionarContatoPageAndroid preencherTelefone(String telefone) {
        ElementUtils.clearAndType(campoTelefone, telefone);
        logger.info("Telefone preenchido: {}", telefone);
        return this;
    }
    
    /**
     * Preenche o email do contato (se disponível)
     */
    public AdicionarContatoPageAndroid preencherEmail(String email) {
        if (ElementUtils.isElementPresent(campoEmail, 3)) {
            ElementUtils.clearAndType(campoEmail, email);
            logger.info("Email preenchido: {}", email);
        } else {
            logger.warn("Campo email não disponível na versão atual do app Contatos");
        }
        return this;
    }
    
    /**
     * Preenche todos os dados do contato
     */
    public AdicionarContatoPageAndroid preencherContato(Contato contato) {
        // Dividir o nome em primeiro nome e sobrenome
        String[] nomes = contato.getNome().split(" ", 2);
        String primeiroNome = nomes[0];
        String sobrenome = nomes.length > 1 ? nomes[1] : "";
        
        preencherNome(primeiroNome);
        if (!sobrenome.isEmpty()) {
            preencherSobrenome(sobrenome);
        }
        preencherTelefone(contato.getTelefone());
        preencherEmail(contato.getEmail());
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
}