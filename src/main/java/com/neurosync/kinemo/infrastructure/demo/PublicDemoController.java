package com.neurosync.kinemo.infrastructure.demo;

import com.neurosync.kinemo.observationhistory.application.EpisodeDetails;
import com.neurosync.kinemo.observationhistory.domain.model.ProfessionalComment;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@Profile("public-demo")
@RequestMapping("/api/v1/public-demo")
class PublicDemoController {
    private static final String CHILD = "demo-mateo";
    private static final String CAREGIVER = "demo-caregiver";
    private static final String PROFESSIONAL = "demo-professional";

    record Health(String status) {}
    record Child(String id, String firstName, String lastName, String birthDate) {}
    record Activity(String id, String title, int durationMinutes, String status) {}
    record Routine(String id, String title, LocalDate scheduledDate, List<Activity> activities) {}
    record Guideline(String id, String title, String instructions, String status) {}
    record Guide(String id, String title, String content, String category) {}
    record Snapshot(String source, Child child, List<EpisodeDetails> episodes,
                    List<Routine> routines, List<Guideline> guidelines, List<Guide> guides) {}

    @GetMapping("/health")
    Health health() {
        return new Health("ok");
    }

    @GetMapping("/snapshot")
    Snapshot snapshot() {
        var now = Instant.now();
        var today = LocalDate.now(ZoneId.of("America/Lima"));
        var review = now.minus(2, ChronoUnit.DAYS);
        var episodes = List.of(
                new EpisodeDetails(id(1), CHILD, CAREGIVER, null,
                        "Se inquietó al cambiar de actividad. Un aviso visual le ayudó a retomar la rutina.",
                        now.minus(1, ChronoUnit.DAYS), "mild", 5, "Cambio de actividad", "Aviso visual",
                        null, null, List.of()),
                new EpisodeDetails(id(2), CHILD, CAREGIVER, null,
                        "El ruido del recreo le incomodó. Después de una pausa volvió a la actividad.",
                        now.minus(4, ChronoUnit.DAYS), "moderate", 12, "Ruido", "Pausa en un lugar tranquilo",
                        review, PROFESSIONAL, List.of(new ProfessionalComment(id(4), PROFESSIONAL,
                                "Registro ficticio de una revisión profesional.", review))),
                new EpisodeDetails(id(3), CHILD, CAREGIVER, null,
                        "Completó su rutina de la mañana con apoyo visual.",
                        now.minus(8, ChronoUnit.DAYS), null, null, null, null, null, null, List.of()));
        var routines = List.of(new Routine("demo-routine", "Prepararse para salir", today, List.of(
                new Activity("demo-activity-1", "Preparar la mochila", 10, "COMPLETED"),
                new Activity("demo-activity-2", "Revisar el horario visual", 5, "PENDING"))));
        var guidelines = List.of(new Guideline("demo-guideline", "Pauta de transiciones",
                "Ejemplo de una pauta compartida por la profesional con la familia.", "PUBLISHED"));
        var guides = List.of(new Guide("demo-guide", "Organizar la rutina visual",
                "Ejemplo de una guía de consulta para preparar las actividades del día.", "RUTINAS"));
        return new Snapshot("spring-boot", new Child(CHILD, "Mateo", "Ejemplo", "2018-04-14"),
                episodes, routines, guidelines, guides);
    }

    private static UUID id(int number) {
        return new UUID(0, number);
    }
}
