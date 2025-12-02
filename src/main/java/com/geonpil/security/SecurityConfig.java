package com.geonpil.security;

import com.geonpil.mapper.user.UserMapper;
import com.geonpil.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {

    private final UserMapper userMapper;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userMapper);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        System.out.println("✅ SecurityFilterChain registered");


        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                            "/admin/**",
                            "/api/search/*/index", "/api/search/*/index-all", "/api/search/*/delete", "/api/search/*/delete-all", "/api/search/*/reindex-all"
                            ).hasRole("ADMIN")
                        //누구나 접근
                        .requestMatchers(
                                "/", "/signup", "/signup/form", "/signup/social", "/login","/find-password","/withdrawal-complete", "signup-success",
                                "/recover/**"
                                ,  "/error", "/verify/**","/board/list/**",
                                "/contest/list/**", "/contest/detail/**",
                                "/api/search/**", "/api/search/*/search", "/books/**","/reviews/**","/bug-report/**","/api/search/board/**","/api/search/contest/**"
                        ).permitAll()
                        //관리자만 접근

                        //로그인한 사용자만 접근
                        .requestMatchers("/api/reviews/**").authenticated()
                        //정적 리소스
                        .requestMatchers("/css/**", "/js/**", "/images/**","/upload/**","/fonts/**"
                                ,"/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .failureHandler(customAuthFailureHandler)
                        .loginProcessingUrl("/do-login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureHandler(customAuthFailureHandler)
                        //.successHandler(loginSuccessHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customAuthFailureHandler)
                        )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(userDetailsService())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String acceptHeader = request.getHeader("Accept");
                            boolean isApiRequest = request.getRequestURI().startsWith("/api/");

                            if (isApiRequest || (acceptHeader != null && acceptHeader.contains("application/json"))) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\": \"로그인이 필요합니다.\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication)
                    throws ServletException, IOException {
                SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
                handler.setDefaultTargetUrl("/");
                handler.setAlwaysUseDefaultTargetUrl(false);

                HttpSession session = request.getSession(false);

                System.out.println("널인가? :" + request.getSession(false));
                if (session != null) {
                    System.out.println("이게뭐지 : " + (String) session.getAttribute("redirectAfterLogin"));
                    String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                    if (redirectUrl != null) {
                        session.removeAttribute("redirectAfterLogin");
                        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                        return;
                    }
                }

                // 기본 처리 (SavedRequest가 있을 경우 자동 사용됨)
                handler.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
