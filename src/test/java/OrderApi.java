import io.restassured.RestAssured;
import io.restassured.response.Response;

public class OrderApi {
    public OrderApi() {
        RestAssured.baseURI = ApiConfig.BASE_URL;

    }

    public Response createOrder(OrderDto orderDto) {
        return RestAssured.given()
                .contentType("application/json")
                .body(orderDto)
                .post("/orders");
    }

    public Response cancelOrder(int trackId) {
        return RestAssured.given()
                .contentType("application/json")
                .queryParam("track", trackId)
                .put("/orders/cancel");
    }

    public Response getOrderList(int courierId, int nearestStation, int limit, int page) {
        return RestAssured.given()
                .queryParams("courierId", courierId,
                        "nearestStation", nearestStation,
                        "limit", limit,
                        "page", page)
                .get("/orders");
    }
}
