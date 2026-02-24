package com.projects.intrustion_detection.controller;

import com.projects.intrustion_detection.Configuration.JwtService;
import com.projects.intrustion_detection.Entity.UserInfo;
import com.projects.intrustion_detection.repository.UserInfoRepository;
import com.projects.intrustion_detection.request.AuthRequest;
import com.projects.intrustion_detection.response.AuthResponse;
import com.projects.intrustion_detection.service.AttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserInfoRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AttemptService attemptService;



    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserInfo user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists!");
        }

        UserInfo newUser = new UserInfo();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());

        UserInfo savedUser = userRepository.save(newUser);

        String jwt = jwtService.generateToken(savedUser.getEmail());

        AuthResponse response = new AuthResponse();
        response.setMessageGenerated("Registration successful!");
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setJwt(jwt);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            HttpServletRequest httpServletRequest) {
        //first get ip from httpservletrequest.
        String ip = Optional.ofNullable(httpServletRequest.getHeader("X-Forwarded-For"))
                .map(h -> h.split(",")[0].trim())
                .orElse(httpServletRequest.getRemoteAddr());

        //if ip already blocked -> do not proceed further
        if (attemptService.isBlocked(ip)) {
            AuthResponse response = new AuthResponse();
            response.setMessageGenerated("Too many requests! Please try again later.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            //if username and password are valid, reset attempts for ip
            attemptService.resetIp(ip);
            userRepository.findByEmail(authentication.getName()).ifPresent(user ->{
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                userRepository.save(user);
            });

            String jwt = jwtService.generateToken(authentication.getName());

            String role = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);

            AuthResponse response = new AuthResponse();
            response.setMessageGenerated("Login successful!");
            response.setJwt(jwt);
            response.setRole(role);
            response.setEmail(authentication.getName());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {  //basically, failed login attempt
            attemptService.trackIp(ip);



            AuthResponse response = new AuthResponse();
            Optional<UserInfo> userByEmail = userRepository.findByEmail(request.getEmail());
            if(userByEmail.isPresent()){
                UserInfo user = userByEmail.get();
                user.setFailedAttempts(user.getFailedAttempts() + 1);
                if(user.getFailedAttempts() >= 15) {
                    user.setAccountNonLocked(false);
                    attemptService.logAttack(httpServletRequest.getRequestURI(),
                           ip, request.getEmail());
                }
                userRepository.save(user);

            }
            response.setMessageGenerated("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
