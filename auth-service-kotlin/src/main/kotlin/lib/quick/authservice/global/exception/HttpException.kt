package lib.quick.authservice.global.exception;

import org.springframework.http.HttpStatus

class HttpException(
	val statusCode: HttpStatus,
	override val message: String
): RuntimeException() {
	override fun fillInStackTrace(): Throwable {
		return this;
	}
}
