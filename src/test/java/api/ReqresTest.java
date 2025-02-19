package api;

import io.restassured.RestAssured;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;

public class ReqresTest {
    private static final String URL = "https://reqres.in/";

    @Test
    public void createUser() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK201());
        СreateUser user = new СreateUser("Jane19", 36);
        RestAssured.given()
                .body(user)
                .when()
                .post("api/users")
                .then()
                .log().all()
                .statusCode(201);

        Assert.assertThat(user.getName(), Matchers.is("Jane19"));
        Assert.assertThat(user.getAge(), Matchers.is(36));
    }

    @Test
    public void patchRequestAndChangeName() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        String name;
        name = RestAssured.given()
                .log().all()
                .when()
                .body(new СreateUser("Jane19", 36))
                .patch("api/users/2")
                .then()
                .statusCode(200)
                .assertThat()
                .body("name", Matchers.is("Jane19"))
                .extract()
                .response()
                .jsonPath()
                .getString("name");

        RestAssured.given()
                .log().all()
                .when()
                .header("newName", name)
                .body(new СreateUser("Olga", 36))
                .patch("api/users/2" + name)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("name", Matchers.is("Olga"));

    }

    @Test
    public void deleteUser() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.uniqueResponse(204));
                 given()
                .delete("api/users/2")
                .then()
                .log().all();
    }


    @Test
    public void successRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Integer id = 4;
        String token = "здесь должен быть токен";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessReg() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK400());
        Register user = new Register("sydney@fife", "");
        UnSuccesReg unSuccessReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccesReg.class);
        Assert.assertEquals("Missing password", unSuccessReg.getError());
    }


    //убедиться что имена файлов аватаров совпадают:
    @Test
    public void checkAvatarAndIDTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
//        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
//        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }

    }
}
