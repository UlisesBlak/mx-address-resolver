# mx-address-resolver

Formulario web para la captura de direcciones de México, con validación y resolución automática de valores (estado, municipio, localidad, colonia) a partir del código postal, usando el catálogo oficial proporcionado.

Prueba técnica DE-TT04 v1.1 — Desarrollador Java Web.

## Stack técnico

- **Backend:** Java 21, Spring Boot 4.1.0, Spring Data JPA, Hibernate 7
- **Base de datos:** PostgreSQL 17
- **Frontend:** HTML, CSS y JavaScript vanilla (sin frameworks), servido como recurso estático por Spring Boot
- **Build:** Maven
- **Contenedores:** Podman + podman-compose (compatible con Docker)

## Requisitos previos

- JDK 21 o superior
- Maven (o usar el wrapper `./mvnw` incluido, no requiere instalación aparte)
- Podman y podman-compose (o Docker y Docker Compose)

## Configuración y despliegue

### 1. Clonar el repositorio

```bash
git clone https://github.com/UlisesBlak/mx-address-resolver.git
cd mx-address-resolver
```

### 2. Colocar el catálogo de base de datos

El archivo `catalogos_mx.sql` (proporcionado junto con el enunciado de la prueba) no se incluye en este repositorio por su tamaño (~22 MB). Colócalo en:

```
db/catalogos_mx.sql
```

### 3. Levantar la base de datos

```bash
podman compose up -d
```

Esto levanta un contenedor PostgreSQL 17 y carga automáticamente el catálogo la primera vez que se crea el volumen de datos.

> **Nota:** el archivo `catalogos_mx.sql` usa sentencias `INSERT` individuales (no `COPY` en bloque), por lo que la carga inicial de las ~220,000 filas puede tardar varios minutos. Solo ocurre una vez; en arranques posteriores el volumen ya tiene los datos.

Verifica que la carga terminó:

```bash
podman exec -it mx-address-resolver-db psql -U postgres -d mx_address_resolver -c "SELECT COUNT(*) FROM codigo_postal;"
```

Debería devolver `72725`.

### 4. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en **http://localhost:8080**.

### Configuración de conexión

La conexión a la base de datos se define en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mx_address_resolver
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Estos valores coinciden con las credenciales por defecto definidas en `podman-compose.yml`.

## Endpoints de la API

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/direccion/estados` | Lista todos los estados |
| `GET` | `/api/direccion/municipios?estado={clave}` | Municipios de un estado |
| `GET` | `/api/direccion/localidades?estado={clave}` | Localidades de un estado |
| `GET` | `/api/direccion/codigo-postal/{cp}` | Resuelve estado, municipios, localidades y colonias a partir de un código postal |
| `POST` | `/api/direccion/validar` | Valida la consistencia de una dirección completa contra el catálogo |

## Decisiones técnicas

- **`ddl-auto=validate`**: Hibernate valida el esquema contra las entidades al arrancar, sin modificar la base de datos existente. El catálogo ya viene con su esquema definido, así que la app no debe crear ni alterar tablas.
- **Llaves compuestas con `@EmbeddedId`**: las tablas `municipio`, `localidad` y `colonia` usan llave primaria compuesta en el catálogo original, modeladas con relaciones JPA reales (`@ManyToOne` + `@MapsId`) en lugar de campos planos, para reflejar correctamente el modelo de datos.
- **Colonia solo se relaciona con `codigo_postal`**, no con `municipio` ni `localidad`: así está diseñado el catálogo original, y es correcto — esa información ya es derivable a través del código postal.
- **`spring.jpa.open-in-view=false`**: se desactiva explícitamente para evitar mantener conexiones de base de datos abiertas durante el renderizado de la respuesta.
- **Frontend servido por Spring Boot** (mismo origen): evita configuración de CORS y permite levantar toda la aplicación con un solo comando.