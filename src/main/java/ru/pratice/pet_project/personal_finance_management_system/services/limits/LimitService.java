package ru.pratice.pet_project.personal_finance_management_system.services.limits;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

@Service
@Slf4j
@AllArgsConstructor
public class LimitService {
    private final LimitRepository limitRepository;
    private final UserService userService;

    public LimitTracker getLimitById(Long id) throws ResourceNotFoundException {
        return limitRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Limit with id: " + id + " not found"));
    }

    public LimitTracker getLimitByUsername(String username) throws ResourceNotFoundException {
        return limitRepository.findLimitByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Limit with username: " + username + " not found")
        );
    }

    public void deleteLimitById(Long id) {
        isLimitExistsById(id);
        limitRepository.deleteById(id);
        log.info("Limit with id: {} deleted.", id);
    }

    public void deleteLimitByUsername(String username) {
        limitRepository.deleteLimitsByUsername(username);
        log.info("Limit with username: {} deleted.", username);
    }

    private void isLimitExistsById(Long id) {
        if (!limitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Limit with id: " + id + " not found");
        }
    }


    public void saveLimit(LimitTracker limit) {
        limit.setUsername(limit.getUsername().trim());
        limit.setTotalAmount(0L);
        checkExistenceOfUserByUsername(limit.getUsername());
        checkAmountByCorrectness(limit.getLimitAmount());
        limitRepository.save(limit);
        log.info("Limit with id: {} saved.", limit.getId());
    }

    @Transactional
    public void updateLimitAmount(Long id, Long limitAmount) {
        isLimitExistsById(id);
        checkAmountByCorrectness(limitAmount);
        limitRepository.updateLimitAmount(id, limitAmount);
        log.info("Limit amount with id: {} updated.", id);
    }

    @Transactional
    public void updateTotalAmount(Long id, Long totalAmount) {
        isLimitExistsById(id);
        checkAmountByCorrectness(totalAmount);
        limitRepository.updateTotalAmount(id, totalAmount);
        log.info("Total amount with id: {} updated.", id);
    }

    private void checkExistenceOfUserByUsername(String username) {
        userService.getUserByName(username);
    }

    private void checkAmountByCorrectness(Long amount) throws InvalidEntityException {
        if (amount <= 0) {
            throw new InvalidEntityException("Amount must be greater than zero");
        }
    }
}
