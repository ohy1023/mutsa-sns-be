package com.likelionsns.final_project.config;

import com.likelionsns.final_project.domain.dto.UserDto;
import com.likelionsns.final_project.service.UserService;
import com.likelionsns.final_project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    private static final String[] NO_CHECK_URL = {
            "/swagger-ui/chat.html", "/swagger-ui/springfox.css",
            "/swagger-ui/swagger-ui.css", "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/springfox.js", "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-resources/configuration/ui", "/swagger-ui/favicon-32x32.png",
            "/swagger-resources/configuration/security", "/swagger-resources",
            "/v3/api-docs", "/api/v1/users/login", "/hello", "/chat/**/websocket",
            "/login", "/register", "/" , "/sendMessage", "/chat/info", "/createPost",
            "/myChat", "/sendTest?roomId=" + "*", "/my-posts"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("requestUri:{}", request.getRequestURI());

        if (Arrays.stream(NO_CHECK_URL).anyMatch(s -> s.equals(request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return; // return으로 이후 현재 필터 진행 막기
        }

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorizationHeader);

        if (authorizationHeader == null || isNotStartBearer(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token;
        try {
            token = authorizationHeader.split(" ")[1].trim();
        } catch (Exception e) {
            log.info("token 추출에 실패했습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        String userName = JwtUtils.getUserName(token, secretKey);
        log.info("userName : {}", userName);

        //userDetail 가져오기
        UserDto user = userService.getUserByUserName(userName);

        if (JwtUtils.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getUserRole().toString())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
        filterChain.doFilter(request, response);
    }

    private static boolean isNotStartBearer(String authorizationHeader) {
        return !authorizationHeader.startsWith("Bearer ");
    }
}
