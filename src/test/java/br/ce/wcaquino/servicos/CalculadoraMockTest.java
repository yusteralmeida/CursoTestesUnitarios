package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;
	@Spy
	private Calculadora calcSpay;
	@Mock
	private EmailService email;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaDias() {
		Mockito.when(calcMock.soma(1, 2)).thenReturn(5);
//		Quando não sabe o que fazer ele chama o verdadeiro metodo
//		Mockito.when(calcSpay.soma(1, 2)).thenReturn(8);
//		Diz para o Spy fazer nada
		Mockito.doNothing().when(calcSpay).imprimi();
//		É a expectativa sem imprimir a primeira linha do método
		Mockito.doReturn(5).when(calcSpay).soma(1, 2);
		
		System.out.println("Mock: "+ calcMock.soma(1, 2));
		System.out.println("Spy: "+ calcSpay.soma(1, 2));
	
		// O mock não faz nada
		System.out.println("Mock");
		calcMock.imprimi();
		System.out.println("Spy");
		calcSpay.imprimi();
	}
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);

//		Valor esperado para a captura
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
//		argCapt.capture() => captura os valores de entrada
		Mockito.when(calc.soma(argCapt.capture(), argCapt.capture())).thenReturn(2);

		assertEquals(2, calc.soma(1, 202020202));

//		Mostra os valores em um array
//		System.out.println(argCapt.getAllValues());
	}
}
