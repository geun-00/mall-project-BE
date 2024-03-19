package com.example.mallapi.service;

import com.example.mallapi.domain.Member;
import com.example.mallapi.domain.MemberRole;
import com.example.mallapi.dto.MemberDTO;
import com.example.mallapi.dto.MemberModifyDTO;

public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    void modifyMember(MemberModifyDTO memberModifyDTO);

    default MemberDTO entityToDTO(Member member) {
        return new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList().stream().map(Enum::name).toList()
        );
    }
}
