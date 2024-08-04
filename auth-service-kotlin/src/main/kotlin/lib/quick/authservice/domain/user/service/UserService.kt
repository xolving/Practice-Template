package lib.quick.authservice.domain.user.service;

import lib.quick.authservice.domain.user.controller.dto.GetUserInfoResponse;
import lib.quick.authservice.global.util.MemberUtil;
import org.springframework.stereotype.Service;

@Service
class UserService (
    private val memberUtil: MemberUtil
){
    fun getUserInfo(): GetUserInfoResponse {
        return GetUserInfoResponse(memberUtil.getCurrentMember().email);
    }
}
