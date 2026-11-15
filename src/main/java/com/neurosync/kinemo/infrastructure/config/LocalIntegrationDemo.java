package com.neurosync.kinemo.infrastructure.config;

import com.neurosync.kinemo.child.application.ChildAccessService;
import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.ProfessionalAssignmentJpaEntity;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.SpringDataProfessionalAssignmentRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import java.util.Comparator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@Profile("local | evaluation")
class LocalIntegrationDemo implements ApplicationRunner {
    static final long FAMILY = 7001;
    static final long CAREGIVER = 7002;
    static final long PSYCHOLOGIST = 7003;

    private final ChildProfiles profiles;
    private final ChildAccessService access;
    private final SpringDataProfessionalAssignmentRepository professionals;
    private final Environment environment;
    private Long childId;

    LocalIntegrationDemo(ChildProfiles profiles, ChildAccessService access,
                         SpringDataProfessionalAssignmentRepository professionals, Environment environment) {
        this.profiles = profiles;
        this.access = access;
        this.professionals = professionals;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var existing = profiles.list(FAMILY).stream().min(Comparator.comparing(child -> child.getId()));
        var child = existing.orElseGet(() ->
                profiles.create(FAMILY, "Mateo", "Ejemplo", LocalDate.of(2018, 4, 14)));
        childId = child.getId();
        if (existing.isPresent() && environment.matchesProfiles("evaluation")) return;
        String id = childId.toString();
        if (!access.activeCaregivers(id).contains(Long.toString(CAREGIVER))) {
            var email = "cuidador@example.test";
            var network = profiles.invite(FAMILY, childId, email,
                    new PermissionSet(Set.of(ChildAccessService.READ_PROFILE)));
            var invitation = network.getInvitations().stream()
                    .filter(item -> email.equals(item.getInviteeEmail()) && item.isValid())
                    .findFirst().orElseThrow();
            profiles.accept(CAREGIVER, email, childId, invitation.getId());
        }
        String assignmentId = "local-angular-" + id;
        if (!professionals.existsById(assignmentId)) {
            professionals.save(new ProfessionalAssignmentJpaEntity(
                    assignmentId, id, Long.toString(PSYCHOLOGIST), true));
        }
    }

    Long childId() {
        return childId;
    }
}
