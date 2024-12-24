package org.sid.customer_service;
import org.sid.customer_service.entities.Customer;

import org.sid.customer_service.repo.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(CustomerRepository customerRepository){
		return args -> {
			customerRepository.saveAllAndFlush(List.of(
					Customer.builder().name("ayoub").email("ayoub@gmail.com").build(),
					Customer.builder().name("adil").email("adil@gmail.com").build(),
					Customer.builder().name("ahmad").email("ahmad@gmail.com").build()
			));

			customerRepository.findAll().forEach(System.out::println);
		};

	}
}
