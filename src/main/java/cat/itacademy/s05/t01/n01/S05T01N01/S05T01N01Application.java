package cat.itacademy.s05.t01.n01.S05T01N01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "cat.itacademy.s05.t01.n01.S05T01N01")
public class S05T01N01Application {

	public static void main(String[] args) {
		SpringApplication.run(S05T01N01Application.class, args);
	}

}
