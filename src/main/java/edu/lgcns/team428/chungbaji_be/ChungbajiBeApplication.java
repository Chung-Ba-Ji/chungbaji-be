package edu.lgcns.team428.chungbaji_be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChungbajiBeApplication {
	public static void main(String[] args) {

		Dotenv env = Dotenv.configure().ignoreIfMissing().load();

		env.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())	
		);

		SpringApplication.run(ChungbajiBeApplication.class, args);
	}

}
