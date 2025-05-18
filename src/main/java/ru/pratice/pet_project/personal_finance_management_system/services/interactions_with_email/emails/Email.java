package ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@FieldDefaults(level = AccessLevel.PROTECTED)
@Data
@Slf4j
public abstract class Email {
    @NonFinal String header;
    @NonFinal String content;

    public abstract void makeEmailContent();

    @SuppressWarnings("All")
    protected String convertHtmlContentToString(String fileName) {
        try (var fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return readFile(fileInputStream);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @SneakyThrows
    private String readFile(InputStream fileInputStream) {
        byte[] buffer = fileInputStream.readAllBytes();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
