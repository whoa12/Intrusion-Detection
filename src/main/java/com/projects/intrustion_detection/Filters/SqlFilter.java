package com.projects.intrustion_detection.Filters;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.repository.AttackRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SqlFilter extends OncePerRequestFilter {
    private final AttackRepository attackRepository;
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i).*\\b(or|and)\\b|--|;|/\\*|\\*/|\\b(union|select|insert|update|delete|drop|create|alter|exec)\\b"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        // Wrapping content for POST JSON
        CachedBodyHttpServletRequest requestWrapper =
                new CachedBodyHttpServletRequest(httpRequest);


            // Check GET parameters
            for (String param : requestWrapper.getParameterMap().keySet()) {
                String[] paramValues = requestWrapper.getParameterValues(param);
                for (String value : paramValues) {
                    if (SQL_INJECTION_PATTERN.matcher(value).find()) {
                        block(requestWrapper, httpResponse, value);

                        return;
                    }
                }
            }




            // Now check the cached request body
            byte[] body = requestWrapper.cachedBody;
            if (body.length > 0) {
                String payload = new String(body, StandardCharsets.UTF_8);
                if (SQL_INJECTION_PATTERN.matcher(payload).find()) {
                    block(requestWrapper, httpResponse, payload);
                    return;

                }
            }

            filterChain.doFilter(requestWrapper, httpResponse);




    }

    private void block(HttpServletRequest request,
                       HttpServletResponse response,
                       String payload) throws IOException {
        Attack attack = new Attack();
        attack.setAttackType("SQL Injection");
        attack.setIpAddress(request.getRemoteAddr());
        attack.setUri(request.getRequestURI());
        attack.setPayload(payload);
        attack.setTimeStamp(LocalDateTime.now());
        attackRepository.save(attack);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
                { "error": "SQL_INJECTION_DETECTED" }
            """);
    }
    public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream));
        }
    }

    public static class CachedBodyServletInputStream extends ServletInputStream {

        private InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }

}