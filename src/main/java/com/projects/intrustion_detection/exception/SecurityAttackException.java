package com.projects.intrustion_detection.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SecurityAttackException extends RuntimeException {
    private final String attackType;
    private final String payload;

    public SecurityAttackException(String attackType, String payload){
        super(attackType + "detected");
        this.attackType = attackType;
        this.payload = payload;
    }



}
