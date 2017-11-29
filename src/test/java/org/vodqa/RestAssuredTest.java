package org.vodqa;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class RestAssuredTest {

  @Before
  public void setUp() {
    RestAssured.baseURI = "http://localhost:9000";
  }

  @Test
  public void testStatusCode() {

    given().

    when().
        get("/users").
    then().
        assertThat().
        statusCode(HttpStatus.SC_OK);
  }


  @Test
  public void testGetRequest() {
    given().
        contentType("application/json").
    when().
        get("/users").
    then().
        assertThat().
        body("userId", hasItems(2, 3, 4)).log();
  }

  @Test
  public void testSimpleGetRequest() {
    given().

    when().
        get("/users/1").
    then().
        assertThat().
        body("userId", equalTo(1)).
        body("userName", equalTo("Robert")).
        body("employer", equalTo("facebook")).
        body("location.state", equalTo("California")).
        body("location.city", equalTo("San Jose"));
  }


  @Test
  public void testPostRequest() {

    given().
        contentType("application/json").
        body("[{\"userName\":\"Andy\",\"employer\":\"Google\",\"location\":{\"state\":\"California\",\"city\":\"Mountain View\"}}]").
    when().
        post("/users").
    then().
        assertThat().
        body("userName", hasItems("Andy"));
  }

  @Test
  public void testPutRequest() {
    int userId = 1;

    given().
        contentType("application/json").
    when().
        body("{\"userName\":\"Taylor\"}").
            put("/users/" + userId).
    then().
        statusCode(HttpStatus.SC_OK).
        body("userName", equalTo("Taylor"));

  }

  @Test
  public void testDeleteRequest() {
    int userId = 6;
    given().

        when().
        delete("/users/" + userId).
    then().
        statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void testResponseData() {
    Response response =
        given().
            contentType(ContentType.JSON).
        when().
            get("/users/5").
        then().
            extract().response();

    String userName = response.path("userName");
    String userCity = response.path("location.city");

    Assert.assertTrue(userName.equals("Steve"));
    Assert.assertTrue(userCity.equals("San Francisco"));
  }

  @Test
  public void testUsingJsonPath() {
    String json = get("/users/5").asString();

    JsonPath jsonPath = new JsonPath(json).setRoot("location");
    String state = jsonPath.getString("state");
    String city = jsonPath.getString("city");
    Assert.assertTrue(state.equals("California"));
    Assert.assertTrue(city.equals("San Francisco"));

  }
}