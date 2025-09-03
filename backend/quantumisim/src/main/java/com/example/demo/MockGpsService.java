// In a new file: MockGpsService.java
package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class MockGpsService {

    private final String[] possibleLocations = {"New York, NY", "London, UK", "Tokyo, JP", "Sydney, AU"};
    private final Random random = new Random();

    // Simulates fetching the real-time location of a user's device
    public String getCurrentUserLocation() {
        // In a real app, this would involve complex logic.
        // For our simulation, we just pick a random location.
        String location = possibleLocations[random.nextInt(possibleLocations.length)];
        System.out.println("GPS_SERVICE: Simulated user real-time location is: " + location);
        return location;
    }
}