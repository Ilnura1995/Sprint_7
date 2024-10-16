import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class) // Указываем, что тест будет параметризован
public class OrderTest {
    private OrderApi orderApi; // Экземпляр API для работы с заказами
    private int trackId; // ID трека заказа для последующего использования

    @Parameterized.Parameter(0) // Первый параметр (описание цвета)
    public String colorDescription;
    @Parameterized.Parameter(1) // Второй параметр (список цветов)
    public ArrayList<String> colorParam;

    @Before
    public void setUp() {
        // Инициализация перед запуском тестов
        orderApi = new OrderApi(); // Создаем экземпляр API
        // Инициализация других параметров заказа, если необходимо
    }

    @Parameterized.Parameters(name = "{index}: {0}") // Указываем формат для вывода параметров
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Один цвет - Black", new ArrayList<>(Arrays.asList("Black"))}, // Первый тест с одним цветом
                {"Один цвет - Grey", new ArrayList<>(Arrays.asList("Grey"))}, // Второй тест с одним цветом
                {"Оба цвета", new ArrayList<>(Arrays.asList("Black", "Grey"))}, // Третий тест с двумя цветами
                {"Без цвета", new ArrayList<>()} // Четвертый тест без цвета
        });
    }

    @Test
    @Step("Создание заказа с цветами: {0}") // Шаг с описанием цветов
    public void orderCreationTest() {
        // Создаем объект заказа, используя заданные параметры
        OrderDto orderDto = new OrderDto(/* параметры заказа, включая colorParam */);

        // Отправляем запрос на создание заказа
        trackId = orderApi.createOrder(orderDto)
                .then()
                .statusCode(201) // Проверка, что статус ответа 201 (создано)
                .body("track", notNullValue()) // Проверка, что трек заказа не null
                .extract() // Извлекаем трек заказа
                .path("track");
    }

    @After
    public void tearDown() {
        // После теста, если trackId не равен 0, отменяем заказ
        if (trackId != 0) {
            orderApi.cancelOrder(trackId)
                    .then()
                    .statusCode(200) // Проверка, что статус ответа 200 (успешно)
                    .body("ok", equalTo(true)); // Проверка, что ответ содержит поле "ok" со значением true
        }
    }
}
