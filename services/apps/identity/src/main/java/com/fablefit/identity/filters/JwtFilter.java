package com.fablefit.identity.filters;

import com.fablefit.identity.entity.Tenant;
import com.fablefit.identity.entity.User;
import com.fablefit.identity.enums.Role;
import com.fablefit.identity.exception.TenantErrorCode;
import com.fablefit.identity.exception.UserErrorCode;
import com.fablefit.identity.repository.TenantRepository;
import com.fablefit.identity.repository.UserRepository;
import com.fablefit.identity.service.JwtService;
import com.fablefit.identity.utils.AuthContext;
import com.fablefit.exceptionhandler.ApplicationException;

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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

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
                return;
            }
            String userPublicId = jwtService.getSubject(token);
            String tenantKey = jwtService.getClaim(token, "tenantKey");

            User user = userRepository.findByPublicId(userPublicId).orElseThrow(
                    () -> new ApplicationException(UserErrorCode.USER_NOT_FOUND)
            );
            Tenant tenant = tenantRepository.findByKey(tenantKey).orElseThrow(
                    () -> new ApplicationException(TenantErrorCode.TENANT_NOT_FOUND)
            );

            AuthContext authContext = AuthContext.builder()
                    .user(user)
                    .tenant(tenant)
                    .build();

            Role role = user.getRole();
            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authContext,
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {

        }
        filterChain.doFilter(request, response);
    }
}
