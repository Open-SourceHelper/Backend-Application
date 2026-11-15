package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.storage;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.VisualStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.*;
@Component @Profile("local")
public class LocalVisualStorageAdapter implements VisualStoragePort {
    private final Path folder=Path.of(System.getProperty("user.dir"),".local-bc04-uploads").toAbsolutePath();
    public StoredVisual store(String filename,byte[] content) {
        if(content==null||content.length==0||content.length>2*1024*1024)
            throw new UseCaseException("INVALID_UPLOAD","La imagen debe pesar entre 1 byte y 2 MB.");
        boolean png=content.length>8&&(content[0]&255)==137&&content[1]==80&&content[2]==78&&content[3]==71&&
                content[4]==13&&content[5]==10&&content[6]==26&&content[7]==10;
        boolean jpeg=content.length>4&&(content[0]&255)==255&&(content[1]&255)==216&&(content[2]&255)==255;
        if(!png&&!jpeg)throw new UseCaseException("INVALID_UPLOAD","El contenido no es JPG/PNG.");
        String clean=filename==null?"visual":filename.replace('\\','/');
        clean=clean.substring(clean.lastIndexOf('/')+1).replaceAll("[^a-zA-Z0-9._-]","_");
        if(clean.isBlank())clean="visual";
        if(clean.length()>180)clean=clean.substring(0,180);
        String type=png?"image/png":"image/jpeg";
        String key=UUID.randomUUID().toString()+(png?".png":".jpg");
        try {Files.createDirectories(folder);Files.write(folder.resolve(key),content,StandardOpenOption.CREATE_NEW);}
        catch(IOException e){throw new UseCaseException("STORAGE_ERROR","No fue posible guardar la imagen.");}
        return new StoredVisual(key,clean,type);
    }
    public byte[] load(String key){
        Path target=safe(key);
        try{return Files.readAllBytes(target);}catch(IOException e){
            throw new UseCaseException("ASSET_NOT_FOUND","Imagen no disponible.");}
    }
    public void delete(String key){
        try{Files.deleteIfExists(safe(key));}catch(IOException ignored){}
    }
    private Path safe(String key){
        if(key==null||!key.matches("[a-f0-9-]{36}\\.(png|jpg)"))
            throw new UseCaseException("INVALID_UPLOAD","Clave de almacenamiento inválida.");
        Path target=folder.resolve(key).normalize();
        if(!target.startsWith(folder))throw new UseCaseException("INVALID_UPLOAD","Ruta inválida.");
        return target;
    }
}
