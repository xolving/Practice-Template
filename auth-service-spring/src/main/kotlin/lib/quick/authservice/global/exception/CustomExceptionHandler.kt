package lib.quick.authservice.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lib.quick.authservice.global.security.dto.ExceptionResponse;
import lib.quick.authservice.global.util.getLogger
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class CustomExceptionHandler (
	private val objectMapper: ObjectMapper
) {
	val log = getLogger()

	@ExceptionHandler(HttpException::class)
	fun httpException(exception: HttpException): ResponseEntity<ExceptionResponse> {
		val response = ExceptionResponse(
			exception.statusCode.value(), exception.message
		)

		log.error("{}", objectMapper.writeValueAsString(response));
		return ResponseEntity.status(exception.statusCode).body(response);
	}
}
