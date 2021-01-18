package br.com.solucoes.financeiras.api.usuario.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.solucoes.financeiras.api.usuario.model.Usuario;
import br.com.solucoes.financeiras.api.usuario.repository.UsuarioRepository;
import br.com.solucoes.financeiras.core.security.JwtTokenProvider;
import br.com.solucoes.financeiras.core.security.exception.SecurityException;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	private static Logger logger = LoggerFactory.getLogger(UsuarioService.class);

	public String signin(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			String token = jwtTokenProvider.createToken(username, usuarioRepository.findByUsername(username).getRoles());
			logger.info("\"" + username + "\"" + " logado com sucesso");
			return token;
	    } catch (AuthenticationException e) {
	    	throw new SecurityException("Username ou senha inválidos.", HttpStatus.UNPROCESSABLE_ENTITY);
	    }

  	}

	public String signup(Usuario usuario) {
		if (!usuarioRepository.existsByUsername(usuario.getUsername())) {
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuarioRepository.save(usuario);
			return jwtTokenProvider.createToken(usuario.getUsername(), usuario.getRoles());
	    } else {
	    	throw new SecurityException("Username já está em uso.", HttpStatus.UNPROCESSABLE_ENTITY);
	    }
	}

	public void delete(String username) {
		usuarioRepository.deleteByUsername(username);
	}

	public Usuario search(String username) {
		Usuario user = usuarioRepository.findByUsername(username);
		if (user == null) {
			throw new SecurityException("O usuário não existe.", HttpStatus.NOT_FOUND);
	    }
		return user;
	}

	public Usuario whoami(HttpServletRequest req) {
	    return usuarioRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
	}

	public String refresh(String username) {
	    return jwtTokenProvider.createToken(username, usuarioRepository.findByUsername(username).getRoles());
	}
}
