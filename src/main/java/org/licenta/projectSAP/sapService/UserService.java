package org.licenta.projectSAP.sapService;

import org.licenta.projectSAP.sapRepository.entity.User;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    @Async
    CompletableFuture<User> createUser(User user);

    @Async
    CompletableFuture<User> getUserById(Long id);

    @Async
    CompletableFuture<User> getUserByUsername(String username);

    @Async
    CompletableFuture<List<User>> getAllUsers();
    @Async
    CompletableFuture<User> deleteUserById(Long id);
}
