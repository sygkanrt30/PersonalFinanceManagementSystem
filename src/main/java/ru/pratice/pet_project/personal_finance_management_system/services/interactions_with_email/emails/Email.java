package ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@FieldDefaults(level = AccessLevel.PROTECTED)
@Data
@Slf4j
public abstract class Email {
    @NonFinal String header;
    @NonFinal String content;

    public abstract void makeEmailContent();

    protected String convertHtmlContentToString(String fileName) {
        try (var fileInputStream = new FileInputStream("src/main/resources/emails/" + fileName)) {
            return readFile(fileInputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String readFile(FileInputStream fileInputStream) throws IOException {
        byte[] buffer = fileInputStream.readAllBytes();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
