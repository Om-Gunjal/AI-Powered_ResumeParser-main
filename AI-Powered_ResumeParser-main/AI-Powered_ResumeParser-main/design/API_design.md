# **API Design Table**:


| Endpoint                          | Method | Query Parameters            | Description                                        | Success                | Error                  | Authentication           |
|----------------------------------|--------|-----------------------------|----------------------------------------------------|------------------------|------------------------|--------------------------|
| `/api/applicant`                  | POST   | None                        | Parse and save a resume                            | `201 Created`          | `400 Bad Request`, `409 Conflict` | Yes (`USER`, `ADMIN`)    |
| `/api/applicants`                 | GET    | None                        | Retrieve all stored resumes                         | `200 OK`               | `401 Unauthorized`     | Yes (`USER`, `ADMIN`)    |
| `/api/applicant/{id}`             | PUT    | None                        | Update an applicant's resume                        | `200 OK`               | `404 Not Found`        | Yes (`ADMIN`)            |
| `/api/applicant/{id}`             | DELETE | None                        | Delete an applicant's resume                        | `204 No Content`       | `404 Not Found`        | Yes (`ADMIN`)            |
| `/api/applicants/role/{roleName}` | GET    | None                        | Get applicants by their role                        | `200 OK`               | `404 Not Found`        | Yes (`USER`, `ADMIN`)    |
| `/api/roles/{applicantId}`        | GET    | None                        | Get roles associated with an applicant              | `200 OK`               | `404 Not Found`        | Yes (`USER`, `ADMIN`)    |
| `/api/applicant/searchByFirstname`| GET    | `firstname`                 | Search applicants by firstname                      | `200 OK`               | `404 Not Found`        | Yes (`USER`, `ADMIN`)    |
| `/api/applicant/searchByLastname` | GET    | `lastname`                  | Search applicants by lastname                       | `200 OK`               | `404 Not Found`        | Yes (`USER`, `ADMIN`)    |
| `/api/applicant/searchByEmail`    | GET    | `email`                     | Search applicants by email                          | `200 OK`               | `404 Not Found`        | Yes (`USER`, `ADMIN`)    |
| `/api/health`                     | GET    | None                        | Check if the API is running                         | `200 OK`               | None                   | No                       |

---

# **JSON Request & Response Schemas:**

### 1. **POST /api/applicant** *(Create Applicant)*

**Request Body:**
```json
{
  "resumeText": "string"
}
```

**Response Body:**
```json
{
  "id": 0,
  "firstname": "string",
  "lastname": "string",
  "email": "string",
  "roleFitnessList": [
    {
      "roleName": "string",
      "fitness": 0
    }
  ]
}
```

**Error Example (`409 Conflict`):**
```json
{
    "status": 409,
    "error": "Conflict",
    "message": "Applicant with this email already exists.",
    "path": "/api/applicant"
}
```

---

### 2. **PUT /api/applicant/{id}** *(Update Applicant)*

**Request & Response Body:**
```json
{
  "id": 0,
  "firstname": "string",
  "lastname": "string",
  "email": "string",
  "roleFitnessList": [
    {
      "roleName": "string",
      "fitness": 0
    }
  ]
}
```

---

### 3. **GET /api/applicants** *(Retrieve All Applicants)*

**Response Body:**
```json
[
  {
    "id": 0,
    "firstname": "string",
    "lastname": "string",
    "email": "string",
    "roleFitnessList": [
      {
        "roleName": "string",
        "fitness": 0
      }
    ]
  }
]
```

---

### 4. **GET /api/applicants/role/{roleName}** *(Filter by Role)*

**Response Body:** *(Same as the `/api/applicants` response)*

---

### 5. **GET /api/applicant/searchByFirstname** *(Search by Firstname)*

**Response Body:** *(Same as the `/api/applicants` response)*

---

### 6. **GET /api/applicant/searchByEmail** *(Search by Email)*

**Response Body:**
```json
{
  "id": 0,
  "firstname": "string",
  "lastname": "string",
  "email": "string",
  "roleFitnessList": [
    {
      "roleName": "string",
      "fitness": 0
    }
  ]
}
```

---

### 7. **POST /register** *(User Registration)*

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "confirmPassword": "string",
  "role": "string",
  "passwordsMatch": true
}
```

**Response Body:**
```json
{
  "id": 0,
  "username": "string",
  "role": "string"
}
```

---

### 8. **POST /login** *(User Login)*

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response Body:**
```json
{
  "token": "string",
  "user": {
    "id": 0,
    "username": "string",
    "role": "string"
  }
}
```

---

### 9. **GET /api/health** *(API Health Check)*

**Response Body:**
```json
"Resume Parser API is running!"
```

---
