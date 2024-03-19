package com.example.mallapi.controller;

import com.example.mallapi.Const.JwtConst;
import com.example.mallapi.util.CustomJWTException;
import com.example.mallapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

import static com.example.mallapi.Const.JwtConst.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class ApiRefreshController {

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam("refreshToken") String refreshToken) {

        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7);

        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        log.info("refresh .... claims = {}", claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
     }

    private boolean checkTime(Integer exp) {
        Date expDate = new Date((long) exp * 1000);//JWT exp를 날짜로 변환
        long gap = expDate.getTime() - System.currentTimeMillis();//현재 시간과의 차이 계산

        long leftMin = gap / (1000 * 60);//분단위 계산
        return leftMin < 60;//1시간도 안 남았는지
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException e) {
            if (e.getMessage().equals(EXPIRED_EX)) {
                return true;
            }
        }

        return false;
    }
}
