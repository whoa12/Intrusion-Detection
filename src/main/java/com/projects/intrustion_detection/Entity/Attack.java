package com.projects.intrustion_detection.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String attackType;
    private String ipAddress;
    private String uri;
    private String payload;
    private LocalDateTime timeStamp;

    @JsonIgnore
    private boolean blocked = false;


}
