package com.projects.intrustion_detection.controller;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import com.projects.intrustion_detection.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private IAdminService adminService;

    @GetMapping
    public ResponseEntity<List<Attack>> getAttacks(){
        List<Attack> attacks = adminService.getAllAttacks();
        return ResponseEntity.ok().body(attacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attack> getAttackById(@PathVariable("id") Integer id){
        Attack attack = adminService.getAttackById(id);
        return ResponseEntity.ok().body(attack);
    }

    @PostMapping("/block-ip")
    public ResponseEntity<String> blockIp(@RequestParam String ip, @RequestBody BlockedIpAddress blockedIpAddress){
        adminService.blockUserByIpAddress(ip, blockedIpAddress);
        String msg = "IP: "+ ip+ " blocked successfully";
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @PostMapping("/unblock-ip")
    public ResponseEntity<String> unblockIp(@RequestParam String ip){
        adminService.unblockUserByIpAddress(ip);
        String msg = "IP: " + ip + " unblocked successfully!";
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }
    @PostMapping("/block-email")
    public ResponseEntity<String> blockUserByEmail(@RequestParam String email){
        adminService.blockUser(email);
        String msg = "User with e-mail: " + email + " blocked successfully!";
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @PostMapping("/unblock-email")
    public ResponseEntity<String> unblockUserByEmail(@RequestParam String email){
        adminService.unblockUser(email);
        String msg = "User with e-mail: " + email + " unblocked successfully!";
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }




}
