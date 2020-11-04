package com.thehecklers.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	WebClient.Builder builder() {
		return WebClient.builder();
	}

	@Bean
	WebClient client(WebClient.Builder builder) {
		return builder.build();
	}
}

@RestController
@RequestMapping("/")
@AllArgsConstructor
class ReportController {
	private final WebClient client;

	@GetMapping
	String helloFromReportService() {
		return "Hello from Report Service!";
	}

	@GetMapping("/aircraft")
	Iterable<Aircraft> getAircraftFromAircraftService() {
		return client.get()
				.uri("http://aircraft-service/aircraft")
				.retrieve()
				.bodyToFlux(Aircraft.class)
				.collectList()
				.block();
	}

	@GetMapping("/aircraft/{id}")
	Aircraft getAircraftByReg(@PathVariable String id) {
		return client.get()
				.uri("http://aircraft-service/aircraft/{id}", id)
				.retrieve()
				.bodyToMono(Aircraft.class)
				.block();
	}
}

@Data
class Aircraft {
	private String reg;
	private String type, description;
}