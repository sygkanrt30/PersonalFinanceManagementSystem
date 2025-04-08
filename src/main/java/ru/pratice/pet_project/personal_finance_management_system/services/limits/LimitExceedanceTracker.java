package ru.pratice.pet_project.personal_finance_management_system.services.limits;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.limits.LimitTracker;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.EmailSender;
import ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails.ExceedingLimitEmail;

@SuppressWarnings("All")
@AllArgsConstructor
@Component
public class LimitExceedanceTracker {
    LimitRepository limitRepository;
    EmailSender emailSender;

    public  void incrementTotalAmount(User user, long differenceInSum) {
        limitRepository.incrementTotalAmount(user.getUsername(), differenceInSum);
        verificationOfLimitExcess(user);
    }

    public void decrementTotalAmount(User user, long differenceInSum) {
        limitRepository.decrementTotalAmount(user.getUsername(), differenceInSum);
    }

    private void verificationOfLimitExcess(User user) {
        LimitTracker limitTracker = limitRepository.findLimitByUsername(user.getUsername()).get();
        long totalAmount = limitTracker.getTotalAmount();
        long limitAmount = limitTracker.getLimitAmount();
        if (totalAmount > limitAmount) {
            emailSender.sendEmail(new ExceedingLimitEmail(limitAmount, totalAmount), user.getEmail());
        }
    }
}