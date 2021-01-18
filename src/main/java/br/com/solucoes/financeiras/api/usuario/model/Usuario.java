package br.com.solucoes.financeiras.api.usuario.model;

import br.com.solucoes.financeiras.core.security.enums.Role;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	List<Role> roles;

}