package ru.pratice.pet_project.personal_finance_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinanceManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinanceManagementSystemApplication.class, args);
	}
}
//todo отправка сообщения на почту если лимит превышен
//todo внедрить лимиты в мое rest api