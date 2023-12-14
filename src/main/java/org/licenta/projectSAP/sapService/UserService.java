package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();
    void deleteUserById(Long id);
}
