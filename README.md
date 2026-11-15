# Kinemo Backend

API REST con Spring Boot, Java 21 y Spring Data JPA.

## Ejecución local

```powershell
mvn clean verify
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

El perfil `local` usa H2 en memoria y escucha únicamente en `127.0.0.1:8080`. Swagger UI está en `http://localhost:8080/swagger-ui/index.html` y el documento OpenAPI en `http://localhost:8080/v3/api-docs`.

Para las operaciones de demostración del niño `child-001`, usa HTTP Basic. Las cuentas de familia, cuidador y profesional son `parent-demo-001`, `caregiver-alert-001` y `psychologist-guide-001`. La contraseña local es `demo123`. Las pruebas también usan cuentas sintéticas con identificadores numéricos para validar el flujo de un perfil infantil registrado en el backend.

## Rutas principales

- **BC02 — Perfil infantil y red de cuidado.** `GET/POST /api/v1/children`. Bajo `/{id}` se actualiza el perfil infantil, se consulta y actualiza `/clinical-profile` y se administran `/care-network`, invitaciones y permisos.
- **BC03 — Alertas.** `POST /api/v1/crisis-alerts`, `GET /api/v1/crisis-alerts?childId=...`, `GET /api/v1/crisis-alerts/{id}`, cambios de estado en `/{id}/attend` y `/{id}/close`, y bandeja `/notifications`. Las notificaciones son registros internos en la API.
- **BC04 — Orientaciones y rutinas.** `/api/v1/routines`, `/api/v1/clinical-guidelines`, `/api/v1/practical-guides` y `/api/v1/patients`.
- **BC05 — Historial.** `GET/POST /api/v1/children/{childId}/episodes`. El listado acepta `page` y `size`. El detalle está en `/{id}`. Un psicólogo asignado puede comentar en `POST /{id}/comments` y registrar la primera revisión en `PUT /{id}/review`. Para vincular una alerta al registrar una crisis, envía el identificador de una alerta cerrada del mismo niño.

BC03, BC04 y BC05 consultan la red de cuidado para aplicar los permisos vigentes. BC05 también comprueba que la alerta esté cerrada y que el profesional esté asignado al niño. La prueba de integración comprueba el efecto de la revocación en los tres módulos.

Fuera del perfil `local`, el acceso a la API permanece bloqueado hasta integrar IAM. La aceptación real de invitaciones necesita una cuenta con correo verificado. El perfil `dev` requiere `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. El perfil `prod` valida que el esquema JPA ya exista.

## Prueba desde Angular

Con el perfil `local`, el backend crea un perfil infantil compartido y asigna tres cuentas de prueba. La sesión de Angular se obtiene en `GET /api/v1/local-demo/session` con HTTP Basic. Usa `7001` para familia, `7002` para cuidador y `7003` para psicóloga, con contraseña `demo123`. Estas cuentas y el endpoint de sesión están disponibles en el perfil `local`.

En el frontend, ejecuta `npm start` y abre `http://localhost:4200/backend-local`. Angular reenvía las solicitudes de esa vista a Spring Boot en el puerto 8080.

## Demo pública en Render

Crea un **Web Service** en Render desde este repositorio, selecciona **Docker** y el plan **Free**. Usa la rama que contiene el `Dockerfile` y deja la raíz en blanco. El contenedor arranca con el perfil `public-demo` y usa el puerto `PORT` de Render.

- **Health Check Path**: `/api/v1/public-demo/health`.
- **FRONTEND_ORIGIN**: el origen HTTPS del sitio Angular, sin ruta ni barra final.

La demo sirve únicamente datos ficticios en `GET /api/v1/public-demo/snapshot`. Las rutas de escritura y las consultas privadas quedan bloqueadas en este perfil.
