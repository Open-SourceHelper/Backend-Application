# Kinemo Alert - Backend

Backend de Kinemo desarrollado con Java 21, Spring Boot, Maven y Spring Data JPA.

En este repositorio estamos trabajando la API de la aplicación. Por ahora, la parte desarrollada es **Crisis Alert Management (BC03)**, encargada de registrar y dar seguimiento a las alertas de cuidado.

## ¿Qué hace el BC03?

- Permite activar una alerta SOS para un niño.
- Verifica que el cuidador tenga autorización.
- Muestra el historial de alertas.
- Permite cambiar el estado de una alerta: `ACTIVE → ATTENDED → CLOSED`.
- Guarda avisos internos para otros cuidadores autorizados.

Los avisos todavía se guardan en la base de datos; **no se envían SMS ni notificaciones push reales**.

## Estructura del código

Organizamos el módulo siguiendo DDD y separando comandos y consultas con CQRS:

- `domain`: modelos y reglas de negocio.
- `application`: casos de uso, comandos y consultas.
- `infrastructure`: conexión con la base de datos y adaptadores.
- `interfaces/rest`: endpoints que utiliza el frontend.

## Cómo ejecutarlo

1. Abrir este repositorio en **IntelliJ IDEA**.
2. Importar `pom.xml` como proyecto Maven y seleccionar **JDK 21**.
3. Ejecutar `KinemoBackendApplication` con el perfil `local`. En la configuración de ejecución se puede agregar esta opción de VM:

   ```text
   -Dspring.profiles.active=local
   ```

4. La API estará disponible en `http://localhost:8080`.
5. Para probar las solicitudes, abrir `kinemo-alert.http` desde IntelliJ.

También se pueden ejecutar las pruebas con:

```bash
mvn test
```

### Usuarios de prueba

Solo funcionan con el perfil `local`:

| Usuario | Contraseña |
|---|---|
| `caregiver-alert-001` | `demo123` |
| `caregiver-alert-002` | `demo123` |

Ambos tienen acceso al niño `child-001` en los datos de prueba. **Estas cuentas no deben utilizarse en un servidor público.**

## Endpoints

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/v1/crisis-alerts` | Crear una alerta |
| GET | `/api/v1/crisis-alerts?childId=child-001` | Ver alertas del niño |
| PATCH | `/api/v1/crisis-alerts/{id}/attend` | Marcar como atendida |
| PATCH | `/api/v1/crisis-alerts/{id}/close` | Cerrar una alerta |
| GET | `/api/v1/crisis-alerts/notifications` | Consultar avisos internos |

Ejemplo para crear una alerta:

```json
{
  "childId": "child-001",
  "description": "Necesita acompañamiento"
}
```

## Pendiente

Falta conectar este backend con el frontend Angular, que actualmente utiliza JSON Server. También debemos integrar la autenticación del BC01 y la red de cuidadores del BC02.

Por ahora usamos H2 para las pruebas locales. El backend aún no está listo para desplegarse como servicio público.
