package lib.quick.authservice.global.handler;

import lib.quick.authservice.global.exception.HttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(HttpException.class)
	ResponseEntity<ExceptionResponse> httpException(HttpException exception) {
		log.error("{}, {}", exception.getStatusCode(), exception.getMessage());
		return ResponseEntity.status(exception.getStatusCode())
			.body(new ExceptionResponse(exception.getMessage()));
	}
}
