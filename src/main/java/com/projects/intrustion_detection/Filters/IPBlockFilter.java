package com.projects.intrustion_detection.Filters;

import com.projects.intrustion_detection.repository.AttackRepository;
import com.projects.intrustion_detection.repository.BlockedIpAddressRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IPBlockFilter extends OncePerRequestFilter {

    @Autowired
    private BlockedIpAddressRepository blockedIPRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        if (blockedIPRepository.existsByIpAddress(ip)) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Your IP is blocked");

            return;
        }
        filterChain.doFilter(request, response);
    }
}
