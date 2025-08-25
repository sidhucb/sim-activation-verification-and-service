package CheckSim;

import java.util.List; // Import List
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/sim")
public class simstatuscontroller {

    @Autowired
    private simstatusservcie service;

    @GetMapping("/status/{identifier}")
    // Update the method to return a ResponseEntity containing a List
    public ResponseEntity<?> getSimStatus(@PathVariable String identifier) {
        List<simstatus> statuses = service.getStatusByEmail(identifier); // This now returns a List

        if (statuses.isEmpty()) {
            return ResponseEntity.status(404).body("No SIM status entries found for this user.");
        } else {
            return ResponseEntity.ok(statuses); // Return the entire list
        }
    }
}