package com.projects.intrustion_detection.service;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import com.projects.intrustion_detection.Entity.UserInfo;
import com.projects.intrustion_detection.repository.AttackRepository;
import com.projects.intrustion_detection.repository.BlockedIpAddressRepository;
import com.projects.intrustion_detection.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService{
    private final UserInfoRepository userInfoRepository;
    private final BlockedIpAddressRepository blockedIpAddressRepository;
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

        return blockedIpAddressRepository.findAll();
    }

    @Override
    public BlockedIpAddress getBlockedAddressById(Integer id) {

        return blockedIpAddressRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Id: " + id + " not found"));
    }

    @Override
    public void blockUser(String email) {
        UserInfo user = userInfoRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Not found with email: "+ email));
        if(!user.getAccountNonLocked()){
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

    @Override
    public void blockUserByIpAddress(String ipAddress, BlockedIpAddress blockedIpAddress) {
        BlockedIpAddress ipAddressToBlock = new BlockedIpAddress();
        ipAddressToBlock.setIpAddress(ipAddress);
        ipAddressToBlock.setReason(blockedIpAddress.getReason());
        ipAddressToBlock.setTimeStamp(LocalDateTime.now());
        blockedIpAddressRepository.save(blockedIpAddress);

    }

    @Override
    public void unblockUserByIpAddress(String ipAddress) {
        BlockedIpAddress ipProfile = blockedIpAddressRepository
                .findByIpAddress(ipAddress)
                .orElseThrow(() -> new RuntimeException("IP address: "+ ipAddress
                + " not found!"));
        blockedIpAddressRepository.delete(ipProfile);
    }


}
