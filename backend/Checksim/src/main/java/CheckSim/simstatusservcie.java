package CheckSim;

import java.util.List; // Import List
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class simstatusservcie {

    @Autowired
    private simstatusrepo repository;

    // Change the return type to List<simstatus>
    public List<simstatus> getStatusByEmail(String email) {
        return repository.findByEmail(email);
    }
}