package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.storage;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.VisualStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component @Profile("!local")
public class DisabledVisualStorageAdapter implements VisualStoragePort {
    private UseCaseException disabled(){return new UseCaseException("STORAGE_NOT_CONFIGURED","Configure un almacenamiento privado antes de desplegar.");}
    public StoredVisual store(String file,byte[] bytes){throw disabled();}
    public byte[] load(String key){throw disabled();}
    public void delete(String key){throw disabled();}
}
