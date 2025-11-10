package com.varun.pgm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PgmApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgmApplication.class, args);
	}

}
