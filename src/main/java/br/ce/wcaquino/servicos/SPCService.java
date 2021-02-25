package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

// primeiro fazer uma interface
// // #1 SPC. Depois fazer a implementação de spc e o set

public interface SPCService {
	public boolean possuiNegativacao(Usuario usuario) throws Exception; 
}
