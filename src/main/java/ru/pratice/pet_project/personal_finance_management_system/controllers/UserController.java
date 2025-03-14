package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "{id}")
    public User getUserById(@PathVariable(name = "id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping(path = "name/{name}")
    public User getUserByName(@PathVariable(name = "name") String name) {
        return userService.getUserByName(name);
    }

    @GetMapping(path = "email/{email}")
    public User getUserByEmail(@PathVariable(name = "email") String email) {
        return userService.getUserByEmail(email);
    }

    @DeleteMapping
    public void deleteUsers() {
        userService.deleteAllUsers();
    }

    @DeleteMapping(path = "{id}")
    public void deleteUserById(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping(path = "name/{name}")
    public void deleteUserByName(@PathVariable(name = "name") String name) {
        userService.deleteUserByName(name);
    }

    @DeleteMapping(path = "email/{email}")
    public void deleteUserByEmail(@PathVariable(name = "email") String email) {
        userService.deleteUserByEmail(email);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(path = "{id}")
    public void updateUser(@PathVariable(name = "id") Long id, @RequestBody User user) {
        userService.update(id, user);
    }

    @PatchMapping(path = "{id}")
    public void updatePartOfUser(@PathVariable(name = "id") Long id,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false) String birth) {
        int countNotNullParam = 0;
        if (name != null) {
            countNotNullParam++;
            userService.updateName(id, name);
        }
        if (email != null) {
            countNotNullParam++;
            userService.updateEmail(id, email);
        }
        if (password != null) {
            countNotNullParam++;
            userService.updatePassword(id, password);
        }
        if (birth != null) {
            countNotNullParam++;
            userService.updateBirth(id, birth);
        }
        if (countNotNullParam == 0) {
            throw new InvalidEntityException("There are no parameters for changes in the request for changes",
                    LocalTime.now());
        }
    }
}
