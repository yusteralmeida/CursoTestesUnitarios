package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTest {

	private Calculadora calc;

	@Before
	public void setup() {
		calc = new Calculadora();
	}

//	1º TDD(Test Driven Development = Desenvolvimento Orientado por Testes) 
	@Test
	public void DeveSomarDoisValores() {
//		Cenario
		int a = 5;
		int b = 3;

//		 Ação
		int resultado = calc.soma(a, b);

//		 Verificação
		assertEquals(8, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {
//		Cenario
		int a = 8;
		int b = 5;

//		 Ação
		int resultado = calc.subtracao(a, b);

//		 Verificação
		assertEquals(3, resultado);
	}

	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
//		Cenario
		int a = 6;
		int b = 3;

//		 Ação
		int resultado = calc.divisao(a, b);

//		 Verificação
		assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
//		Cenario
		int a = 10;
		int b = 0;

//		 Ação
		int resultado = calc.divisao(a, b);

//		 Verificação
		assertEquals(2, resultado);
	}
}
