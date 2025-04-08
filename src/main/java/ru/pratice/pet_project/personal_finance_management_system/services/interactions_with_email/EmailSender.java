package ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails.Email;


@Component
@AllArgsConstructor
@Slf4j
public class EmailSender {
    static String EMAIL_FROM = "financemanagmeentsystem@gmail.com";
    JavaMailSender mailSender;

    public void sendEmail(Email email, String toEmail) {
        try {
            MimeMessage message = createMimeMessage(email, toEmail);
            mailSender.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    private MimeMessage createMimeMessage(Email email, String toEmail) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(EMAIL_FROM);
        helper.setTo(toEmail);
        helper.setSubject(email.getHeader());
        helper.setText(email.getContent(), true);
        return mimeMessage;
    }
}

