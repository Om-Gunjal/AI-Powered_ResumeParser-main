package com.example.resumeparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Scanner;

@SpringBootApplication
public class ResumeparserApplication {

	public static void main(String[] args) {

		if (System.getProperty("OPENAI_API_KEY") == null) {
			Scanner scanner = new Scanner(System.in);

			System.out.print("Enter OpenAI API Key: ");
			String openAiApiKey = scanner.nextLine();

			System.setProperty("OPENAI_API_KEY", openAiApiKey);

		}
		SpringApplication.run(ResumeparserApplication.class, args);
	}

}
