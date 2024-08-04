package lib.quick.authservice.domain.user.controller;

import lib.quick.authservice.domain.user.controller.dto.GetUserInfoResponse;
import lib.quick.authservice.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class UserController (
    private val userService: UserService
) {
    @GetMapping
    fun getUserInfo(): ResponseEntity<GetUserInfoResponse> {
        return ResponseEntity.ok(userService.getUserInfo());
    }
}
