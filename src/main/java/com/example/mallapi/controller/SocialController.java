package com.example.mallapi.controller;

import com.example.mallapi.dto.MemberDTO;
import com.example.mallapi.dto.MemberModifyDTO;
import com.example.mallapi.service.MemberService;
import com.example.mallapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/member/kakao")
    public Map<String, Object> getMemberFromKakao(@RequestParam("accessToken") String accessToken) {
        log.info("accessToken = {}", accessToken);

        MemberDTO memberDTO = memberService.getKakaoMember(accessToken);
        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    @PutMapping("/api/member/modify")
    public Map<String, String> modify(@RequestBody MemberModifyDTO memberModifyDTO) {
        log.info("memberModifyDTO = {}", memberModifyDTO);
        memberService.modifyMember(memberModifyDTO);

        return Map.of("result", "modified");
    }
}
