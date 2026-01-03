package xp.theatrical.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExerciseApplicationTests {

    @LocalServerPort
    int port;

    @Test
    void contextLoads() {
    }

    @Test
    void missing_customer() {
        // @formatter:off
        given()
            .port(port)
        .when()
            .get("/statement/")
        .then()
            .statusCode(404);
        // @formatter:on
    }

    @Test
    void missing_customer_message() {
        // @formatter:off
        String response = given()
                    .port(port)
                .when()
                    .get("/statement/")
                    .asString();

        assertThat(response).contains("\"status\":404,\"error\":\"Not Found\",\"path\":\"/statement/\"");
        // @formatter:on
    }

    @Test
    void unknown_customer() {
        // @formatter:off
        given()
            .port(port)
        .when()
            .get("/statement/does_not_exist")
        .then()
            .statusCode(200);
        // @formatter:on
    }

    @Test
    void unknown_customer_message() {
        // @formatter:off
        String response = given()
                    .port(port)
                .when()
                    .get("/statement/does_not_exist")
                    .asString();
        // @formatter:on
        assertThat(response).contains("Statement for does_not_exist\nAmount owed is $0.00\nYou earned 0 credits\n");
    }

    // TODO: whoever can, write tests for actual functionality

}
