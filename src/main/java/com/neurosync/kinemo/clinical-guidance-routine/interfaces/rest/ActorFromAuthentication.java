package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import org.springframework.security.core.Authentication;
import java.util.stream.Collectors;
public final class ActorFromAuthentication {
    private ActorFromAuthentication(){}
    public static Actor from(Authentication authentication){
        return new Actor(authentication.getName(),authentication.getAuthorities().stream()
                .map(a->a.getAuthority()).filter(v->v.startsWith("ROLE_"))
                .map(v->v.substring("ROLE_".length())).collect(Collectors.toSet()));
    }
}
