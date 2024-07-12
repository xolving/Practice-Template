package lib.quick.authservice.domain.member.controller;

import lib.quick.authservice.domain.member.controller.dto.GetUserInfoResponse;
import lib.quick.authservice.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<GetUserInfoResponse> getUserInfo(){
        return ResponseEntity.ok(memberService.getUserInfo());
    }
}
