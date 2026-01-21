# CRUD Prueba – Spring Boot 🚀

> API REST desarrollada con **Spring Boot**, con una **arquitectura orientada a dominio (DDD)** y siguiendo principios de **Clean Architecture**, orientada a autenticación y gestión de usuarios.

---

## Descripción del Proyecto

Este proyecto implementa una API backend con una **arquitectura orientada a dominio (Domain-Driven Design)**, estructurada en capas **Domain – Application – Infrastructure**, permitiendo un código desacoplado, mantenible y escalable.

Incluye:

* Autenticación con JWT
* Gestión de usuarios (CRUD)
* Validaciones y manejo de excepciones
* Seguridad con Spring Security

---

## Arquitectura (Orientada a Dominio)

```text
src/main/java/com/example/crud_prueba
├── domain/            # Lógica de negocio pura
│   ├── model/
│   ├── repository/
│   └── service/
├── application/       # Casos de uso y DTOs
│   ├── dto/
│   ├── usecases/
│   └── validators/
├── infrastructure/    # Implementaciones técnicas
│   ├── persistence/
│   ├── security/
│   └── controllers/
└── CrudPruebaApplication.java
```

---

## Tecnologías Utilizadas

| Tecnología      | Versión                 |
| --------------- | ----------------------- |
| Java            | 17                      |
| Spring Boot     | 3.x                     |
| Spring Web      | 3.x                     |
| Spring Data JPA | 3.x                     |
| Spring Security | 6.x                     |
| Hibernate       | 6.x                     |
| Maven           | 3.9+                    |
| JWT             | JJWT                    |
| Base de Datos   | H2 / PostgreSQL / MySQL |

> Las versiones exactas pueden revisarse en el archivo `pom.xml`.

---

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de contar con:

*  **Java JDK 17**
*  **Maven 3.9 o superior**
*  Base de datos (opcional si se usa H2)
*  IDE recomendado: IntelliJ IDEA o VS Code

---

##  Levantamiento del Sistema

###  Clonar el repositorio

```bash
git clone https://github.com/Borys1no/crud_prueba.git
cd crud_prueba
```

### Configuración de la base de datos (opcional)

Editar `application.properties` o `application.yml`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crud_prueba
spring.datasource.username=usuario
spring.datasource.password=password
```

### Compilar el proyecto

```bash
mvn clean install
```

### Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O ejecutando la clase principal desde el IDE:

```java
CrudPruebaApplication.java
```

---

## Acceso a la API

* Base URL:

```text
http://localhost:8080
```

* Ejemplo de endpoint:

```text
POST /api/auth/login
```

---

## Seguridad

* Autenticación basada en **JWT**
* Configuración centralizada con `SecurityConfig`
* Filtros de seguridad personalizados

---


## Autor

**Borys Cereceda**
Programador Junior

---

## Licencia

Proyecto con fines académicos y de aprendizaje.

