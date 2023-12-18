package org.licenta.projectSAP.sapService.implemented;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.licenta.projectSAP.sapRepository.UserRepository;
import org.licenta.projectSAP.sapRepository.entity.User;
import org.licenta.projectSAP.sapService.UserService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImplemented implements UserService {
    private final UserRepository userRepository;

    public UserServiceImplemented(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<User> createUser(User user) {
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    @Override
    public CompletableFuture<User> getUserById(Long id) {
        return CompletableFuture.completedFuture(userRepository.findById(id).orElse(null));
    }

    @Override
    public CompletableFuture<User> getUserByUsername(String username) {
        return CompletableFuture.completedFuture(userRepository.findByUsername(username));
    }

    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Override
    @Transactional
    public CompletableFuture<User> deleteUserById(Long id) {
        User user =  userRepository.findById(id).get();
        userRepository.deleteById(id);

        return CompletableFuture.completedFuture(user);
    }
}

