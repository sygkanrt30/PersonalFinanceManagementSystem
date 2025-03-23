package ru.pratice.pet_project.personal_finance_management_system.services.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.UserRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.EntityAlreadyExistsException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id: " + id + " not found"));
    }

    public User getUserByName(String name) {
        return userRepository.findUserByName(name).orElseThrow(
                () -> new ResourceNotFoundException("User with name: " + name + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email: " + email + " not found"));
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    public void deleteUserById(Long id) {
        isUserExistsById(id);
        userRepository.deleteUserById(id);
        log.info("Deleting user with id: {}", id);
    }

    @Transactional
    public void deleteUserByName(String name) {
        Optional<User> user = userRepository.findUserByName(name);
        isUserEmpty(user, "User with name: " + name + " not found");
        userRepository.deleteUserByName(name);
        log.info("Deleting user {} by name", user);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        isUserEmpty(user, "User with email: " + email + " not found");
        userRepository.deleteUserByEmail(email);
        log.info("Deleting user {} by email", user);
    }

    public void create(User user) {
        trimFields(user);
        user.setAge(calculateAgeByBirth(user.getBirth()));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("User's fields make duplicate");
        }
        log.info("Creating user {}", user);
    }

    @Transactional
    public void update(Long id, User user) {
        trimFields(user);
        user.setAge(calculateAgeByBirth(user.getBirth()));
        try {
            if (!userRepository.existsById(id)) {
                userRepository.save(user);
                log.info("Create user {}", user);
                return;
            }
            userRepository.updateUser(id,
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getBirth(),
                    user.getAge());
            log.info("Update user {} since user with id: {}", user, id);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("User's fields make duplicate");
        }
    }

    @Transactional
    public void updateName(Long id, String name) {
        isUserExistsById(id);
        try {
            userRepository.updateName(id, name);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("User's fields make duplicate");
        }
        log.info("Updating user name {} with id: {}", name, id);
    }

    private void isUserExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id: " + id + " not found");
        }
    }

    @Transactional
    public void updateEmail(Long id, String email) {
        isUserExistsById(id);
        try {
            userRepository.updateEmail(id, email);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("User's fields make duplicate");
        }
        log.info("Updating user email {} with id: {}", email, id);
    }

    @Transactional
    public void updateBirth(Long id, LocalDate birth) {
        isUserExistsById(id);
        int age = calculateAgeByBirth(birth);
        userRepository.updateBirth(id, birth, age);
        log.info("Updating user birth {} and age {} with id: {}", birth, age, id);
    }

    @Transactional
    public void updatePassword(Long id, String password) {
        isUserExistsById(id);
        userRepository.updatePassword(id, password);
        log.info("Updating user password {} with id: {}", password, id);

    }

    private int calculateAgeByBirth(LocalDate birth) {
        return Period.between(birth, LocalDate.now()).getYears();
    }
    private void isUserEmpty(Optional<User> user, String message) {
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(message);
        }
    }

    private void trimFields(User user) throws NullPointerException {
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());
    }
}
