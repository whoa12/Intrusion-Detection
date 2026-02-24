package com.projects.intrustion_detection.service;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import com.projects.intrustion_detection.Entity.UserInfo;
import com.projects.intrustion_detection.repository.AttackRepository;
import com.projects.intrustion_detection.repository.BlockedUserRepository;
import com.projects.intrustion_detection.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService{
    private final UserInfoRepository userInfoRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final AttackRepository attackRepository;

    @Override
    public List<Attack> getAllAttacks() {

        return attackRepository.findAll();
    }

    @Override
    public Attack getAttackById(Integer id )throws RuntimeException {

        return attackRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Id:" +id+ " not found"));
    }

    @Override
    public List<BlockedIpAddress> getAllBlockedIpAddressList() {

        return blockedUserRepository.findAll();
    }

    @Override
    public BlockedIpAddress getBlockedAddressById(Integer id) {

        return blockedUserRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Id: " + id + " not found"));
    }

    @Override
    public void blockUser(String email) {
        UserInfo user = userInfoRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Not found with email: "+ email));
        if(user.getAccountNonLocked()){
            throw new RuntimeException("User already blocked!");
        }

        user.setAccountNonLocked(false);

        userInfoRepository.save(user);

    }

    @Override
    public void unblockUser(String email) {
        UserInfo user = userInfoRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Not found with email: "+ email));

        if(!user.getAccountNonLocked()){
            user.setAccountNonLocked(true);
            user.setFailedAttempts(0);
        }
        userInfoRepository.save(user);
    }
}
