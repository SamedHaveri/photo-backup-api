package al.photoBackup.exception.handler;

import al.photoBackup.model.dto.ErrorDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
																  HttpStatus status, WebRequest request) {
		if (ex.getBindingResult().getFieldError() != null) { //return first validation error
			return new ResponseEntity<>(new ErrorDetails(ex.getBindingResult().getFieldError().getDefaultMessage()),
					HttpStatus.BAD_REQUEST);
		}
		final var errorDetails = new ErrorDetails("Kërkesë e gabuar");
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
																		  HttpHeaders headers, HttpStatus status,
																		  WebRequest request) {
		final var errorDetails = new ErrorDetails("Mungon parametri: " + ex.getParameterName());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
																		 HttpHeaders headers, HttpStatus status, WebRequest request) {
		final var errorDetails = new ErrorDetails("Funksioni nuk ekziston");
		return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
	}
}
