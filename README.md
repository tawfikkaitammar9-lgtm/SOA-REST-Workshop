# SOA REST Workshop 1

Java JAX-RS REST API for `UniteEnseignement` and `Module`, implemented as a Maven WAR and designed for Tomcat 9.

## Team

- Student(s): _Add your group members here before submission._
- Repository: _Add your GitHub repository URL here after publishing._

## Prerequisites

- JDK 11 or newer
- Maven 3.8+
- Apache Tomcat 9

## Build and deploy

```powershell
mvn clean package
```

Copy `target/soa-rest-workshop.war` to Tomcat's `webapps` folder and start Tomcat. The base URL is:

`http://localhost:8080/soa-rest-workshop/api`

The data store is in memory, so it resets whenever the application restarts.

## Endpoints

| Method | Endpoint | Request format | Result |
| --- | --- | --- | --- |
| POST | `/UE` | XML | Create a UE (201) |
| GET | `/UE` | - | List UEs |
| GET | `/UE?semestre=2` | - | List UEs for a semester |
| GET | `/UE?code=1` | - | Get a UE by code |
| PUT | `/UE/{id}` | XML | Update a UE |
| DELETE | `/UE/{id}` | - | Delete a UE (204) |
| POST | `/modules` | JSON | Create a module (201) |
| GET | `/modules` | - | List modules |
| GET | `/modules/{matricule}` | - | Get a module |
| PUT | `/modules/{matricule}` | JSON | Update a module |
| DELETE | `/modules/{matricule}` | - | Delete a module (204) |
| GET | `/modules/UE?codeUE=1` | - | List modules belonging to a UE |

All successful responses are JSON. Invalid input returns `400`, an unknown resource returns `404`, and duplicate create requests return `409`.

## Example requests

Create the UE before creating its modules:

```xml
<uniteEnseignement>
  <code>1</code>
  <domaine>Computer Science</domaine>
  <responsable>Dr. Martin</responsable>
  <credits>6</credits>
  <semestre>2</semestre>
</uniteEnseignement>
```

```json
{
  "matricule": "MOD-101",
  "nom": "Web Services",
  "coefficient": 3,
  "volumeHoraire": 42,
  "type": "PROFESSIONNEL",
  "uniteEnseignement": { "code": 1 }
}
```

An importable Postman collection is included at `postman/SOA-REST-Workshop.postman_collection.json`.

## Postman test evidence

The `screenshots/` folder contains successful Postman test evidence for every required endpoint:

| Resource | Verified operations |
| --- | --- |
| UniteEnseignement | Create (201), list (200), filter by semester (200), find by code (200), update (200), delete (204) |
| Module | Create (201), list (200), find by matricule (200), filter by UE (200), update (200), delete (204) |
