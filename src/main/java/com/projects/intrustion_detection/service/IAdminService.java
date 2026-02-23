package com.projects.intrustion_detection.service;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;

import java.util.List;

public interface IAdminService {
     List<Attack> getAllAttacks();
     Attack getAttackById(Long id);

     List<BlockedIpAddress> getAllBlockedIpAddressList();

     BlockedIpAddress getBlockedAddressById(Long id);

     void blockUser(String email);

     void unblockUser(String email);

}
