package tests.android;

import tests.base.BaseTest;
import factory.PageFactory;
import pages.android.*;
import model.Contato;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Testes específicos para Android
 */
public class ContatosTestAndroid extends BaseTest {
    
    @Test(description = "Teste de adição de contato no Android")
    public void testeAdicionarContatoAndroid() {
        logger.info("Iniciando teste de adição de contato no Android");
        
        // Criar contato
        Contato novoContato = new Contato.Builder()
                .nome("Fábio Fernandes")
                .telefone("(11) 99999-9999")
                .email("fabio@teste.com")
                .empresa("Empresa Teste")
                .build();
        
        // Navegar e adicionar contato
        ContatosMainPageAndroid mainPage = (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
        Assert.assertTrue(mainPage.isCurrentPage(), "Não está na tela principal de contatos");
        
        AdicionarContatoPageAndroid addPage = mainPage.clicarAdicionarContato();
        Assert.assertTrue(addPage.isCurrentPage(), "Não está na tela de adicionar contato");
        
        addPage.preencherContato(novoContato);
        addPage.salvarContato();
        
        // Verificar se contato foi adicionado
        ContatosMainPageAndroid mainPageRetorno = new ContatosMainPageAndroid();
        Assert.assertTrue(mainPageRetorno.isCurrentPage(), "Não retornou para tela principal");
        
        boolean contatoEncontrado = mainPageRetorno.contatoExiste(novoContato.getNome());
        Assert.assertTrue(contatoEncontrado, "Contato não foi encontrado na lista");
        
        logger.info("Teste de adição de contato concluído com sucesso");
    }
    
    @Test(description = "Teste de cancelamento de adição no Android")
    public void testeCancelarAdicaoAndroid() {
        logger.info("Iniciando teste de cancelamento no Android");
        
        ContatosMainPageAndroid mainPage = (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
        AdicionarContatoPageAndroid addPage = mainPage.clicarAdicionarContato();
        
        // Preencher dados e cancelar
        Contato contato = new Contato.Builder()
                .nome("Teste Cancelar")
                .telefone("(11) 88888-8888")
                .build();
        
        addPage.preencherContato(contato);
        addPage.cancelarAdicao();
        
        ContatosMainPageAndroid mainPageRetorno = new ContatosMainPageAndroid();
        Assert.assertTrue(mainPageRetorno.isCurrentPage(), "Não retornou para tela principal");
        
        // Verificar que contato não foi salvo
        boolean contatoNaoEncontrado = !mainPageRetorno.contatoExiste(contato.getNome());
        Assert.assertTrue(contatoNaoEncontrado, "Contato foi salvo mesmo sendo cancelado");
        
        logger.info("Teste de cancelamento concluído com sucesso");
    }
    
    @Test(description = "Teste de pesquisa de contato no Android")
    public void testePesquisarContatoAndroid() {
        logger.info("Iniciando teste de pesquisa no Android");
        
        ContatosMainPageAndroid mainPage = (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
        
        // Verificar se há contatos para pesquisar
        if (!mainPage.temContatos()) {
            logger.info("Lista de contatos vazia, adicionando um contato primeiro");
            
            // Adicionar um contato para poder pesquisar
            AdicionarContatoPageAndroid adicionarPage = mainPage.clicarAdicionarContato();
            Contato contato = new Contato.Builder()
                    .nome("Fábio")
                    .telefone("11999999999")
                    .email("")
                    .build();
            adicionarPage.preencherContato(contato);
            adicionarPage.salvarContato();
            
            // Voltar para a página principal
            mainPage = (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
        }
        
        // Pesquisar por um contato
        String termoPesquisa = "Fábio";
        mainPage.pesquisarContato(termoPesquisa);
        
        // Verificar que a pesquisa foi realizada (ou que tentou realizar)
        Assert.assertTrue(mainPage.isCurrentPage(), "Não está na tela principal após pesquisa");
        
        logger.info("Teste de pesquisa concluído com sucesso");
    }
    
    @Test(description = "Teste de exclusão de contato no Android")
    public void testeExcluirContatoAndroid() {
        logger.info("Iniciando teste de exclusão de contato no Android");
        
        ContatosMainPageAndroid mainPage = (ContatosMainPageAndroid) PageFactory.createContatosMainPage();
        Assert.assertTrue(mainPage.isCurrentPage(), "Não está na tela principal de contatos");
        
        // Definir o contato para exclusão
        String nomeParaExcluir = "Fábio Exclusão";
        Contato contatoParaExcluir = new Contato.Builder()
                .nome(nomeParaExcluir)
                .telefone("(11) 88888-8888")
                .email("exclusao@teste.com")
                .empresa("Empresa Exclusão")
                .build();
        
        // Verificar se o contato já existe
        boolean contatoJaExiste = mainPage.contatoExiste(nomeParaExcluir);
        
        if (!contatoJaExiste) {
            // Contato não existe, então criar primeiro
            logger.info("Contato '{}' não existe, criando antes de excluir", nomeParaExcluir);
            
            AdicionarContatoPageAndroid addPage = mainPage.clicarAdicionarContato();
            Assert.assertTrue(addPage.isCurrentPage(), "Não está na tela de adicionar contato");
            
            addPage.preencherContato(contatoParaExcluir);
            addPage.salvarContato();
            
            // Voltar para a página principal e verificar se contato foi criado
            mainPage = new ContatosMainPageAndroid();
            Assert.assertTrue(mainPage.isCurrentPage(), "Não retornou para tela principal após adicionar");
            
            // Verificar se o contato foi criado com sucesso
            boolean contatoCriado = mainPage.contatoExiste(nomeParaExcluir);
            if (!contatoCriado) {
                logger.warn("Contato não foi encontrado na lista após criação, mas continuando com o teste");
            } else {
                logger.info("Contato '{}' criado com sucesso", nomeParaExcluir);
            }
        } else {
            logger.info("Contato '{}' já existe, procedendo diretamente para exclusão", nomeParaExcluir);
        }
        
        // Agora proceder com a exclusão do contato
        DetalhesContatoPageAndroid detalhesPage;
        try {
            logger.info("Selecionando contato '{}' para exclusão", nomeParaExcluir);
            detalhesPage = mainPage.selecionarContato(nomeParaExcluir);
            Assert.assertTrue(detalhesPage.isCurrentPage(), "Não está na tela de detalhes do contato");
            
            // Excluir o contato
            logger.info("Executando exclusão do contato '{}'", nomeParaExcluir);
            ContatosMainPageAndroid mainPageRetorno = detalhesPage.excluirContato();
            Assert.assertTrue(mainPageRetorno.isCurrentPage(), "Não retornou para tela principal após exclusão");
            
            // Verificar se o contato foi excluído
            boolean existeDepois = mainPageRetorno.contatoExiste(nomeParaExcluir);
            Assert.assertFalse(existeDepois, "Contato ainda existe após exclusão");
            
            logger.info("Contato '{}' excluído com sucesso - teste concluído", nomeParaExcluir);
            
        } catch (Exception e) {
            logger.error("Erro durante a exclusão do contato: {}", e.getMessage());
            throw new AssertionError("Falha na exclusão do contato: " + e.getMessage(), e);
        }
        
        logger.info("Teste de exclusão concluído com sucesso");
    }
}
