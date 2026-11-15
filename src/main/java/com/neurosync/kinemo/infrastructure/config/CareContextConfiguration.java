package com.neurosync.kinemo.infrastructure.config;

import com.neurosync.kinemo.child.application.ChildAccessService;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.CareNetworkAccess;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.LocalCareNetworkAccess;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.SpringDataProfessionalAssignmentRepository;
import com.neurosync.kinemo.crisisalert.application.port.CareNetworkAuthorizationPort;
import com.neurosync.kinemo.crisisalert.domain.model.AlertStatus;
import com.neurosync.kinemo.crisisalert.domain.repository.CrisisAlertRepository;
import com.neurosync.kinemo.crisisalert.infrastructure.carenetwork.JpaCareNetworkAuthorizationAdapter;
import com.neurosync.kinemo.observationhistory.application.ClosedAlertLookup;
import com.neurosync.kinemo.observationhistory.application.HistoryAccess;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
class CareContextConfiguration {
    private static final String LOCAL_DEMO_CHILD = "child-001";

    @Bean
    @Primary
    CareNetworkAuthorizationPort crisisCareAccess(ChildAccessService children,
            JpaCareNetworkAuthorizationAdapter demo, Environment environment) {
        return childId -> {
            if (LOCAL_DEMO_CHILD.equals(childId) && environment.acceptsProfiles(Profiles.of("local"))) {
                return demo.findAuthorizedCaregiverIds(childId);
            }
            var authorized = new ArrayList<>(children.activeCaregivers(childId));
            children.ownerId(childId).ifPresent(authorized::add);
            return List.copyOf(authorized);
        };
    }

    @Bean
    @Primary
    CareNetworkAccess routineCareAccess(ChildAccessService children,
                ObjectProvider<LocalCareNetworkAccess> localAccess) {
        return new CareNetworkAccess() {
            @Override
            public boolean isParent(String childId, String actorId) {
                var demo = localAccess.getIfAvailable();
                return LOCAL_DEMO_CHILD.equals(childId) && demo != null
                        ? demo.isParent(childId, actorId) : children.isOwner(childId, actorId);
            }

            @Override
            public boolean isCaregiver(String childId, String actorId) {
                var demo = localAccess.getIfAvailable();
                return LOCAL_DEMO_CHILD.equals(childId) && demo != null
                        ? demo.isCaregiver(childId, actorId) : children.activeCaregivers(childId).contains(actorId);
            }
        };
    }

    @Bean
    HistoryAccess historyAccess(CareNetworkAccess care,
               SpringDataProfessionalAssignmentRepository assignments) {
        return (actorId, childId) -> {
            if (assignments.existsByChildIdAndProfessionalIdAndActiveTrue(childId, actorId)) {
                return Optional.of(HistoryAccess.Role.PSYCHOLOGIST);
            }
            if (care.isParent(childId, actorId)) {
                return Optional.of(HistoryAccess.Role.FAMILY);
            }
            if (care.isCaregiver(childId, actorId)) {
                return Optional.of(HistoryAccess.Role.CAREGIVER);
            }
            return Optional.empty();
        };
    }

    @Bean
    ClosedAlertLookup closedAlertLookup(CrisisAlertRepository alerts) {
        return (alertId, childId) -> {
            UUID id;
            try {
                id = UUID.fromString(alertId);
            } catch (IllegalArgumentException ignored) {
                return false;
            }
            return alerts.findById(id)
                    .filter(alert -> alert.status() == AlertStatus.CLOSED && childId.equals(alert.childId()))
                    .isPresent();
        };
    }
}
