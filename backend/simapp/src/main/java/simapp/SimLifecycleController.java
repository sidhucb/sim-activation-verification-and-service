package simapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sim/requests")
public class SimLifecycleController {

    @Autowired
    private SimRequestRepository repo;

    @PostMapping
    @Transactional
    public ResponseEntity<SimRequest> upsert(@RequestBody SimRequest dto) {
        Long userId = dto.getUserId();
        String requestId = dto.getRequestId();

        var existing = repo.findByUserIdAndRequestId(userId, requestId);
        SimRequest entity = existing.orElseGet(SimRequest::new);

        entity.setRequestId(requestId);
        entity.setUserId(userId);
        entity.setUsername(dto.getUsername());
        entity.setStatus(dto.getStatus() == null ? "Pending" : dto.getStatus());
        // phoneNumber and provisionedAt set later in flow

        return ResponseEntity.ok(repo.save(entity));
    }
}
