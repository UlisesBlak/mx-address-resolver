# mx-address-resolver

Formulario web para la captura de direcciones de México, con validación y resolución automática de valores (estado, municipio, localidad, colonia) a partir del código postal, usando el catálogo oficial proporcionado.

Prueba técnica DE-TT04 v1.1 — Desarrollador Java Web.

## Stack técnico

- **Backend:** Java 21, Spring Boot 4.1.0, Spring Data JPA, Hibernate 7
- **Base de datos:** PostgreSQL 17
- **Frontend:** HTML, CSS y JavaScript vanilla (sin frameworks), servido como recurso estático por Spring Boot
- **Build:** Maven
- **Contenedores:** Podman + podman-compose (compatible con Docker), o instalación nativa de PostgreSQL como alternativa

## Requisitos previos

- JDK 21 o superior
- Maven (o usar el wrapper incluido: `./mvnw` en Linux/macOS, `mvnw.cmd` en Windows)
- Podman y podman-compose, o Docker y Docker Compose — **o**, alternativamente, PostgreSQL 17 instalado de forma nativa (ver Opción B más abajo)

> Los comandos de este documento usan sintaxis de Linux/macOS. En Windows, reemplaza `podman compose` por `docker compose` si usas Docker Desktop, y `./mvnw` por `mvnw.cmd`.

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

Hay dos formas de tener PostgreSQL disponible, elige la que se ajuste a tu entorno.

#### Opción A: con contenedores (Podman o Docker) — recomendada

```bash
podman compose up -d
```

En Windows con Docker Desktop, usa `docker compose up -d` en su lugar.

Esto levanta un contenedor PostgreSQL 17 y carga automáticamente el catálogo la primera vez que se crea el volumen de datos.

> **Nota:** el archivo `catalogos_mx.sql` usa sentencias `INSERT` individuales (no `COPY` en bloque), por lo que la carga inicial de las ~220,000 filas puede tardar varios minutos. Solo ocurre una vez; en arranques posteriores el volumen ya tiene los datos.

Verifica que la carga terminó:

```bash
podman exec -it mx-address-resolver-db psql -U postgres -d mx_address_resolver -c "SELECT COUNT(*) FROM codigo_postal;"
```

(en Windows/Docker: `docker exec -it mx-address-resolver-db psql -U postgres -d mx_address_resolver -c "SELECT COUNT(*) FROM codigo_postal;"`)

Debería devolver `95748`.

#### Opción B: PostgreSQL instalado directamente (sin contenedores)

Si no cuentas con Podman/Docker (por ejemplo, si WSL2 no está disponible o presenta problemas en tu entorno de Windows), puedes instalar PostgreSQL 17 nativamente:

1. Descarga e instala PostgreSQL 17 desde [postgresql.org/download](https://www.postgresql.org/download/)
2. Durante la instalación, define una contraseña para el usuario `postgres` (usa `postgres` para que coincida con la configuración por defecto de este proyecto, o ajusta `application.properties` si usas otra)
3. Crea la base de datos:
```bash
   psql -U postgres -c "CREATE DATABASE mx_address_resolver;"
```
4. Carga el catálogo:
```bash
   psql -U postgres -d mx_address_resolver -f db/catalogos_mx.sql
```
   (en Windows: `psql -U postgres -d mx_address_resolver -f db\catalogos_mx.sql`)
5. Verifica la carga:
```bash
   psql -U postgres -d mx_address_resolver -c "SELECT COUNT(*) FROM codigo_postal;"
```
   Debería devolver `95748`.

> **Troubleshooting (Windows) — error de codificación al cargar el catálogo:** si aparece un error como `carácter con secuencia de bytes 0x81 en codificación «WIN1252» no tiene equivalente en la codificación «UTF8»`, el cliente `psql` de Windows está interpretando el archivo con la codificación de la terminal (WIN1252) en vez de UTF-8, aunque el archivo y la base de datos sí estén en UTF-8 correctamente. Antes de cargar el `.sql`, ejecuta en la misma terminal:
> ```cmd
> set PGCLIENTENCODING=UTF8
> ```
> y vuelve a correr el comando de carga (paso 4). Esta variable solo aplica a la sesión de terminal actual.

> **Troubleshooting (Windows) — el instalador no agrega `psql` al PATH:** si después de instalar PostgreSQL el comando `psql --version` no se reconoce, agrega manualmente `C:\Program Files\PostgreSQL\17\bin` a la variable de entorno `Path` del sistema (Panel de control → Variables de entorno → Variables del sistema → `Path` → Editar → Nuevo), y abre una terminal nueva.

### 4. Ejecutar la aplicación

**Importante:** espera a que la base de datos termine de cargar el catálogo completo antes de este paso (ver nota del paso 3). Iniciar la aplicación mientras la base de datos todavía está cargando datos causará un error de conexión.

```bash
./mvnw spring-boot:run
```

En Windows: `mvnw.cmd spring-boot:run`

La aplicación queda disponible en **http://localhost:8080**.

### Configuración de conexión

La conexión a la base de datos se define en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mx_address_resolver
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Estos valores coinciden con las credenciales por defecto definidas en `podman-compose.yml`. Si usas la Opción B (instalación nativa) con una contraseña distinta, ajusta `spring.datasource.password` para que coincida.

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
- **Se documentan dos vías de base de datos** (contenedores y nativa): asegura que el proyecto pueda evaluarse aunque el entorno no tenga Podman/Docker/WSL2 disponible o configurado correctamente.
- **Inyección de dependencias por constructor**: tanto `DireccionService` como `DireccionController` reciben sus dependencias vía constructor en lugar de `@Autowired` en campos. Con un único constructor, Spring la infiere automáticamente; este enfoque mantiene las dependencias explícitas, inmutables (`final`) y facilita las pruebas con mocks.