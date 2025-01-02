package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	//===========================================================================================================================
	@Configuration
	@Order(1)
	public static class App1ConfigurationAdapter {

		@Bean
		public SecurityFilterChain filterChainApp1(HttpSecurity http) throws Exception {

			http.securityMatcher("/admin/**")
					.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
							authorizationManagerRequestMatcherRegistry
									.requestMatchers("/admin/login").permitAll()
									.requestMatchers("/admin/bestproduct/add").permitAll() // 이 경로는 모든 사용자에게 허용
									.requestMatchers("/admin/**").hasRole("ADMIN")

					)


					// log in
					.formLogin(httpSecurityFormLoginConfigurer ->
							httpSecurityFormLoginConfigurer
									.loginPage("/admin/login")
									.loginProcessingUrl("/admin/loginProc")
									.failureHandler(new CustomAuthenticationFailureHandler())
									.defaultSuccessUrl("/admin/home"))
					// logout
					.logout(httpSecurityLogoutConfigurer ->
							httpSecurityLogoutConfigurer.logoutUrl("/admin/logout")
									.logoutSuccessUrl("/admin/login")
									.invalidateHttpSession(true)
									.deleteCookies("JSESSIONID"))

					.sessionManagement((sessionManagement) -> sessionManagement
							.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) //

					.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
							httpSecurityExceptionHandlingConfigurer
									.accessDeniedHandler((request, response, accessDeniedException) -> {
												// 접근 거부 시 처리 로직
												HttpSession session = request.getSession(false);
												if (session != null) {
													session.invalidate();  // 세션 무효화
												}
												response.sendRedirect("/admin/login?access_error=true");
											}
									))

					.csrf(AbstractHttpConfigurer::disable);

			return http.build();
		}
	}

	//===========================================================================================================================
	@Configuration
	@Order(2)
	public static class App2ConfigurationAdapter {

		@Bean
		SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


			http.securityMatcher("/test/**", "/api/**", "/", "/user/**", "/question/**", "/answer/**",
							"/cscenter/**", "/myshopping/**", "/home/**", "/product/**", "/myhistory/**",
							"/productlist/**", "/wishlist/**", "/mypage/**", "/messages/**", "/portfolio/**",
							"/friends/**", "/comparison/**", "/profileImage/**", "/images/**", "/api-ssl/**",
							"/cart/**", "/chat/**", "/ws/**", "/wss/**", "/verify-captcha/**", "/check-rate-limit/**")  // /cart/** 추가


					.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
							.requestMatchers("/cart/**").authenticated()
							.requestMatchers("/chat/**").authenticated()
							.requestMatchers("/ws/**").authenticated()
							.requestMatchers("/wss/**").authenticated()// 인증된 사용자만 /cart/** 경로에 접근 가능
							.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
					.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/**")))
					.headers((headers) -> headers.addHeaderWriter(
							new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

					.sessionManagement((sessionManagement) -> sessionManagement
							.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)) //

					.formLogin(httpSecurityFormLoginConfigurer ->
							httpSecurityFormLoginConfigurer
									.loginPage("/user/login")
									.loginProcessingUrl("/user/loginProc")
									.failureHandler(new CustomAuthenticationFailureHandler())
									.defaultSuccessUrl("/home"))

					.logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
							.logoutSuccessUrl("/home").invalidateHttpSession(true));

			return http.build();
		}
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}

}
