package com.neurosync.kinemo.child.application;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;
import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChildAccessService {
    public static final String READ_PROFILE = "canReadChildProfile";

    private final ChildRepository children;

    public ChildAccessService(ChildRepository children) {
        this.children = children;
    }

    public boolean isOwner(String childId, String accountId) {
        return child(childId).map(value -> value.getParentId().toString().equals(accountId)).orElse(false);
    }

    public List<String> activeCaregivers(String childId) {
        return child(childId).map(value -> value.getCareNetwork().getMembers().stream()
                .filter(CareNetworkMember::hasAccess)
                .filter(member -> member.getRole() == CareNetworkRole.CAREGIVER)
                .filter(member -> member.getPermissions().contains(READ_PROFILE))
                .map(member -> member.getCaregiverId().toString()).distinct().toList()).orElseGet(List::of);
    }

    private Optional<Child> child(String id) {
        try {
            return children.findById(Long.valueOf(id));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
