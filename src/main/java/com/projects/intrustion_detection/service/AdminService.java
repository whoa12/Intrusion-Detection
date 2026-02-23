package com.projects.intrustion_detection.service;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService{

    @Override
    public List<Attack> getAllAttacks() {
        return null;
    }

    @Override
    public Attack getAttackById(Long id) {
        return null;
    }

    @Override
    public List<BlockedIpAddress> getAllBlockedIpAddressList() {
        return null;
    }

    @Override
    public BlockedIpAddress getBlockedAddressById(Long id) {
        return null;
    }

    @Override
    public void blockUser(String email) {

    }

    @Override
    public void unblockUser(String email) {

    }
}
