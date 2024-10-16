import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CourierApi {
    public CourierApi() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
    }

    public Response createCourier(CourierDto courier) {
        return RestAssured.given()
                .contentType("application/json")
                .body(courier)
                .post("/courier");
    }

    public Response deleteCourier(String courierId) {
        return RestAssured.given()
                .contentType("application/json")
                .delete("/courier/" + courierId);
    }

    public Response loginCourier(LoginDto loginDto) {
        return RestAssured.given()
                .contentType("application/json")
                .body(loginDto)
                .post("/courier/login");
    }
}
