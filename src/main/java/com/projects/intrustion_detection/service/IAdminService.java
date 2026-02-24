package com.projects.intrustion_detection.service;

import com.projects.intrustion_detection.Entity.Attack;
import com.projects.intrustion_detection.Entity.BlockedIpAddress;

import java.util.List;

public interface IAdminService {
     List<Attack> getAllAttacks();
     Attack getAttackById(Integer id);

     List<BlockedIpAddress> getAllBlockedIpAddressList();

     BlockedIpAddress getBlockedAddressById(Integer id) throws RuntimeException;

     void blockUser(String email);

     void unblockUser(String email);

}
