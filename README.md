# Microservicio de Inventario - Gestión de Productos (Ferretería)

Sistema Back-End empresarial desarrollado en **Java 17 / 21** con **Spring Boot 3.x**, diseñado bajo una arquitectura limpia por capas (Layered Architecture) para la administración centralizada de la entidad **Producto** en una red de ferreterías.

---

## 🚀 Tecnologías y Librerías Utilizadas

- **Lenguaje:** Java 17 / 21 LTS
- **Framework:** Spring Boot 3.3.4
  - **Spring Web:** Exposición de endpoints RESTful con `ResponseEntity`.
  - **Spring Data JPA:** Persistencia relacional, repositorios e índices optimizados.
  - **Spring Security:** Autenticación JWT sin estado (stateless) y HTTP Basic.
  - **Spring Validation:** Validación declarativa con Bean Validation (`@NotNull`, `@NotBlank`, `@Size`).
- **Base de Datos:** PostgreSQL (Producción / Desarrollo) & H2 Database en memoria (Entornos de Test).
- **Seguridad Criptográfica:** JJWT (`io.jsonwebtoken:0.12.6`), algoritmo HMAC-SHA256 y `BCryptPasswordEncoder`.
- **Documentación de API:** SpringDoc OpenAPI 3.0 & Swagger UI (`springdoc-openapi-starter-webmvc-ui:2.6.0`).
- **Arquitectura de Clases:** POJOs puros sin dependencia de Lombok (constructores manuales, inyección `@Autowired` explícita y getters/setters nativos para máxima compatibilidad con VS Code y JDK 17).
- **Internacionalización (i18n):** `MessageSource` y `AcceptHeaderLocaleResolver` (Español `es` e Inglés `en`).
- **Pruebas Automatizadas:** JUnit 5, Mockito, Spring Boot Test y MockMvc.

---

## 📁 Estructura del Proyecto

```text
ferreteria-inventario/
├── pom.xml                                     # Definición de dependencias y plugins Maven
├── README.md                                   # Documentación técnica del proyecto
└── src/
    ├── main/
    │   ├── java/com/ferreteria/inventario/
    │   │   ├── InventarioApplication.java      # Clase principal Spring Boot
    │   │   ├── config/                         # Configuraciones del sistema
    │   │   │   ├── I18nConfig.java             # MessageSource y LocaleResolver
    │   │   │   ├── OpenApiConfig.java          # Configuración Swagger y seguridad JWT
    │   │   │   └── SecurityConfig.java         # Cadena de filtros de seguridad y RBAC
    │   │   ├── controller/                     # Controladores REST API
    │   │   │   ├── AuthController.java         # Autenticación y emisión de tokens
    │   │   │   └── ProductoController.java     # CRUD completo de productos
    │   │   ├── dto/                            # Data Transfer Objects
    │   │   │   ├── ApiResponseDto.java         # Envoltorio estándar de respuesta JSON
    │   │   │   ├── AuthRequestDto.java         # Credenciales de inicio de sesión
    │   │   │   ├── AuthResponseDto.java        # Token JWT y datos de sesión
    │   │   │   ├── ErrorResponseDto.java       # Estructura estandarizada de errores
    │   │   │   ├── ProductoRequestDto.java     # Payload validado de entrada
    │   │   │   └── ProductoResponseDto.java    # Datos de salida de producto
    │   │   ├── exception/                      # Manejo desacoplado de errores
    │   │   │   ├── BadRequestException.java    # Error de reglas de negocio (400)
    │   │   │   ├── GlobalExceptionHandler.java # @RestControllerAdvice global con i18n
    │   │   │   └── ResourceNotFoundException.java # Recurso no encontrado (404)
    │   │   ├── model/
    │   │   │   ├── entity/
    │   │   │   │   └── Producto.java           # Entidad JPA con auditoría
    │   │   │   └── enums/
    │   │   │       ├── EstadoProducto.java     # DISPONIBLE, RESERVADA, DANADA
    │   │   │       └── RoleEnum.java           # ROL_ADMIN, ROL_BODEGA, ROL_VENTAS
    │   │   ├── repository/
    │   │   │   └── ProductoRepository.java     # Spring Data JPA Repository
    │   │   ├── security/                       # Implementación de seguridad JWT
    │   │   │   ├── jwt/
    │   │   │   │   ├── JwtAccessDeniedHandler.java        # Manejo 403 Forbidden
    │   │   │   │   ├── JwtAuthenticationEntryPoint.java   # Manejo 401 Unauthorized
    │   │   │   │   ├── JwtAuthenticationFilter.java       # Interceptor de tokens Bearer
    │   │   │   │   └── JwtTokenProvider.java              # Criptografía y claims JWT
    │   │   │   ├── model/
    │   │   │   │   └── UserPrincipal.java                 # Adaptador UserDetails
    │   │   │   └── service/
    │   │   │       └── CustomUserDetailsService.java      # Usuarios y roles RBAC
    │   │   └── service/                        # Capa de lógica de negocio
    │   │       ├── ProductoService.java        # Contrato de interfaz de servicio
    │   │       └── impl/
    │   │           └── ProductoServiceImpl.java# Implementación con transacciones
    │   └── resources/
    │       ├── application.yml                 # Configuración principal (PostgreSQL, JWT)
    │       ├── schema.sql                      # Script DDL e inserción de datos iniciales
    │       └── i18n/                           # Mensajes multi-idioma
    │           ├── messages.properties         # Mensajes por defecto
    │           ├── messages_es.properties      # Mensajes en Español
    │           └── messages_en.properties      # Mensajes en Inglés
    └── test/
        ├── java/com/ferreteria/inventario/
        │   ├── controller/
        │   │   └── ProductoControllerTest.java # Pruebas unitarias de la API Web (MockMvc)
        │   ├── integration/
        │   │   └── ProductoIntegrationTest.java# Pruebas de integración E2E (Spring Boot Test)
        │   └── service/
        │       └── ProductoServiceImplTest.java# Pruebas unitarias de negocio (Mockito)
        └── resources/
            └── application-test.yml            # Configuración de base de datos H2 de test
```

---

## 👥 Matriz de Seguridad y Roles (RBAC)

El sistema implementa tres roles de usuario con credenciales de prueba pre-configuradas con hash BCrypt:

| Usuario | Contraseña | Rol asignado | Permisos en la API de Productos |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | `ROL_ADMIN` | **Total:** Crear, Consultar, Listar, Actualizar y Eliminar. |
| `bodega`| `bodega123`| `ROL_BODEGA`| **Operativo:** Crear, Consultar, Listar y Actualizar. |
| `ventas`| `ventas123`| `ROL_VENTAS`| **Consulta:** Consultar por ID y Listar catálogo. |

---

## 🌐 Internacionalización (i18n)

Todas las respuestas de validación y de confirmación de operaciones están internacionalizadas. Puede alternar el idioma enviando la cabecera HTTP estándar `Accept-Language`:

- `Accept-Language: es` -> Respuestas y validaciones en Español.
- `Accept-Language: en` -> Respuestas y validaciones en Inglés.

---

## 📖 Documentación Interactiva (Swagger / OpenAPI)

Una vez iniciado el aplicativo, acceda a la consola interactiva Swagger UI:
- **URL de Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Especificación OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

*Para autenticar peticiones en Swagger UI, haga clic en el botón `Authorize` e ingrese el token JWT obtenido del endpoint `/api/v1/auth/login`.*

---

## 📡 Catálogo de Endpoints REST

### 1. Autenticación (`/api/v1/auth`)

#### Iniciar Sesión y Generar Token
- **Método:** `POST`
- **Ruta:** `/api/v1/auth/login`
- **Permisos:** Público
- **Cuerpo de Solicitud:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "username": "admin",
    "roles": ["ROL_ADMIN"],
    "expiresInMs": 86400000
  },
  "timestamp": "2026-10-13T19:15:00"
}
```

---

### 2. Productos (`/api/v1/productos`)

#### Crear un Producto
- **Método:** `POST`
- **Ruta:** `/api/v1/productos`
- **Roles:** `ROL_ADMIN`, `ROL_BODEGA`
- **Cabeceras:** `Authorization: Bearer <TOKEN>`, `Accept-Language: es`
- **Cuerpo de Solicitud:**
```json
{
  "nombre": "Rotomartillo SDS Plus 800W",
  "categoria": "Herramientas Eléctricas",
  "descripcion": "Rotomartillo de 3 funciones: taladro, percutor y cincelador.",
  "estado": "DISPONIBLE"
}
```
- **Respuesta (201 Created):**
```json
{
  "success": true,
  "message": "Producto creado exitosamente",
  "data": {
    "id": 1,
    "nombre": "Rotomartillo SDS Plus 800W",
    "categoria": "Herramientas Eléctricas",
    "descripcion": "Rotomartillo de 3 funciones: taladro, percutor y cincelador.",
    "estado": "DISPONIBLE",
    "createdAt": "2026-10-13T19:20:00",
    "updatedAt": "2026-10-13T19:20:00"
  },
  "timestamp": "2026-10-13T19:20:00"
}
```

#### Consultar Producto por ID
- **Método:** `GET`
- **Ruta:** `/api/v1/productos/{id}`
- **Roles:** `ROL_ADMIN`, `ROL_BODEGA`, `ROL_VENTAS`
- **Cabeceras:** `Authorization: Bearer <TOKEN>`

#### Listar Productos con Filtros Opcionales
- **Método:** `GET`
- **Ruta:** `/api/v1/productos?categoria=Herramientas+Manuales&estado=DISPONIBLE`
- **Roles:** `ROL_ADMIN`, `ROL_BODEGA`, `ROL_VENTAS`
- **Cabeceras:** `Authorization: Bearer <TOKEN>`

#### Actualizar Producto
- **Método:** `PUT`
- **Ruta:** `/api/v1/productos/{id}`
- **Roles:** `ROL_ADMIN`, `ROL_BODEGA`
- **Cabeceras:** `Authorization: Bearer <TOKEN>`
- **Cuerpo de Solicitud:**
```json
{
  "nombre": "Rotomartillo SDS Plus 850W Pro",
  "categoria": "Herramientas Eléctricas",
  "descripcion": "Modelo mejorado con sistema antivibración AVT.",
  "estado": "RESERVADA"
}
```

#### Eliminar Producto
- **Método:** `DELETE`
- **Ruta:** `/api/v1/productos/{id}`
- **Roles:** `ROL_ADMIN` *(Exclusivo)*
- **Cabeceras:** `Authorization: Bearer <TOKEN>`

---

## 🧪 Ejecución de Pruebas

Para ejecutar la batería completa de pruebas unitarias y de integración:

```bash
mvn clean test
```

### Cobertura de Pruebas:
1. **`ProductoServiceImplTest`:** Valida la lógica de negocio, unicidad de nombres, lanzamientos de excepciones de negocio y no existencia, y mapeo de entidades con Mockito.
2. **`ProductoControllerTest`:** Valida la capa web con MockMvc, serialización JSON, validaciones de Bean Validation (`@NotNull`, `@NotBlank`) y verificación de seguridad RBAC por método.
3. **`ProductoIntegrationTest`:** Prueba de extremo a extremo que realiza login, genera token JWT real, persiste entidades en memoria H2 y valida la internacionalización en español e inglés.

---

## 🛠️ Despliegue y Configuración de Base de Datos

En `application.yml` o mediante variables de entorno del sistema:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ferreteria_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=mi_password_segura
```

Para compilar y empaquetar el microservicio:

```bash
mvn clean package -DskipTests
java -jar target/inventario-backend-1.0.0.jar
```
