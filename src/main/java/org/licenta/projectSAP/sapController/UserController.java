package org.licenta.projectSAP.sapController;

import org.licenta.projectSAP.sapRepository.entity.User;
import org.licenta.projectSAP.sapService.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public DeferredResult<ResponseEntity<User>> createUser(@RequestBody User user) {
        DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();

        userService.createUser(user)
                .whenComplete((createdUser, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    } else {
                        deferredResult.setResult(ResponseEntity.status(HttpStatus.CREATED).body(createdUser));
                    }
                });

        return deferredResult;
    }

    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();

        userService.getUserById(id)
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else if (user != null) {
                        deferredResult.setResult(ResponseEntity.ok(user));
                    } else {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    }
                });

        return deferredResult;
    }

    @GetMapping("/username/{username}")
    public DeferredResult<ResponseEntity<User>> getUserByUsername(@PathVariable String username) {
        DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();

        userService.getUserByUsername(username)
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else if (user != null) {
                        deferredResult.setResult(ResponseEntity.ok(user));
                    } else {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    }
                });

        return deferredResult;
    }

    @GetMapping("/all")
    public DeferredResult<ResponseEntity<List<User>>> getAllUsers() {
        DeferredResult<ResponseEntity<List<User>>> deferredResult = new DeferredResult<>();

        userService.getAllUsers()
                .whenComplete((users, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    } else {
                        deferredResult.setResult(ResponseEntity.ok(users));
                    }
                });

        return deferredResult;
    }

    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<User>> deleteUserById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();

        userService.deleteUserById(id)
                .whenComplete((user, throwable) -> {
                    if (throwable != null) {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    } else if (user != null) {
                        deferredResult.setResult(ResponseEntity.noContent().build());
                    } else {
                        deferredResult.setErrorResult(ResponseEntity.notFound().build());
                    }
                });

        return deferredResult;
    }
}
