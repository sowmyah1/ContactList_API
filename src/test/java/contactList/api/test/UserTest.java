package contactList.api.test;

import TestListener.TestListener;
import com.github.javafaker.Faker;

import contactList.api.endpoints.UserEndPoints;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import payload.User;

@Listeners(TestListener.class)
public class UserTest {
    Faker faker;
    User userPayload;
    static String token;
    String id;
    static String loginUserId;
    String loginToken;

    public String getToken() {
        return token;
    }

    @BeforeClass
    public void setUpData() {

        faker = new Faker();
        userPayload = new User();

        userPayload.setFirstName(faker.name().firstName());
        userPayload.setLastName(faker.name().lastName());
        userPayload.setEmail(faker.internet().emailAddress());
        userPayload.setPassword(faker.internet().password());
    }

    @Test(priority = 1, groups = "smoke")
    public void testPostUser() {
        Response response = UserEndPoints.createUser(userPayload);
        response.then().log().all();
        token = response.jsonPath().getString("token");
        id = response.jsonPath().getString("user._id");

        Assert.assertEquals(response.getStatusCode(), 201, "User not created");
    }

    @Test(priority = 2, groups = "sanity")
    public void testGetUser() {
        Response response = UserEndPoints.readUser(token);
        response.then().log().all();
        String id1 = response.jsonPath().getString("_id");
        Assert.assertEquals(response.getStatusCode(), 200, "Cannot access user details");
        Assert.assertEquals(id1, id, "user did not match");
    }

    @Test(priority = 3, groups = "regression")
    public void testUpdateUser() {
        userPayload.setFirstName(faker.name().firstName());
        userPayload.setLastName(faker.name().lastName());
        Response response = UserEndPoints.updateUser(userPayload, token);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "User details not updated");

        Response responseAfterUpdate = UserEndPoints.readUser(token);

        Assert.assertEquals(responseAfterUpdate.statusCode(), 200, "User details not updated ");
    }

    @Test(priority = 4, groups = "sanity")
    public void testLoginUser() {
        Response response = UserEndPoints.loginUser(userPayload, token);
        loginToken = response.jsonPath().getString("token");
        loginUserId = response.jsonPath().getString("user._id");
        System.out.println(loginToken);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Login not successful");

    }

    @Test(priority = 5, groups = "sanity")
    public void testLogoutUser() {
        Response response = UserEndPoints.logoutUser(token);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "User logout unsuccessful");
    }

    @Test(priority = 6, groups = "smoke")
    public void testDeleteUser() {
        this.testLoginUser();
        Response response = UserEndPoints.deleteUser(loginToken);
        System.out.println("this is for delete");
        System.out.println(loginToken);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Delete user unsuccessful");
    }


}
