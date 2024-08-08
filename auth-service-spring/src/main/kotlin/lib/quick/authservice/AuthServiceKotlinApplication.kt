package lib.quick.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthServiceKotlinApplication

fun main(args: Array<String>) {
	runApplication<AuthServiceKotlinApplication>(*args)
}
