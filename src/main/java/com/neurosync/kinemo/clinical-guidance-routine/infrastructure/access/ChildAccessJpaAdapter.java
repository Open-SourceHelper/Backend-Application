package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.ChildAccessPort;
import com.neurosync.kinemo.crisisalert.application.port.CareNetworkAuthorizationPort;
import org.springframework.stereotype.Component;
/** Reuses BC03's public CareNetwork port; does not couple BC04 to BC03 JPA entities. */
@Component
public class ChildAccessJpaAdapter implements ChildAccessPort {
    private final SpringDataParentChildRepository parents;
    private final SpringDataProfessionalAssignmentRepository professionals;
    private final CareNetworkAuthorizationPort careNetwork;
    public ChildAccessJpaAdapter(SpringDataParentChildRepository p,SpringDataProfessionalAssignmentRepository pr,
                                 CareNetworkAuthorizationPort c){parents=p;professionals=pr;careNetwork=c;}
    private boolean parent(String child,Actor actor){
        return actor.hasRole("PARENT") && parents.existsByChildIdAndParentIdAndActiveTrue(child,actor.id());
    }
    private boolean caregiver(String child,Actor actor){
        return actor.hasRole("CAREGIVER") && careNetwork.findAuthorizedCaregiverIds(child).contains(actor.id());
    }
    public boolean isAssignedProfessional(String child,Actor actor){
        return actor.hasRole("PSYCHOLOGIST") && professionals.existsByChildIdAndProfessionalIdAndActiveTrue(child,actor.id());
    }
    public boolean mayRead(String child,Actor actor){
        return parent(child,actor)||caregiver(child,actor)||isAssignedProfessional(child,actor);
    }
    public boolean mayCreateRoutine(String child,Actor actor){return parent(child,actor)||isAssignedProfessional(child,actor);}
    public boolean mayExecuteRoutine(String child,Actor actor){return mayRead(child,actor);}
}
