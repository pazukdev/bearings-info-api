package com.pazukdev.backend.filter;

import com.pazukdev.backend.constant.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Siarhei Sviarkaltsau
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Integer TEN_DAYS_MILLIS = 864000000;

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final FilterChain filterChain,
                                            final Authentication authentication) throws IOException {

        final List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        final String userName = authentication.getPrincipal().toString();
        final byte[] secretBytes = SecurityConstants.JWT_SECRET.getBytes();

        final String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretBytes)
                .setHeaderParam("tokenType", SecurityConstants.TOKEN_TYPE)
                .setIssuer(SecurityConstants.TOKEN_ISSUER)
                .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + TEN_DAYS_MILLIS))
                .claim("roles", roles)
                .compact();

        //response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"" + SecurityConstants.TOKEN_HEADER + "\":\"" + SecurityConstants.TOKEN_PREFIX + token + "\"}"
        );
    }

}
