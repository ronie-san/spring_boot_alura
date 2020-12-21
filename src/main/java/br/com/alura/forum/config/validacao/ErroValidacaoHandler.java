package br.com.alura.forum.config.validacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//receptador de ações do Controller
@RestControllerAdvice
public class ErroValidacaoHandler {

	// classe que ajuda a pegar as mensagens de erro, de acordo com o idioma
	@Autowired
	private MessageSource messageSource;

	// se não tiver esta anotação, o Spring retorna 200 (sucesso) ao invés de
	// retornar 400 (erro)
	// este handler funciona como um try/catch; por isso que o Spring retornaria 200
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	// receptador de exceptions de formulário (exemplo: validação de formulário)
	// toda vez que ocorrer este tipo de exception em qualquer Controller, este
	// método será chamado
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroFormularioDTO> handle(MethodArgumentNotValidException exception) {
		List<ErroFormularioDTO> dto = new ArrayList<>();
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

		fieldErrors.forEach(e -> {
			String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
			ErroFormularioDTO erro = new ErroFormularioDTO(e.getField(), mensagem);
			dto.add(erro);
		});

		return dto;
	}
}
