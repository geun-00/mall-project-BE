package com.example.mallapi.service;

import com.example.mallapi.domain.Member;
import com.example.mallapi.domain.MemberRole;
import com.example.mallapi.dto.MemberDTO;
import com.example.mallapi.dto.MemberModifyDTO;
import com.example.mallapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {

        //accessToken을 이용해서 사용자 정보 가져오기
        //기존 DB에 회원 정보가 있는 경우 or 없는 경우
        String nickname = getEmailFromKakaoAccessToken(accessToken);

        Optional<Member> result = memberRepository.findById(nickname);

        if (result.isPresent()) {
            return entityToDTO(result.get());
        } else {
            Member scoialMember = makeSocialMember(nickname);
            memberRepository.save(scoialMember);

            return entityToDTO(scoialMember);
        }
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());
        Member member = result.orElseThrow();

        member.changeNickname(memberModifyDTO.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode((memberModifyDTO.getPw())));

//        memberRepository.save(member);
    }

    private Member makeSocialMember(String email) {
        String tempPassword = makeTempPassword();
        log.info("tempPassword = {}", tempPassword);

        Member member = Member.builder()
                              .email(email)
                              .pw(passwordEncoder.encode(tempPassword))
                              .nickname("Social Member")
                              .social(true)
                              .build();
        member.addRole(MemberRole.USER);

        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response = {}", response);

        LinkedHashMap<String, LinkedHashMap> body = response.getBody();
        LinkedHashMap<String, String> kakaoAccount = body.get("properties");
        log.info("kakaoAccount = {}", kakaoAccount);

        String nickname = kakaoAccount.get("nickname");

        log.info("nickname = {}", nickname);

        return nickname;
    }

    private String makeTempPassword() {
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            bf.append((char) ((int) (Math.random() * 55) + 65));
        }
        return bf.toString();
    }
}
