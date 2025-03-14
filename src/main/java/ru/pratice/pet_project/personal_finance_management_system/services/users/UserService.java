package ru.pratice.pet_project.personal_finance_management_system.services.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.UserRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.EntityAlreadyExistsException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                () -> new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now()));
    }

    public User getUserByName(String name) {
        return userRepository.findUserByName(name.trim()).orElseThrow(
                () -> new ResourceNotFoundException("User with name: " + name + " not found", LocalTime.now()));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email.trim()).orElseThrow(
                () -> new ResourceNotFoundException("User with email: " + email + " not found", LocalTime.now()));
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            log.info("Deleting user with id: {}", id);
            userRepository.deleteUserById(id);
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now());
        }

    }

    @Transactional
    public void deleteUserByName(String name) {
        Optional<User> user = userRepository.findUserByName(name.trim());
        isUserEmpty(user, "User with name: " + name + " not found");
        log.info("Deleting user {} by name", user);
        userRepository.deleteUserByName(name);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email.trim());
        isUserEmpty(user, "User with email: " + email + " not found");
        log.info("Deleting user {} by email", user);
        userRepository.deleteUserByEmail(email);
    }

    public User create(User user) {
        try {
            checkUserFields(user);
            trimFields(user);
            user.setAge(Period.between(user.getBirth(), LocalDate.now()).getYears());
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new InvalidEntityException("User fields can't be null", LocalTime.now());
        }
        log.info("Creating user {}", user);
        return userRepository.save(user);
    }

    @Transactional
    public void update(Long id, User user) {
        try {
            trimFields(user);
            checkUserFields(user);
            user.setAge(Period.between(user.getBirth(), LocalDate.now()).getYears());
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new InvalidEntityException("User fields can't be null", LocalTime.now());
        }
        if (!userRepository.existsById(id)) {
            log.info("Create user {}", user);
            userRepository.save(user);
        } else {
            log.info("Update user {} since user with id: {}", user, id);
            userRepository.updateUser(id,
                    user.getEmail(),
                    user.getName(),
                    user.getPassword(),
                    user.getBirth(),
                    user.getAge());
        }
    }

    @Transactional
    public void updateName(Long id, String name) {
        if (userRepository.existsById(id)) {
            name = name.trim();
            if (!isNameMakeDuplicate(name)) {
                userRepository.updateName(id, name);
                log.info("Updating user name {} with id: {}", name, id);
            } else {
                throw new EntityAlreadyExistsException("Entity with NAME " + name + " already exists.",
                        LocalTime.now());
            }
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now());
        }
    }

    @Transactional
    public void updateEmail(Long id, String email) {
        if (userRepository.existsById(id)) {
            email = email.trim();
            if (!isEmailMakeDuplicate(email)) {
                userRepository.updateEmail(id, email);
                log.info("Updating user email {} with id: {}", email, id);
            } else {
                throw new EntityAlreadyExistsException("Entity with EMAIL " + email + " already exists.",
                        LocalTime.now());
            }
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now());
        }
    }

    @Transactional
    public void updateBirth(Long id, String birth) {
        if (userRepository.existsById(id)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date;
            try {
                date = LocalDate.parse(birth, formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidEntityException("User birth date is incorrect", LocalTime.now());
            }
            int age = Period.between(date, LocalDate.now()).getYears();
            userRepository.updateBirth(id, date, age);
            log.info("Updating user birth {} and age {} with id: {}", birth, age, id);
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now());
        }
    }

    @Transactional
    public void updatePassword(Long id, String password) {
        if (userRepository.existsById(id)) {
            userRepository.updatePassword(id, password.trim());
            log.info("Updating user password {} with id: {}",password, id);
        } else {
            throw new ResourceNotFoundException("User with id: " + id + " not found", LocalTime.now());
        }
    }

    private void isUserEmpty(Optional<User> user, String name) {
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(name, LocalTime.now());
        }
    }

    private boolean isEmailMakeDuplicate(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private boolean isNameMakeDuplicate(String name) {
        return userRepository.findUserByName(name).isPresent();
    }

    private void checkUserFields(User user) {
        if (isEmailMakeDuplicate(user.getEmail())) {
            throw new EntityAlreadyExistsException("Entity with EMAIL " + user.getEmail() + " already exists.",
                    LocalTime.now());
        } else if (isNameMakeDuplicate(user.getName())) {
            throw new EntityAlreadyExistsException("Entity with NAME " + user.getName() + " already exists.",
                    LocalTime.now());
        }
    }

    private void trimFields(User user) throws NullPointerException {
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(user.getPassword().trim());
    }
}
