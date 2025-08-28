package simapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

//NEW: SimLifecycleController.java (in package simapp)
@RestController
@RequestMapping("/api/sim/requests")
public class SimLifecycleController {

 @Autowired private SimRequestRepository repo;

 @PostMapping
 @Transactional
 public ResponseEntity<SimRequest> upsert(@RequestBody SimRequest dto) {
     var existing = repo.findByEmailAndRequestId(dto.getEmail(), dto.getRequestId());
     SimRequest entity = existing.orElseGet(SimRequest::new);
     entity.setRequestId(dto.getRequestId());
     entity.setEmail(dto.getEmail());
     entity.setUsername(dto.getUsername());
     entity.setStatus(dto.getStatus() == null ? "Pending" : dto.getStatus());
     // phoneNumber/provisionedAt set later in flow
     return ResponseEntity.ok(repo.save(entity));
 }
}
