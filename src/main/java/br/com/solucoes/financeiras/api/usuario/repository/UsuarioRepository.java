package br.com.solucoes.financeiras.api.usuario.repository;

import br.com.solucoes.financeiras.api.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	boolean existsByUsername(String username);

	Usuario findByUsername(String username);

	@Transactional
	void deleteByUsername(String username);

}