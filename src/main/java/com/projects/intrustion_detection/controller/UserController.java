package com.projects.intrustion_detection.controller;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import com.projects.intrustion_detection.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final IAdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<List<Attack>> getAttacks(){
        List<Attack> attacks = adminService.getAllAttacks();
        return ResponseEntity.ok().body(attacks);
    }

    @GetMapping("/blocked-ip")
    public ResponseEntity<List<BlockedIpAddress>> getAllBlockedIpAddresses(){
        List<BlockedIpAddress> blockedIpAddresses = adminService.getAllBlockedIpAddressList();
        return ResponseEntity.ok().body(blockedIpAddresses);
    }
}
