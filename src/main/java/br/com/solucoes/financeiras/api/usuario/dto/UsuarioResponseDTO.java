package br.com.solucoes.financeiras.api.usuario.dto;

import java.util.List;

import br.com.solucoes.financeiras.core.security.enums.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

	@ApiModelProperty(position = 0)
	private Integer id;
	
	@ApiModelProperty(position = 1)
	private String username;
	
	@ApiModelProperty(position = 2)
	private String email;
	
	@ApiModelProperty(position = 3)
	List<Role> roles;
}