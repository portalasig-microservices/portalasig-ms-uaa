# portalasig-ms-uaa

Microservicio de autenticación y gestión de usuarios (UAA) de PortalAsig: emisión y validación de tokens JWT (OAuth2), administración de usuarios, clientes y roles.

## Tests y cobertura

La suite de integración corre contra un MySQL 8 real levantado con Testcontainers
(`AbstractMysqlIntegrationTest` de core-lib), con rollback transaccional por test.

```bash
mvn clean verify
```

`verify` ejecuta checkstyle, los tests y el chequeo de cobertura JaCoCo:
el build **falla** si la cobertura baja de **70% líneas / 60% ramas**.

> Nota: con Docker ≥ 29 puede ser necesario `~/.docker-java.properties` con
> `api.version=1.44` para que Testcontainers hable con el daemon.

Estado actual: **84.6% líneas / 64.4% ramas** (46 tests).
