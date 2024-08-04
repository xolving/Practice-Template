package lib.quick.authservice.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class HttpException extends RuntimeException{
	HttpStatus statusCode;
	String message;
	public Throwable fillInStackTrace(){
		return this;
	}
}
