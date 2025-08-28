package simapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling; // Import this

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling // Add this annotation to enable the @Scheduled task
public class SimappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimappApplication.class, args);
	}

}