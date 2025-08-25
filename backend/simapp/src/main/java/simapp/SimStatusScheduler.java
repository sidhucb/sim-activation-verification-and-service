package simapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SimStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(SimStatusScheduler.class);

    @Autowired
    private SimRequestRepository simRequestRepository;

    /**
     * This scheduled task runs every hour to activate SIMs that have been
     * in the 'Provisioning' state for at least 24 hours.
     */
    @Scheduled(fixedRate = 3600000) // 3,600,000 ms = 1 hour
    @Transactional
    public void activateProvisionedSims() {
        log.info("Running scheduled task to activate provisioned SIMs...");

        // 1. Find all requests that are currently 'Provisioning'
        List<SimRequest> provisioningRequests = simRequestRepository.findByStatus("Provisioning");

        if (provisioningRequests.isEmpty()) {
            log.info("No SIMs found in 'Provisioning' status.");
            return;
        }

        // 2. The time threshold for activation (24 hours ago)
        Instant activationThreshold = Instant.now().minus(24, ChronoUnit.HOURS);

        // 3. Iterate and update status if the time has passed
        for (SimRequest request : provisioningRequests) {
            if (request.getProvisionedAt() != null && request.getProvisionedAt().isBefore(activationThreshold)) {
                log.info("Activating SIM for request ID: {}", request.getRequestId());
                request.setStatus("Active");
                simRequestRepository.save(request);
            }
        }
        log.info("Scheduled activation task finished.");
    }
}