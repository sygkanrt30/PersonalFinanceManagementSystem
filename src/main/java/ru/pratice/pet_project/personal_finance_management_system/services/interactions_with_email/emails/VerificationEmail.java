package ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails;

import java.util.Random;


public class VerificationEmail extends Email {
    int code;
    static String CONTENT_HTML_FILE_NAME = "VerificationEmailContent.html";
    static String EMAIL_HEADER = "Подтвердите email на в приложение Personal Finance Management System";

    public VerificationEmail() {
        this.code = makeRandomCode();
        this.header = EMAIL_HEADER;
        makeEmailContent();
    }

    @Override
    public void makeEmailContent() {
        content = convertHtmlContentToString(CONTENT_HTML_FILE_NAME);
        content = insertCheckCode(code);
    }

    private String insertCheckCode(int code) {
        return content.replace("{{code}}", String.valueOf(code));
    }

    private int makeRandomCode() {
        Random r = new Random();
        return r.nextInt(100000, 1000000);
    }
}
