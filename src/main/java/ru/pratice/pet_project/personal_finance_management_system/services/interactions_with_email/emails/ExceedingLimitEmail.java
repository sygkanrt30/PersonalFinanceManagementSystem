package ru.pratice.pet_project.personal_finance_management_system.services.interactions_with_email.emails;


public class ExceedingLimitEmail extends Email {
    long limitAmount;
    long totalAmount;
    static String CONTENT_HTML_FILE_NAME = "ExceedingLimitEmailContent.html";
    static String EMAIL_HEADER = "ПРЕВЫШЕНИЕ ЛИМИТА!!!";

    public ExceedingLimitEmail(long limitAmount, long totalAmount) {
        this.limitAmount = limitAmount;
        this.totalAmount = totalAmount;
        this.header = EMAIL_HEADER;
        makeEmailContent();
    }

    @Override
    public void makeEmailContent(){
        content = convertHtmlContentToString(CONTENT_HTML_FILE_NAME);
        content = insertAmounts(limitAmount, totalAmount);
    }

    private String insertAmounts(long limitAmount, long totalAmount) {
        content = content.replace("{{limitAmount}}", String.valueOf(limitAmount));
        content = content.replace("{{totalAmount}}", String.valueOf(totalAmount));
        return content;
    }
}
