    package stepdef;

    import io.cucumber.java.en.And;
    import io.cucumber.java.en.Given;
    import io.cucumber.java.en.Then;
    import io.cucumber.java.en.When;
    import io.restassured.RestAssured;
    import io.restassured.response.Response;
    import lombok.extern.slf4j.Slf4j;
    import utils.CamundaHandler;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.List;

    import static org.junit.Assert.*;
    import static utils.CamundaHandler.completeUserTask;

    @Slf4j
    public class OrderProcessSteps {

        private String CAMUNDA_LOCAL_URL = "http://localhost:8080/engine-rest";
        private Response response;
        private String processInstanceId;

        @Given("a new process instance is created")
        public void a_new_process_instance_is_created() throws IOException {
            log.info("Creating a new process instance");
            String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/camunda-variables.json")));
            String processDefinitionKey = "Process_0ff4f2m"; // Replace with your actual process definition key

            response = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .body(requestBody)
                    .post("/process-definition/key/" + processDefinitionKey + "/start");

            log.debug("Response: {}", response.getBody().asString());
            assertEquals(200, response.getStatusCode());

            processInstanceId = response.jsonPath().getString("id");
            assertNotNull(processInstanceId);
            log.info("Process instance created with ID: {}", processInstanceId);
        }

        @Then("a user task is created for the process instance")
        public void a_collect_info_task_is_created_for_the_process_instance() {
            log.info("Checking if a user task is created for the process instance");

            String userTaskName = "collect customer details";  // The ID of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .get("/task?processInstanceId=" + processInstanceId);

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            List<String> taskIds = taskResponse.jsonPath().getList("id");
            assertTrue("User task with id " + userTaskName + " was created", taskIds.contains(userTaskName));

            log.info("User task with name {} is created for the process instance", userTaskName);
        }

        @And("the user information is collected")
        public void theUserInformationIsCollected() throws IOException {log.info("Checking if a user task is created for the process instance");

            String userTaskName = "collect customer details";  // The name of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            String taskId = taskResponse.jsonPath().getString("id[0]");
            assertNotNull("User task with ID " + taskId + " was created", taskId);

            log.info("User task with name {} is created for the process instance", userTaskName);
            completeUserTask(taskId);
        }

        @And("the service availability check is passed")
        public void theServiceAvailabilityCheckIsPassed(){
            log.info("Service task {} is processing", "- check service availability -");
            CamundaHandler.waitForServiceTaskToComplete(processInstanceId, "select service package");
            log.info("Service task {} is completed", "- check service availability -");

        }

        @When("the customer selects a service package and agrees to terms")
        public void theCustomerSelectsAServicePackageAndAgreesToTerms() throws IOException {
            log.info("User task {} is processing", "- select service package -");

            String userTaskName = "select service package";  // The name of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            String taskId = taskResponse.jsonPath().getString("id[0]");
            assertNotNull("User task with ID " + taskId + " was created", taskId);

            log.info("User task with name {} is created for the process instance", userTaskName);
            completeUserTask(taskId);
        }

        @And("the credit check is approved")
        public void theCreditCheckIsApproved() {
            log.info("Service task {} is processing", "- check credit -");
            CamundaHandler.waitForServiceTaskToComplete(processInstanceId, "proceed for payment");
            log.info("Service task {} is completed", "- check credit -");

        }

        @And("the customer completes the payment")
        public void theCustomerCompletesThePayment() throws IOException {
            log.info("User task {} is processing", "- proceed for payment -");

            String userTaskName = "proceed for payment";  // The name of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            String taskId = taskResponse.jsonPath().getString("id[0]");
            assertNotNull("User task with ID " + taskId + " was created", taskId);

            log.info("User task with name {} is created for the process instance", userTaskName);
            completeUserTask(taskId);
        }

        @Then("the contract and terms care agreed")
        public void theContractAndTermsCareAgreed() throws IOException {
            log.info("User task {} is processing", "- prepare contract and terms -");

            String userTaskName = "prepare contract and terms";  // The name of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            String taskId = taskResponse.jsonPath().getString("id[0]");
            assertNotNull("User task with ID " + taskId + " was created", taskId);

            log.info("User task with name {} is created for the process instance", userTaskName);
            completeUserTask(taskId);
        }

        @And("the installation is scheduled")
        public void theInstallationIsScheduled() throws IOException {
            log.info("Checking if a user task is created for the process instance");

            String userTaskName = "schedule installation";  // The name of the user task

            Response taskResponse = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .queryParam("processInstanceId", processInstanceId)
                    .queryParam("taskName", userTaskName)
                    .when()
                    .get("/task");

            log.debug("Task Response: {}", taskResponse.getBody().asString());
            assertEquals(200, taskResponse.getStatusCode());

            String taskId = taskResponse.jsonPath().getString("id[0]");
            assertNotNull("User task with ID " + taskId + " was created", taskId);

            log.info("User task with name {} is created for the process instance", userTaskName);
            completeUserTask(taskId);
        }

        @And("the service is activated successfully")
        public void theServiceIsActivatedSuccessfully() {
            Response response = RestAssured.given()
                    .baseUri(CAMUNDA_LOCAL_URL)
                    .contentType("application/json")
                    .when()
                    .get("/history/process-instance/" + processInstanceId);

            // Check the status of the response
            response.then().statusCode(200);

            // Extract the completed status from the response
            boolean completed = response.jsonPath().getBoolean("ended");
            assertTrue("Process instance is completed.", completed);
        }
    }
