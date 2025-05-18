package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.entities.User;
import ru.pratice.pet_project.personal_finance_management_system.repositories.UserRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {
    UserService userService;
    UserRepository userRepository;
    //служебный endpoint
    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/get/{id}")
    public User getUserById(@PathVariable(name = "id") long id) {
        return userService.getUserById(id);
    }

    @GetMapping(path = "/get_by-name/{name}")
    public User getUserByName(@PathVariable(name = "name") String name) {
        return userService.getUserByName(name.trim());
    }

    @DeleteMapping(path = "/delete/{id}")
    public void deleteUserById(@PathVariable(name = "id") long id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping(path = "/delete-name/{name}")
    public void deleteUserByName(@PathVariable(name = "name") String name) {
        userService.deleteUserByName(name.trim());
    }

    @PostMapping("/create")
    public void createUser(@RequestBody User user, @RequestParam long limit) {
        userService.create(user, limit);
    }

    @PutMapping(path = "/update/{id}")
    public void updateUser(@PathVariable(name = "id") long id, @RequestBody User user) {
        userService.update(id, user);
    }

    @PatchMapping("/update-username")
    public void updateUsername(@RequestParam long id,
                               @RequestParam String username) {
        userService.updateName(id, username.trim());
    }

    @PatchMapping("/update-password")
    public void updatePassword(@RequestParam long id,
                               @RequestParam String password) {
        userService.updatePassword(id, password.trim());
    }
}
