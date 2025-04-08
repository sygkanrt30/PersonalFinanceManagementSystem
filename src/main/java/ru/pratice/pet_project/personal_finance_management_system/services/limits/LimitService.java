package ru.pratice.pet_project.personal_finance_management_system.services.limits;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

@Service
@Slf4j
@AllArgsConstructor
public class LimitService {
    LimitRepository limitRepository;
    LimitExceedanceTracker limitExceedanceTracker;

    public LimitTracker getLimitById(long id) {
        return limitRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Limit with id: " + id + " not found"));
    }

    public LimitTracker getLimitByUsername(String username) {
        return limitRepository.findLimitByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Limit with username: " + username + " not found")
        );
    }

    private void isLimitExistsById(long id) {
        if (!limitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Limit with id: " + id + " not found");
        }
    }

    public void saveLimit(LimitTracker limit) {
        limit.setUsername(limit.getUsername().trim());
        limit.setTotalAmount(0L);
        checkAmountByCorrectness(limit.getLimitAmount());
        limitRepository.save(limit);
        log.info("Limit with id: {} saved.", limit.getId());
    }

    @Transactional
    public void updateLimitAmount(long id, long limitAmount) {
        isLimitExistsById(id);
        checkAmountByCorrectness(limitAmount);
        limitRepository.updateLimitAmount(id, limitAmount);
        log.info("Limit amount with id: {} updated.", id);
    }

    private void checkAmountByCorrectness(long amount) {
        if (amount <= 0) {
            throw new InvalidEntityException("Amount must be greater than zero");
        }
    }

    public void updateTotalAmount(User user, long previousAmount, long amount) {
        long difference = previousAmount - amount;
        if (difference >= 0) {
            limitExceedanceTracker.incrementTotalAmount(user, difference);
        } else
            limitExceedanceTracker.decrementTotalAmount(user, Math.abs(difference));
    }

    public void updateTotalAmountAfterSave(User user, long amount) {
        limitExceedanceTracker.incrementTotalAmount(user, amount);
    }
}
