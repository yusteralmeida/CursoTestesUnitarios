package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spc;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
//			Minha propria exceção
				throw new FilmeSemEstoqueException();
			}
		}
		if (usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}

		// #2 SPC
		boolean negativado;
		try {
			negativado = spc.possuiNegativacao(usuario); 
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}
		
		if (negativado) {
			throw new LocadoraException("Usuário negativado!");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		Double valorTotal = 0d;

		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();

			switch (i) {
			case 2:
				valorFilme = valorFilme * 0.75;
				break;
			case 3:
				valorFilme = valorFilme * 0.5;
				break;
			case 4:
				valorFilme = valorFilme * 0.25;
				break;
			case 5:
				valorFilme = 0d;
				break;
			}

			valorTotal += valorFilme;
		}

		locacao.setValor(valorTotal);
		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);

		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		}

		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

//	Sem anotações de mockito
//	public void setLocacaoDAO(LocacaoDAO dao) {
//		this.dao = dao;
//	}
//
//	public void setEmailService(EmailService email) {
//		this.emailService = email;
//	}
//
//	// #3 SPC
//	public void setSpc(SPCService spc) {
//		this.spc = spc;
//	}

	public void notificarAtraso() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if(locacao.getDataRetorno().before(new Date())) {
			emailService.notificarAtraso(locacao.getUsuario());
		}}
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novalocacao = new Locacao();
		novalocacao.setUsuario(locacao.getUsuario());
		novalocacao.setFilmes(locacao.getFilmes());
		novalocacao.setDataLocacao(new Date());
		novalocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novalocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novalocacao);
		
	}
}
