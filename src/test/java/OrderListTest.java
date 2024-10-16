import io.qameta.allure.Step;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class OrderListTest {
    private int courierId; // ID курьера для теста
    private int nearestStation; // ID ближайшей станции
    private int limit; // Максимальное количество заказов для получения
    private int page; // Номер страницы для пагинации
    private OrderApi orderApi; // Экземпляр API для работы с заказами

    @Before
    public void setUp() {
        // Инициализация перед запуском тестов
        orderApi = new OrderApi(); // Создаем экземпляр API
        courierId = 387417; // Задаем ID курьера
        nearestStation = 3; // Задаем ID ближайшей станции
        limit = 30; // Устанавливаем лимит на количество заказов
        page = 0; // Устанавливаем номер страницы для запроса
    }

    @Test
    @Step("Получение списка заказов")
    public void getOrderListTest() {
        // Отправляем запрос для получения списка заказов
        orderApi.getOrderList(courierId, nearestStation, limit, page)
                .then()
                .statusCode(200) // Проверяем, что статус ответа 200 (ОК)
                .body("orders", notNullValue()) // Проверяем, что поле "orders" не null
                .body("orders.size()", greaterThan(0)); // Проверяем, что размер списка заказов больше 0
    }
}
