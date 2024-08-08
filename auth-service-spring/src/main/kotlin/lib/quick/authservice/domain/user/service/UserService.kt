package lib.quick.authservice.domain.user.service;

import lib.quick.authservice.domain.user.controller.dto.GetUserInfoResponse;
import lib.quick.authservice.global.util.MemberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val memberUtil: MemberUtil
){
    @Transactional(readOnly = true)
    fun getUserInfo(): GetUserInfoResponse {
        return GetUserInfoResponse(memberUtil.getCurrentMember().email);
    }
}
