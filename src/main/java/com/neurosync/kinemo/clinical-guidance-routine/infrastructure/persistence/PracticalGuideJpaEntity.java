package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.PracticalGuide;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="bc04_practical_guides")
public class PracticalGuideJpaEntity {
    @Id private UUID id;
    @Column(nullable=false,length=160) private String title;
    @Column(nullable=false,length=80) private String category;
    @Column(nullable=false,length=4000) private String content;
    @Column(nullable=false) private boolean published;
    protected PracticalGuideJpaEntity(){}
    public PracticalGuideJpaEntity(UUID id,String title,String category,String content,boolean published){
        this.id=id;this.title=title;this.category=category;this.content=content;this.published=published;
    }
    public PracticalGuide toDomain(){return new PracticalGuide(id,title,category,content,published);}
}
