package com.fablefit.identity.filters;

import com.fablefit.identity.repository.UserRepository;
import com.fablefit.identity.service.JwtService;
import com.fablefit.identity.service.TenantService;
import com.fablefit.identity.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final TenantService tenantService;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);
        try {
            boolean isValidToken = jwtService.validateToken(token);
            if (!isValidToken) {
                filterChain.doFilter(request, response);
            }
            String userPublicId = jwtService.getSubject(token);
            String userID=userService.resolveUserPublicIdToInternalId(userPublicId).toString();
            String role = jwtService.getClaim(token, "role");
            String tenantKey=jwtService.getClaim(token, "tenantKey");
            String tenantId=tenantService.getTenantIdFromKey(tenantKey).toString();
            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    Map.of(
                        "user",userID,
                        "tenant",tenantId
                    ),
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {

        }
        filterChain.doFilter(request, response);
    }
}
