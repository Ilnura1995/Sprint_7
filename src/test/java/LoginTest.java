import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {
    private CourierApi courierApi; // Экземпляр API для работы с курьерами
    private String courierId; // ID курьера, который будет использоваться для удаления
    private String login; // Логин для тестируемого курьера
    private String password; // Пароль для тестируемого курьера
    private String firstName; // Имя для тестируемого курьера
    private String invalidLogin; // Неверный логин для тестов
    private String invalidPassword; // Неверный пароль для тестов

    @Before
    public void setUp() {
        // Инициализация перед запуском тестов
        courierApi = new CourierApi(); // Создаем экземпляр API
        login = "shelby2281337"; // Устанавливаем логин курьера
        password = "12345"; // Устанавливаем пароль курьера
        firstName = "Thomas"; // Устанавливаем имя курьера
        invalidLogin = "SAWDIJAWODKO213123_23"; // Устанавливаем неверный логин
        invalidPassword = "awdi12j3i1j23adawd"; // Устанавливаем неверный пароль
    }

    @Test
    @Step("Авторизация курьера с валидными данными")
    public void loginCourier() {
        // Создаем курьера с валидными данными
        CourierDto courier = new CourierDto(login, password, firstName);
        Response response = courierApi.createCourier(courier); // Отправляем запрос на создание курьера

        response.then()
                .statusCode(201) // Проверка успешного создания (код 201)
                .body("ok", equalTo(true)); // Проверка, что ответ содержит "ok": true

        // Логинимся с валидными данными
        LoginDto loginDto = new LoginDto(login, password);
        Response loginResponse = courierApi.loginCourier(loginDto); // Отправляем запрос на авторизацию
        courierId = loginResponse.then()
                .statusCode(200) // Проверка успешной авторизации (код 200)
                .body("id", notNullValue()) // Проверка, что ID курьера не null
                .extract().jsonPath().getString("id"); // Извлекаем ID курьера
    }

    @Test
    @Step("Авторизация курьера с невалидными данными")
    public void loginWithInvalidCredentials() {
        // Пытаемся логиниться с неверными данными
        LoginDto loginDto = new LoginDto(invalidLogin, invalidPassword);
        Response response = courierApi.loginCourier(loginDto); // Отправляем запрос на авторизацию

        response.then()
                .statusCode(404) // Проверка, что сервер вернул ошибку (код 404)
                .body("message", equalTo("Учетная запись не найдена")); // Проверка текста ошибки
    }

    @Test
    @Step("Авторизация курьера с недостающими полями")
    public void loginWithoutRequiredField() {
        // Пытаемся логиниться без пароля
        LoginDto jsonWithoutPassword = new LoginDto();
        jsonWithoutPassword.setLogin(login); // Устанавливаем логин
        Response response = courierApi.loginCourier(jsonWithoutPassword); // Отправляем запрос

        response.then().statusCode(504); // Проверка ошибки (ожидается код 504)

        // Пытаемся логиниться без логина
        LoginDto jsonWithoutLogin = new LoginDto();
        jsonWithoutLogin.setPassword(password); // Устанавливаем пароль
        response = courierApi.loginCourier(jsonWithoutLogin); // Отправляем запрос

        response.then().statusCode(400); // Проверка ошибки (ожидается код 400)

        // Пытаемся логиниться с пустыми полями
        LoginDto jsonEmptyFields = new LoginDto("", ""); // Пустые логин и пароль
        response = courierApi.loginCourier(jsonEmptyFields); // Отправляем запрос

        response.then()
                .statusCode(400) // Проверка ошибки (ожидается код 400)
                .body("message", equalTo("Недостаточно данных для входа")); // Проверка текста ошибки
    }

    @After
    @Step("Удаление учётной записи курьера")
    public void tearDown() {
        // После тестов, если есть ID курьера, удаляем его
        if (courierId != null) {
            courierApi.deleteCourier(courierId).then()
                    .statusCode(200); // Проверка успешного удаления (код 200)
        }
    }
}
