package com.example.mallapi.security.filter;

import com.example.mallapi.dto.MemberDTO;
import com.example.mallapi.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class JwtCheckFilter extends OncePerRequestFilter {


    //return false: 체크 O
    //return true: 체크 X
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        log.info("requestURI = {}", requestURI);

        if (requestURI.startsWith("/api/member/")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("----------------------------------");

        log.info("----------------------------------");

        try {
            String authHeader = request.getHeader("Authorization");
            String accessToken = authHeader.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("claims = {}", claims);

            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social, roleNames);
            log.info("----------------------------------");
            log.info("memberDTO = {}", memberDTO);
            log.info("memberDTO.getAuthorities() = {}", memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Check Error .................");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String json = gson.toJson(Map.of("Error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println(json);
            writer.close();
        }
    }
}
