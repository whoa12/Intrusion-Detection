package com.projects.intrustion_detection.exception;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.repository.AttackRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final AttackRepository attackRepository;

    @ExceptionHandler(SecurityAttackException.class)
    public ResponseEntity<?> handleSecurityAttack(SecurityAttackException ex, HttpServletRequest request){
        Attack attack = new Attack();
        attack.setAttackType(ex.getAttackType());
        attack.setIpAddress(request.getRemoteAddr());
        attack.setUri(request.getRequestURI());
        attack.setPayload(ex.getPayload());
        attack.setTimeStamp(LocalDateTime.now());
        attackRepository.save(attack);


        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Malicious request detected");
    }
}
