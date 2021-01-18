package br.com.solucoes.financeiras.core.security.exception;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerController {

  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {
      @Override
      @SuppressWarnings("deprecation")
      public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        errorAttributes.remove("exception");
        return errorAttributes;
      }
    };
  }

  @ExceptionHandler(SecurityException.class)
  public void handleCustomException(HttpServletResponse res, SecurityException se) throws IOException {
    logarExcecaoSemStackTrace(se);
    res.sendError(se.getHttpStatus().value(), se.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
    res.sendError(HttpStatus.FORBIDDEN.value(), "Acesso negado.");
  }

  @ExceptionHandler(Exception.class)
  public void handleException(HttpServletResponse res) throws IOException {
    res.sendError(HttpStatus.BAD_REQUEST.value(), "Algo saiu errado, contate o administrador do sistema.");
  }

  private static void logarExcecaoSemStackTrace(Throwable e, String... errorsMsg) {
    var mensagem = errorsMsg.length == 1 ? errorsMsg[0] : e.getMessage();
    esconderStackTrace(e);
  }

  private static void esconderStackTrace(Throwable e) {
    e.setStackTrace(new StackTraceElement[]{});
    log.error(e.getMessage());
  }

}