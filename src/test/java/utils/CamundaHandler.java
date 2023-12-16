package utils;

import io.cucumber.messages.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CamundaHandler {
    private static final int waitForWorker = 300;

    /*private void checkInstanceCreated(String requestBody, String processDefinitionKey, int timeoutSeconds) {
        int attempts = 0;
        int statusCode;

        do {
            response = RestAssured.given()
                    .contentType("application/json")
                    .body(requestBody)
                    .post("http://localhost:8080/engine-rest/process-definition/key/" + processDefinitionKey + "/start");

            statusCode = response.getStatusCode();

            if (statusCode == 200 && !response.getBody().asString().isEmpty()) {
                break;
            }

            try {
                Thread.sleep(1000); // Wait for 1 second before retrying
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting to retry process instance creation", e);
                return;
            }

            attempts++;
        } while (attempts < timeoutSeconds);

        if (statusCode != 200) {
            throw new IllegalStateException("Failed to create process instance after " + timeoutSeconds + " attempts");
        }
    }
*/

    public static String getVariablesJson(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public static String createPostBody(String jsonFilePath) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream resource = null;
        JsonNode json = null;
        try {
            resource = CamundaHandler.class.getClassLoader().getResourceAsStream(jsonFilePath);
            json = mapper.readTree(resource);
        } catch (IOException e) {
            log.error("context", e);
        }
        return mapper.writeValueAsString(json);
    }

    public static void completeUserTask(String taskId) throws IOException {

        // Complete the task using the task ID
        RestAssured.given()
                .baseUri("http://localhost:8080")
                .basePath("/engine-rest/task/" + taskId + "/complete")
                .contentType("application/json")
                .body("") // You can pass variables to the task if needed in JSON format
                .when()
                .post()
                .then()
                .statusCode(204); // The expected status code for a successful completion is 204 No Content
    }

    private static final String CAMUNDA_API_URL = "http://localhost:8080/engine-rest"; // Replace with your Camunda API URL

    public static void completeExternalTask(String taskId, String variablesFileName) throws IOException {
        // Convert the JSON file to a String
        String variablesJson = new String(Files.readAllBytes(Paths.get("src/main/resources/json/" + variablesFileName)));

        // Complete the external task using RestAssured
        RestAssured.given()
                .baseUri(CAMUNDA_API_URL)
                .basePath("/external-task/" + taskId + "/complete")
                .contentType("application/json")
                .body(variablesJson)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(204); // Assuming 204 is the expected status code for successful completion
    }


    public static void waitForServiceTaskToComplete(String processInstanceId, String userTaskName) {
        boolean isTaskCompleted = false;
        int attempts = 0;
        int maxAttempts = 100; // Number of attempts to check for task completion
        int delayBetweenAttempts = 3000; // Delay between attempts in milliseconds (e.g., 3000ms = 3s)

        while (!isTaskCompleted && attempts < maxAttempts) {
            // Call Camunda API to check if the service task is completed
            // and the next user task is created

            // For example:
            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_API_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            List<String> taskIds = taskResponse.jsonPath().getList("id");
            isTaskCompleted = !taskIds.isEmpty(); // or check for a specific task ID or name

            if (!isTaskCompleted) {
                // If the task is not completed, wait for a while before checking again
                try {
                    Thread.sleep(delayBetweenAttempts);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted while waiting for the service task to complete.", e);
                }
            }
            attempts++;
        }

        if (!isTaskCompleted) {
            throw new RuntimeException("Service task did not complete within the expected time.");
        }
    }


}
