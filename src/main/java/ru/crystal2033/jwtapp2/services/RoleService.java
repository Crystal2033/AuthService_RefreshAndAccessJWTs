/**
 * Author: Kulikov Pavel (Crystal2033)
 * Date: 17.05.2024
 */

package ru.crystal2033.jwtapp2.services;

import org.springframework.stereotype.Service;
import ru.crystal2033.jwtapp2.entities.Role;
import ru.crystal2033.jwtapp2.repositories.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getUserRole() {
        return roleRepository.findRoleByName("ROLE_USER").get();
    }
}
