package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.time.LocalDate;
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

    @GetMapping(path = "/get_by_name/{name}")
    public User getUserByName(@PathVariable(name = "name") String name) {
        return userService.getUserByName(name.trim());
    }

    @GetMapping(path = "/get_by_email/{email}")
    public User getUserByEmail(@PathVariable(name = "email") String email) {
        return userService.getUserByEmail(email.trim());
    }

    @DeleteMapping
    public void deleteUsers() {
        userService.deleteAllUsers();
    }

    @DeleteMapping(path = "{id}")
    public void deleteUserById(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping(path = "/delete_by_name/{name}")
    public void deleteUserByName(@PathVariable(name = "name") String name) {
        userService.deleteUserByName(name.trim());
    }

    @DeleteMapping(path = "/delete_by_email/{email}")
    public void deleteUserByEmail(@PathVariable(name = "email") String email) {
        userService.deleteUserByEmail(email.trim());
    }

    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.create(user);
    }

    @PutMapping(path = "{id}")
    public void updateUser(@PathVariable(name = "id") Long id, @RequestBody User user) {
        userService.update(id, user);
    }

    @PatchMapping("/update_username")
    public void updateUsername(@RequestParam Long id,
                               @RequestParam String name) {
        userService.updateName(id, name.trim());
    }

    @PatchMapping("/update_email")
    public void updateEmail(@RequestParam Long id,
                            @RequestParam String email) {
        userService.updateEmail(id, email.trim());
    }

    @PatchMapping("/update_password")
    public void updatePassword(@RequestParam Long id,
                               @RequestParam String password) {
        userService.updatePassword(id, password.trim());
    }

    @PatchMapping("/update_birth_date")
    public void updateUsername(@RequestParam Long id,
                               @RequestParam LocalDate birthDate) {
        userService.updateBirth(id, birthDate);
    }
}
