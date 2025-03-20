package net.cechacek.examples.debezium.sb.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class AccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessApplication.class, args);
	}

}
