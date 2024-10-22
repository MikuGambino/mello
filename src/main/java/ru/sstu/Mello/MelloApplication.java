package ru.sstu.Mello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class MelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(MelloApplication.class, args);
	}

}
