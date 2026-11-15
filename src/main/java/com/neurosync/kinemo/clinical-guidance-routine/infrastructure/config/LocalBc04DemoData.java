package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.config;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.*;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import java.util.UUID;
@Configuration @Profile("local")
public class LocalBc04DemoData {
    @Bean ApplicationRunner seedBc04(SpringDataParentChildRepository parents,
            SpringDataProfessionalAssignmentRepository professionals,
            SpringDataPracticalGuideRepository guides){
        return args->{
            if(!parents.existsById("bc04-parent-001"))
                parents.save(new ParentChildJpaEntity("bc04-parent-001","child-001","parent-demo-001",true));
            if(!professionals.existsById("bc04-assignment-001"))
                professionals.save(new ProfessionalAssignmentJpaEntity("bc04-assignment-001","child-001","psychologist-guide-001",true));
            UUID id=UUID.fromString("a3218d28-3494-4cc8-aafa-cf8e4248f001");
            if(!guides.existsById(id)) guides.save(new PracticalGuideJpaEntity(id,
                 "Preparar una transición cotidiana","TRANSITIONS",
                 "Consulta las pautas personalizadas acordadas con el profesional y anticipa el cambio de actividad con apoyos conocidos.",true));
            UUID second=UUID.fromString("a3218d28-3494-4cc8-aafa-cf8e4248f002");
            if(!guides.existsById(second))guides.save(new PracticalGuideJpaEntity(second,
                "Organización de la rutina visual","ROUTINES",
                "Muestra las actividades en el orden previsto y utiliza las indicaciones acordadas con la familia.",true));
        };
    }
}
