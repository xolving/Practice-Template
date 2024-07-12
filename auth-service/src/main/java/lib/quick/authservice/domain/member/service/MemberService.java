package lib.quick.authservice.domain.member.service;

import lib.quick.authservice.domain.member.controller.dto.GetUserInfoResponse;
import lib.quick.authservice.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberUtil memberUtil;

    public GetUserInfoResponse getUserInfo(){
        return new GetUserInfoResponse(memberUtil.getCurrentMember().getEmail());
    }
}
