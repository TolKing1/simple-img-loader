## Image Loader API Documentation

### Retrieve Image by Name
Retrieves an image by its name.

**URL:** `/images/{imageName}`  
**Method:** `GET`  
**URL Parameters:**
- `imageName` : string - The name of the image to retrieve.

**Response:**
- `200 OK` - Successfully retrieved the image.
    - Body: The image file.
    - Content-Type: The MIME type of the image.
- `404 Not Found` - Image not found.

---

### Search Images
Searches for images based on a description.

**URL:** `/images/search/`  
**Method:** `GET`  
**Query Parameters:**
- `text` : string - The text to search for in image descriptions.

**Response:**
- `200 OK` - Successfully retrieved the list of images.
    - Body: JSON array of Image objects.
- `400 Bad Request` - Invalid request parameters.

---

### Save Image
Uploads and saves an image.

**URL:** `/image/save`  
**Method:** `POST`  
**Request Body:**
- `description` : string - Description of the image.
- `image` : file - The image file to upload.

**Response:**
- `200 OK` - Image saved successfully.
    - Body: Text message indicating success.
- `500 Internal Server Error` - Failed to save the image.
    - Body: Error message.

---

#### Image Object
Represents an image entity.

- `id` : string - The unique identifier of the image.
- `description` : string - Description of the image.
- `fileName` : string - The name of the image file.

---

### Database Configuration

Before using the API, you need to configure the database settings in the `application.properties` file. Make sure to set up the following properties:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/telegramImages
spring.datasource.username=postgres
spring.datasource.password=password

# Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
```
Replace localhost:5432 with the actual host and port of your PostgreSQL database, and update the username and password accordingly.

Feel free to adjust the instructions according to your specific setup!