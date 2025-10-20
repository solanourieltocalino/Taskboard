# JavaRefresh / TaskBoard 🧩

**TaskBoard** es un proyecto de práctica backend en **Spring Boot 3 + MySQL**, parte del plan de refresco de conocimientos en Java.  
Incluye un CRUD completo con validaciones, paginación, manejo de errores y buenas prácticas de arquitectura.

---

## 🧱 Stack Tecnológico
- **Java 17 (Temurin)**
- **Spring Boot 3.5.x**
  - Spring Web  
  - Spring Validation  
  - Spring Data JPA  
- **MySQL 8** (con contenedor Docker)
- **Maven 3.9+**
- **Git + GitHub (flujo main/develop/feature)**

---

## ⚙️ Configuración Rápida

**Base de datos:**
```
schema: taskboard  
usuario: admin  
contraseña: admin
```

**Archivo `application.properties`:**
```
spring.datasource.url=jdbc:mysql://localhost:3306/taskboard
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## ▶️ Ejecución del proyecto
```bash
mvn clean package
mvn spring-boot:run
```
La app arranca en: [http://localhost:8080](http://localhost:8080)

---

## 🧩 Flujo de desarrollo (Git)

| Rama | Propósito |
|------|------------|
| **main** | Producción estable |
| **develop** | Integración y pruebas |
| **feature/*** | Desarrollo de nuevas funciones o fixes |

### Ejemplo de flujo:
```bash
git checkout develop
git pull
git checkout -b feature/tareas-crud
# cambios + commits
git push -u origin feature/tareas-crud
```
Luego, crear **Pull Request → develop** desde GitHub.

---

## ✅ DoD (Definition of Done)
- Código compila y levanta sin errores.  
- Tests pasan correctamente.  
- Validaciones y mensajes claros.  
- Código limpio, sin warnings.  
- README y Postman Collection actualizados.  

---

## 📄 Licencia
Proyecto de práctica personal – uso libre con fines educativos.
