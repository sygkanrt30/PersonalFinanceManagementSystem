package ru.pratice.pet_project.personal_finance_management_system.services.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.UserRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.EntityAlreadyExistsException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;

@SuppressWarnings("ALL")
@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    UserRepository userRepository;
    TransactionRepository transactionRepository;
    LimitRepository limitRepository;
    LimitService limitService;

    public User getUserById(long id) {
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

    @Transactional
    public void deleteUserById(long id) {
        isUserExistsById(id);
        cleanUpInOtherTables(getUserById(id));
        userRepository.deleteUserById(id);
        log.info("Deleting user with id: {}", id);
    }
    private void cleanUpInOtherTables(User user) {
        transactionRepository.deleteTransactionByUsername(user.getUsername());
        log.info("Cleaning up transactions with username: {}", user.getUsername());
        limitRepository.deleteLimitsByUsername(user.getUsername());
        log.info("Cleaning up limit with username: {}", user.getUsername());
    }

    @Transactional
    public void deleteUserByName(String name) {
        User user = getUserByName(name);
        userRepository.deleteUserByName(name);
        log.info("Deleting user {} by name", user);
        cleanUpInOtherTables(user);
    }

    public void create(User user, long limit) {
        trimFields(user);
        try {
            userRepository.save(user);
            limitService.saveLimit(LimitTracker.builder()
                    .username(user.getUsername())
                    .limitAmount(limit)
                    .totalAmount(0L)
                    .build());
        } catch (Exception e) {
            throw new InvalidEntityException(e.getMessage());
        }
        log.info("Creating user {}", user);
    }

    private void trimFields(User user) {
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());
    }

    @Transactional
    public void update(long id, User user) {
        trimFields(user);
        try {
            if (!userRepository.existsById(id)) {
                userRepository.save(user);
                log.info("Create user {}", user);
                return;
            }
            updateUsernameOfAnotherEntities(user.getUsername(), id);
            userRepository.updateUser(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getBirth());
        } catch (Exception e) {
            throw new InvalidEntityException(e.getMessage());
        }
        log.info("Update user {} since user with id: {}", user, id);
    }

    private void updateUsernameOfAnotherEntities(String username, long id) {
        transactionRepository.updateName(username, getUserById(id).getUsername());
        limitRepository.updateUsername(username, getUserById(id).getUsername());
    }

    @Transactional
    public void updateName(long id, String name) {
        isUserExistsById(id);
        try {
            updateUsernameOfAnotherEntities(name, id);
            userRepository.updateName(id, name);
            log.info("Updating user name {} with id: {}", name, id);
        } catch (Exception e) {
            throw new InvalidEntityException(e.getMessage());
        }
    }

    private void isUserExistsById(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id: " + id + " not found");
        }
    }

    @Transactional
    public void updateEmail(long id, String email) {
        isUserExistsById(id);
        try {
            userRepository.updateEmail(id, email);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("User's fields make duplicate");
        }
        log.info("Updating user email {} with id: {}", email, id);
    }

    @Transactional
    public void updatePassword(long id, String password) {
        isUserExistsById(id);
        userRepository.updatePassword(id, password);
        log.info("Updating user password {} with id: {}", password, id);

    }
}
