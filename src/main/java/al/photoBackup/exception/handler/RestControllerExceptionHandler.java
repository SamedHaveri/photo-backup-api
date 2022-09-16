package al.photoBackup.exception.handler;

import al.photoBackup.exception.auth.FunctionExpiredException;
import al.photoBackup.exception.auth.FunctionNotAllowedForUserException;
import al.photoBackup.exception.auth.FunctionNotAuthorisedException;
import al.photoBackup.exception.auth.FunctionTimedOutException;
import al.photoBackup.exception.file.FileDownloadFailedException;
import al.photoBackup.exception.file.ErrorCreatingDirectoryException;
import al.photoBackup.exception.file.FileIsNotAnImageException;
import al.photoBackup.exception.file.CustomFileNotFoundException;
import al.photoBackup.exception.user.UserIdNotFoundException;
import al.photoBackup.exception.user.UserNameExistsException;
import al.photoBackup.exception.user.UserNameNotFoundException;
import al.photoBackup.exception.user.UserRoleNameNotFoundException;
import al.photoBackup.model.dto.ErrorDetails;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@ResponseBody
public class RestControllerExceptionHandler {

	@ExceptionHandler(ConversionFailedException.class)
	public ResponseEntity<String> handleConflict(RuntimeException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ConstraintViolationException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorDetails handleConstraintViolation(ConstraintViolationException ex) {
		if (ex.getConstraintViolations().size() > 0)
			return new ErrorDetails(ex.getConstraintViolations().stream().findFirst().get().getMessage());
		else
			return new ErrorDetails(ex.getMessage());
	}

	@ExceptionHandler({DataIntegrityViolationException.class})
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorDetails handleConstraintViolation(Exception ex) {
		var errorMessage = ex.getMessage();
		errorMessage = (null == errorMessage) ? "Internal Server Error" : errorMessage;
		return new ErrorDetails(errorMessage);
	}

	@ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class})
	public ResponseEntity<ErrorDetails> handleConstraintViolationHibernate(
			org.hibernate.exception.ConstraintViolationException ex, WebRequest request) {
		return new ResponseEntity<>(new ErrorDetails(ex.getConstraintName()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {
		String error = ex.getName() + " duhet të jetë i tipit " + ex.getRequiredType().getName();
		return new ResponseEntity<>(new ErrorDetails(error), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({FileIsNotAnImageException.class})
	@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	private ErrorDetails handleBadMediaType(Exception e, WebRequest request) {return new ErrorDetails(e.getMessage());}

	@ExceptionHandler({UserNameExistsException.class, ErrorCreatingDirectoryException.class})
	@ResponseStatus(value = HttpStatus.CONFLICT)
	private ErrorDetails handleConflict(Exception e, WebRequest request) {
		return new ErrorDetails(e.getMessage());
	}

	@ExceptionHandler({FileDownloadFailedException.class})
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	private ErrorDetails handleError(Exception e, WebRequest request) {
		return new ErrorDetails(e.getMessage());
	}

	@ExceptionHandler({UserIdNotFoundException.class, UserRoleNameNotFoundException.class,
			UserNameNotFoundException.class, CustomFileNotFoundException.class})
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	private ErrorDetails handleNotFound(Exception e, WebRequest request) {
		return new ErrorDetails(e.getMessage());
	}
	@ExceptionHandler({FunctionExpiredException.class, FunctionTimedOutException.class})
	@ResponseStatus(value = HttpStatus.GONE)
	private ErrorDetails handleExpired(Exception e, WebRequest request) {
		return new ErrorDetails(e.getMessage());
	}

	@ExceptionHandler({FunctionNotAuthorisedException.class, FunctionNotAllowedForUserException.class})
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	private ErrorDetails handleNotAuthorised(Exception e, WebRequest request) {
		return new ErrorDetails(e.getMessage());
	}

}
