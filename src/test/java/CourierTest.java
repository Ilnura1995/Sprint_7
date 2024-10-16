import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierTest {
    private CourierApi courierApi; // Экземпляр API для работы с курьерами
    private String courierId; // ID курьера, который будет использоваться для удаления
    private String login; // Логин для тестируемого курьера
    private String password; // Пароль для тестируемого курьера
    private String firstName; // Имя для тестируемого курьера
    private String registeredLogin; // Логин, который уже зарегистрирован

    @Before
    public void setUp() {
        // Инициализация перед запуском тестов
        courierApi = new CourierApi(); // Создаем экземпляр API
        login = "loginCourierAk47"; // Устанавливаем логин курьера
        password = "passCourierAk47"; // Устанавливаем пароль курьера
        firstName = "Al Capone"; // Устанавливаем имя курьера
        registeredLogin = "ninja"; // Устанавливаем логин, который уже зарегистрирован
    }

    @Test
    @Step("Создание курьера")
    public void createCourier() {
        // Создаем объект курьера
        CourierDto courier = new CourierDto(login, password, firstName);
        Response response = courierApi.createCourier(courier); // Отправляем запрос на создание курьера

        // Проверяем успешность ответа
        response.then()
                .statusCode(201) // Код состояния 201 - создано
                .body("ok", equalTo(true)); // Ответ должен содержать поле "ok" со значением true

        // Логинимся для получения ID созданного курьера
        Response loginResponse = courierApi.loginCourier(new LoginDto(login, password));
        courierId = loginResponse.then()
                .statusCode(200) // Проверяем успешный ответ на логин
                .body("id", notNullValue()) // ID курьера должен быть не null
                .extract().jsonPath().getString("id"); // Извлекаем ID курьера
    }

    @Test
    @Step("Попытка создания дубликата курьера")
    public void duplicateCourierCreation() {
        // Создаем первого курьера
        CourierDto courier = new CourierDto(login, password, firstName);
        courierApi.createCourier(courier).then()
                .statusCode(201) // Проверяем успешное создание
                .body("ok", equalTo(true)); // Поле "ok" должно быть true

        // Пытаемся создать дубликат курьера
        Response duplicateResponse = courierApi.createCourier(courier);
        duplicateResponse.then()
                .statusCode(409) // Код состояния 409 - конфликт (дубликат)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // Проверяем сообщение об ошибке

        // Логинимся для получения ID курьера
        Response loginResponse = courierApi.loginCourier(new LoginDto(login, password));
        courierId = loginResponse.then()
                .statusCode(200) // Проверяем успешный ответ
                .body("id", notNullValue()) // ID курьера должен быть не null
                .extract().jsonPath().getString("id"); // Извлекаем ID курьера
    }

    @Test
    @Step("Создание курьера без обязательных полей")
    public void createCourierWithoutRequiredFields() {
        // Пытаемся создать курьера без пароля
        CourierDto jsonWithoutPassword = new CourierDto();
        jsonWithoutPassword.setLogin(login); // Устанавливаем логин
        jsonWithoutPassword.setFirstName(firstName); // Устанавливаем имя
        courierApi.createCourier(jsonWithoutPassword).then()
                .statusCode(400) // Код состояния 400 - ошибка запроса
                .body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверяем сообщение об ошибке

        // Пытаемся создать курьера без логина
        CourierDto jsonWithoutLogin = new CourierDto();
        jsonWithoutLogin.setPassword(password); // Устанавливаем пароль
        jsonWithoutLogin.setFirstName(firstName); // Устанавливаем имя
        courierApi.createCourier(jsonWithoutLogin).then()
                .statusCode(400) // Код состояния 400 - ошибка запроса
                .body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверяем сообщение об ошибке
    }

    @Test
    @Step("Создание курьера с уже зарегистрированным логином")
    public void createCourierWithRegisteredLogin() {
        // Пытаемся создать курьера с логином, который уже существует
        CourierDto json = new CourierDto();
        json.setLogin(registeredLogin); // Устанавливаем зарегистрированный логин
        json.setPassword(password); // Устанавливаем пароль
        json.setFirstName(firstName); // Устанавливаем имя

        Response response = courierApi.createCourier(json); // Отправляем запрос на создание
        response.then()
                .statusCode(409) // Код состояния 409 - конфликт (дубликат)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // Проверяем сообщение об ошибке
    }

    @After
    public void tearDown() {
        // После тестов, если есть ID курьера, удаляем его
        if (courierId != null) {
            courierApi.deleteCourier(courierId).then()
                    .statusCode(200); // Проверяем успешное удаление
        }
    }
}
