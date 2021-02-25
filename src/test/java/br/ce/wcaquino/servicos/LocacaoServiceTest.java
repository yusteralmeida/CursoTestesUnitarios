package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

// Para eu poder usar o powerMock
@RunWith(PowerMockRunner.class)
// Classes que eu estou editando o construtor
@PrepareForTest({ LocacaoService.class, DataUtils.class })
public class LocacaoServiceTest {

//	Onde vou injetar os mocks
	@InjectMocks
	private LocacaoService service;

//	Estou dizendo que eh um mock 
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;

	// Vai mostar todos os erros do testeLocacao
	@Rule
	public ErrorCollector error = new ErrorCollector();

	// Usado no deveAlugarFilmeSemFilme
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
//		service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		service.setLocacaoDAO(dao);
//		// #4 SPC
//		spc = Mockito.mock(SPCService.class);
//		service.setSpc(spc);
//		email = Mockito.mock(EmailService.class);
//		service.setEmailService(email);
	}

	@Test
	public void deveAlugarFilme() throws Exception {
//		Nao pode ser um sabado. Se for sabado, ele nao executa
//		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

//		Edita o construtor. Diz que hoje eh segunda
//		PowerMockito.whenNew(Date.class).withAnyArguments().thenReturn(DataUtils.obterData(01, 03, 2021));

//		Metodos estaticos. por causa do Calendar.getInstance().getTime() na classe LocacaoService
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 01);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.YEAR, 2021);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

//		Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

//		Acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

//		Verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(01, 03, 2021)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(02, 03, 2021)), is(true));

	}

//	  1 Forma de resolver um teste que espera uma excecao
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {// FORMA ELEGANTE

//		Cenario
		Usuario usuario = umUsuario().agora();
//		Eu mudei o numero do meio
		List<Filme> filme = Arrays.asList(umFilmeSemEstoque().agora());

//		Acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {// FORMA ROBUSTA(MELHOR ESCOLHA)

//		Cenario
		List<Filme> filme = Arrays.asList(umFilme().agora());

//		Acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
//			Verificacao
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}

	@Test
	public void deveAlugarFilmeSemFilme() throws LocadoraException, FilmeSemEstoqueException {// FORMA NOVA

//		Cenario
		Usuario usuario = umUsuario().agora();

//		Verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
//		Acao
		service.alugarFilme(usuario, null);

	}

	@Test
//	@Ignore // Para ignorar um teste
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {

//		Hoje tem de ser uma sabado para poder ser executado
//		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

//		Edita o construtor. Diz que hoje eh sabado
//		PowerMockito.whenNew(Date.class).withAnyArguments().thenReturn(DataUtils.obterData(27, 02, 2021));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 27);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2021);
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

//		Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

//		Acao
		Locacao retorno = service.alugarFilme(usuario, filmes);

//		Verificacao
		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());

//		Verificacao do Powermockito
		PowerMockito.verifyStatic(Mockito.times(7));
		Calendar.getInstance();
//		verifica se a classe Date foi invocado e se foi 2 vezes 
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}

	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
//		Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

//		 O mockito eh por padrao false. Estou colocando true
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

//		 Acao
		try {
			service.alugarFilme(usuario, filmes);// Aqui que vai dah o erro
			Assert.fail();
//		Verificacao
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário negativado!"));
		}

//		 Mockito: Verificar spc.possuiNegativacao() foi chamado.
		verify(spc).possuiNegativacao(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() throws FilmeSemEstoqueException, LocadoraException {

//		Cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = asList(umLocacao().atrasado().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(), umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora());
//		Mockito
//		Quando chamar o metodo obterLocacoesPendentes, ele vai dar locacoes em obterLocacoesPendentes.
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

//		Acao
		service.notificarAtraso();

//		Mockito: Verificar email se notificarAtraso foi chamado.
		verify(email).notificarAtraso(usuario);

//		Verifica se foi chamado duas vezes
		verify(email, Mockito.times(2)).notificarAtraso(usuario3);

//		 Deve ser enviado pelo ao menos dois email
		verify(email, Mockito.atLeast(2)).notificarAtraso(usuario3);

//		Deve ser enviado no maximo 5 emails
		verify(email, Mockito.atMost(5)).notificarAtraso(usuario3);

//		Nao importa quantos sejam enviados, so vai verificar o primeiro
		verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);

//		Nunca enviou email para usuario2
		verify(email, never()).notificarAtraso(usuario2);

//		Verifica no mock email se foi executado 3 vezes o metodo notificarAtraso,
//		como parametro qualquer instacia da classe Usuario.
		verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));

//		 Nao foi mais enviado email algum
		verifyNoMoreInteractions(email);
	}

	@Test
	public void deveTratarErroNoSpc() throws Exception {
//		Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catratrÃ³fica"));

//		Verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");

//		Acao
		service.alugarFilme(usuario, filmes);

	}

	@Test
	public void deveProrrogarUmaLocacao() throws Exception {
//		Cenario
		Locacao locacao = umLocacao().agora();

//		Acao
		service.prorrogarLocacao(locacao, 3);

//		Verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetorno = argCapt.getValue();

		error.checkThat(locacaoRetorno.getValor(), is(12.0));
		error.checkThat(locacaoRetorno.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetorno.getDataRetorno(), ehHojeComDiferencaDias(3));

	}
}
