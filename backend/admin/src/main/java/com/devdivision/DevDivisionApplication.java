package com.devdivision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
// @EnableSwagger2
public class DevDivisionApplication {

	public static void main(String[] args) {
        System.out.println("Working directory = " + System.getProperty("user.dir"));
        SpringApplication.run(DevDivisionApplication.class, args);
	}

}
