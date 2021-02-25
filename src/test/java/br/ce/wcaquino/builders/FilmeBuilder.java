package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;

	private FilmeBuilder() {}

	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme 1");
		builder.filme.setEstoque(2);
		builder.filme.setPrecoLocacao(4.0);
		return builder; // Não posso enviar this

	}

	public static FilmeBuilder umFilmeSemEstoque() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme 1");
		builder.filme.setEstoque(0);
		builder.filme.setPrecoLocacao(4.0);
		return builder; // Não posso enviar this
	}

	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		return this; // Estou retornando um FilmeBuilder
	}

	public FilmeBuilder comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		return this; // Estou retornando um FilmeBuilder
	}

	public Filme agora() {
		return filme;
	}
}
