# BC03: pulsera simulada y SOS manual

La simulación genera un único evento educativo. No representa un dispositivo físico ni realiza una detección médica.

## Ejecución

Iniciar Spring Boot con el perfil local y Angular con su proxy habitual. Entrar en /backend-local como Familia o Cuidador y abrir Alertas de cuidado.

- Iniciar simulación: el evento se genera a los cinco segundos y se registra sin pulsar SOS.
- Probar fallo de 1 s: un evento ocurre durante la desconexión; no se transmite ni se reenvía al reconectar. El cuidador debe revisar el historial y usar SOS manual.
- Cancelar o salir de la pantalla cancela el evento pendiente. El SOS manual también cancela el temporizador.
- La recuperación de la conexión no implica que la alerta se haya enviado.
- No se permiten dos alertas abiertas del mismo niño, aunque una sea manual y otra automática.

## API

POST /api/v1/crisis-alerts/simulated-bracelet-events

Payload: { "childId": "identificador del niño" }.

Usa la misma autenticación, autorización, transacción, destinatarios y ciclo ACTIVE → ATTENDED → CLOSED que la ruta manual. El responsable se obtiene del usuario autenticado. La descripción distingue el evento de pulsera simulada. No cambia el esquema de base de datos.

Una alerta nueva devuelve 201; una alerta abierta existente devuelve 409; sin sesión devuelve 401; sin acceso al niño devuelve 403; un identificador vacío devuelve 400.

Los avisos continúan siendo internos IN_APP/QUEUED. No se incorporan envíos externos ni credenciales de dispositivos. La simulación se ejecuta solo mientras la pantalla está abierta.

## Verificación

SimulatedBraceletIntegrationTest cubre creación automática, avisos al otro cuidador, exclusión de duplicados en ambos órdenes, SOS manual después del cierre, autenticación, permisos y validación. Ejecutar mvn verify.
