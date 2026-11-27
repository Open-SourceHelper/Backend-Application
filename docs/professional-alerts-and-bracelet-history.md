# BC03: historial de pulsera y revisión profesional

## Historial

La API conserva los eventos MONITORING_STARTED, SIMULATION_STOPPED, DISCONNECTED, SIGNAL_MISSED, RECONNECTED y ALERT_SENT. La fecha la asigna el servidor y el responsable procede de la sesión autenticada. Cada envío tiene un UUID; repetir una solicitud con los mismos datos no duplica el evento.

POST /api/v1/crisis-alerts/bracelet-history recibe eventId, childId y type. Solo la familia y los cuidadores autorizados pueden registrar eventos. GET de la misma ruta, con childId, devuelve los últimos 100 eventos y permite consultar al psicólogo asignado. Una detención no crea por sí misma una alerta de crisis.

## Notificaciones profesionales

GET /api/v1/crisis-alerts/professional-notifications devuelve las alertas no revisadas de los pacientes actualmente asignados. Incluye alertas cerradas que todavía no se revisaron, para no perder una incidencia entre sesiones.

GET /api/v1/crisis-alerts/professional-notifications/{id} obtiene el detalle sin marcarlo como revisado. POST /api/v1/crisis-alerts/professional-notifications/{id}/review confirma la revisión del profesional autenticado. Repetir la confirmación devuelve la misma fecha. Cada profesional confirma por separado.

Las asignaciones se vuelven a comprobar en cada operación. Confirmar no cambia el estado ACTIVE / ATTENDED / CLOSED ni sustituye el seguimiento de familia y cuidadores.

## Interfaz

La sesión del psicólogo consulta las alertas cada dos segundos mientras está abierta. El aviso utiliza un diálogo modal nativo con foco contenido, fondo inerte y sin cierre con Escape o clic exterior. El guard de rutas evita navegación interna mientras haya un aviso pendiente. Es necesario abrir el detalle y confirmar la revisión; si falla la confirmación, el aviso permanece pendiente. Al cerrar la sesión se detienen las consultas.

No se incorporan SMS, correo, push ni un dispositivo físico. Las entregas dependen de la conexión y de que la sesión esté abierta; esto no es un sistema de emergencias garantizado.

## Persistencia

Se agregan las tablas bc03_bracelet_events y bc03_professional_alert_reviews. El perfil local sigue usando H2 en memoria y pierde sus datos cuando se reinicia el backend. El perfil evaluation usa PostgreSQL y su configuración JPA actual; los cambios requieren validación de esquema al desplegar.

## Verificación

ProfessionalBraceletIntegrationTest comprueba deduplicación, historial, permisos, asignaciones, revisión por profesional, repetición de confirmaciones y separación del cierre. Las pruebas Angular cubren el diálogo, sus errores, el ciclo de vida y el aislamiento de sesiones. La prueba de navegador usa dos contextos independientes con cuentas locales ficticias; verifica llegada automática, bloqueo de navegación, recuperación de errores y persistencia de la revisión al recargar.

Ningún cambio de esta rama debe integrarse a develop sin la revisión del responsable del proyecto.
