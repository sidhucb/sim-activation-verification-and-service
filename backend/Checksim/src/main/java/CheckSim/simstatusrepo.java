package CheckSim;

import java.util.List; // Import List
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface simstatusrepo extends JpaRepository<simstatus, Long> {

    // This will now return a list of all statuses for an email
    List<simstatus> findByEmail(String email);

    // Keep this if you also search by username, or change it to a List too
    Optional<simstatus> findByUsername(String username);
}