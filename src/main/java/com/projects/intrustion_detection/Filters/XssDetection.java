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
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class XssDetection extends OncePerRequestFilter {

    private final AttackRepository attackRepository;

    private static final Pattern XSS_PATTERN = Pattern.compile(
            ".*(<script>|</script>|onerror=|onload=|javascript:|alert\\(|document\\.cookie|<img|<iframe).*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,
                                    HttpServletResponse httpResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap both request and response
        CachedBodyHttpServletRequest requestWrapper =
                new CachedBodyHttpServletRequest(httpRequest);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(httpResponse);


            // Check GET parameters
            for (String param : requestWrapper.getParameterMap().keySet()) {
                String[] paramValues = requestWrapper.getParameterValues(param);
                for (String value : paramValues) {
                    if (XSS_PATTERN.matcher(value).find()) {
                        block(requestWrapper, responseWrapper, value);
                        responseWrapper.copyBodyToResponse();
                        return;
                    }
                }
            }


            // Now check the cached request body for POST requests
            byte[] body = requestWrapper.cachedBody;
            if (body.length > 0) {
                String payload = new String(body, StandardCharsets.UTF_8);
                if (XSS_PATTERN.matcher(payload).find()) {
                    // Clear the response first
                    block(requestWrapper, responseWrapper, payload);
                    responseWrapper.copyBodyToResponse();
                    return;
                }
            }


        filterChain.doFilter(requestWrapper, responseWrapper);
            responseWrapper.copyBodyToResponse();



    }

    private void block(HttpServletRequest request,
                       HttpServletResponse response,
                       String payload) throws IOException {

        Attack attack = new Attack();
        attack.setAttackType("XSS");
        attack.setIpAddress(request.getRemoteAddr());
        attack.setUri(request.getRequestURI());
        attack.setPayload(payload);
        attack.setTimeStamp(LocalDateTime.now());
        attackRepository.save(attack);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
                { "error": "XSS_DETECTED" }
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
            return new XssDetection.CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream));
        }
    }

    public class CachedBodyServletInputStream extends ServletInputStream {

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