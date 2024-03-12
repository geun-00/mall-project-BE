package com.example.mallapi.repository;

import com.example.mallapi.domain.Member;
import com.example.mallapi.domain.MemberRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MemberRepositoryTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void testInsertMember() {
        // given
        for (int i = 0; i < 10; i++) {
            Member member = Member.builder()
                                  .email("user" + i + "@test.com")
                                  .pw(passwordEncoder.encode("1111"))
                                  .nickname("USER" + i)
                                  .build();
            member.addRole(MemberRole.USER);

            if (i >= 5) {
                member.addRole(MemberRole.MANAGER);
            }
            if (i >= 8) {
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        }
        // when

        // then
    }

    @Test
    void testRead() {
        String email = "user9@test.com";
        Member member = memberRepository.getWithRoles(email);

        log.info("---------");
        log.info("member = {}", member);
        log.info("member = {}", member.getMemberRoleList());
    }

}