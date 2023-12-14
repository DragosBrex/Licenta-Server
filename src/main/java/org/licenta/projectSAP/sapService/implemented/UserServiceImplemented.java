package org.licenta.projectSAP.sapService.implemented;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.licenta.projectSAP.sapRepository.UserRepository;
import org.licenta.projectSAP.sapRepository.entity.User;
import org.licenta.projectSAP.sapService.UserService;

import java.util.List;

@Service
public class UserServiceImplemented implements UserService {
    private final UserRepository userRepository;

    public UserServiceImplemented(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}

