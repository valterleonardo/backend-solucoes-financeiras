package br.com.solucoes.financeiras.api.usuario.http;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.solucoes.financeiras.api.usuario.dto.UsuarioDTO;
import br.com.solucoes.financeiras.api.usuario.dto.UsuarioResponseDTO;
import br.com.solucoes.financeiras.api.usuario.model.Usuario;
import br.com.solucoes.financeiras.api.usuario.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/users")
@Api(tags = "users")
public class UsuarioController {

	public static final String EXPIRED_OR_INVALID_JWT_TOKEN = "JWT Token expirado ou inválido.";
	public static final String ACESSO_NEGADO = "Acesso negado.";
	public static final String ALGUMA_COISA_SAIU_ERRADO = "Alguma coisa está errado.";
	public static final String O_USUARIO_NAO_EXISTE = "O Usuário não existe.";
	public static final String USUARIO_OU_SENHA_INVALIDOS = "Usuário/Senha estão inválidos.";
	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping("/signin")
	@ApiOperation(value = "${UsuarioController.signin}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = ALGUMA_COISA_SAIU_ERRADO),
			@ApiResponse(code = 422, message = USUARIO_OU_SENHA_INVALIDOS)})
	public String login(@ApiParam("Signup User") @RequestBody UsuarioDTO user) {
		return usuarioService.signin(user.getUsername(), user.getPassword());
	}

	@PostMapping("/signup")
	@ApiOperation(value = "${UsuarioController.signup}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = ALGUMA_COISA_SAIU_ERRADO),
	      	@ApiResponse(code = 403, message = ACESSO_NEGADO),
	      	@ApiResponse(code = 422, message = USUARIO_OU_SENHA_INVALIDOS)})
	public String signup(@ApiParam("Signup User") @RequestBody UsuarioDTO user) {
	    return usuarioService.signup(modelMapper.map(user, Usuario.class));
	}

	@DeleteMapping(value = "/{username}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "${UsuarioController.delete}", authorizations = { @Authorization(value="apiKey") })
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = ALGUMA_COISA_SAIU_ERRADO),
			@ApiResponse(code = 403, message = ACESSO_NEGADO),
			@ApiResponse(code = 404, message = O_USUARIO_NAO_EXISTE),
			@ApiResponse(code = 500, message = EXPIRED_OR_INVALID_JWT_TOKEN)})
	public String delete(@ApiParam("Username") @PathVariable String username) {
		usuarioService.delete(username);
		return username;
	}

	@GetMapping(value = "/{username}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "${UsuarioController.search}", response = UsuarioResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = ALGUMA_COISA_SAIU_ERRADO),
			@ApiResponse(code = 403, message = ACESSO_NEGADO),
			@ApiResponse(code = 404, message = O_USUARIO_NAO_EXISTE),
			@ApiResponse(code = 500, message = EXPIRED_OR_INVALID_JWT_TOKEN)})
	public UsuarioResponseDTO search(@ApiParam("Username") @PathVariable String username) {
	    return modelMapper.map(usuarioService.search(username), UsuarioResponseDTO.class);
	}

	@GetMapping(value = "/me")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
	@ApiOperation(value = "${UsuarioController.me}", response = UsuarioResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = ALGUMA_COISA_SAIU_ERRADO),
			@ApiResponse(code = 403, message = ACESSO_NEGADO),
	      	@ApiResponse(code = 500, message = EXPIRED_OR_INVALID_JWT_TOKEN)})
  	public UsuarioResponseDTO whoami(HttpServletRequest req) {
	    return modelMapper.map(usuarioService.whoami(req), UsuarioResponseDTO.class);
	}

	@GetMapping("/refresh")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
	public String refresh(HttpServletRequest req) {
	    return usuarioService.refresh(req.getRemoteUser());
	}
}