package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader=request.getHeader("Authorization");
        DecodedJWT jwt=jwtUtils.resolveJwt(authHeader);
        if(jwt!=null){
            UserDetails userDetails=jwtUtils.toUser(jwt);
            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //request.setAttribute("username",userDetails.getUsername());
            //request.setAttribute("roles",userDetails.getAuthorities());
            request.setAttribute("id",jwtUtils.toId(jwt));

        }
        filterChain.doFilter(request, response);
    }
}
