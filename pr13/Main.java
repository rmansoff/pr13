import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {
    private static final int PORT = 8088;
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final List<Item> items = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Ініціалізація HTTP сервера
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Роутинг запитів
        server.createContext("/home", Main::handleHome);
        server.createContext("/about", Main::handleAbout);
        server.createContext("/items", Main::handleItems);
        server.createContext("/add-item", Main::handleAddItem);
        server.createContext("/", Main::handleNotFound);

        // Додавання прикладів даних
        items.add(new Item("Laptop", 1000));
        items.add(new Item("Phone", 500));
        items.add(new Item("Headphones", 150));
        items.add(new Item("Monitor", 300));
        items.add(new Item("Keyboard", 50));

        // Запуск сервера
        server.setExecutor(null); // створює стандартний executor
        server.start();
        System.out.println("Server is running on port " + PORT);
    }

    // Метод для обробки /home
    private static void handleHome(HttpExchange exchange) throws IOException {
        Request request = new Request(exchange);
        Response response = new Response(exchange);
        logRequest(request);
        response.sendResponse(200, "text/html", "<h1>Welcome to the Home Page!</h1>");
    }

    // Метод для обробки /about
    private static void handleAbout(HttpExchange exchange) throws IOException {
        Request request = new Request(exchange);
        Response response = new Response(exchange);
        logRequest(request);

        String body = "<h1>About Me</h1>" +
                      "<p>Прізвище та ім'я: Ivan Ivanov/p>" +
                      "<p>Група: PS 4-1 </p>" +
                      "<p>Улюблений фільм: Inception</p>" +
                      "<p>Улюблена пісня: Imagine - John Lennon</p>";

        response.sendResponse(200, "text/html", body);
    }

    // Метод для обробки /items із фільтрацією
    private static void handleItems(HttpExchange exchange) throws IOException {
        Request request = new Request(exchange);
        Response response = new Response(exchange);
        logRequest(request);

        Map<String, String> queryParams = request.getQueryParams();
        double minPrice = queryParams.containsKey("minPrice") ? Double.parseDouble(queryParams.get("minPrice")) : 0;
        double maxPrice = queryParams.containsKey("topPrice") ? Double.parseDouble(queryParams.get("topPrice")) : Double.MAX_VALUE;
        int limit = queryParams.containsKey("limit") ? Integer.parseInt(queryParams.get("limit")) : items.size();

        List<Item> filteredItems = items.stream()
                .filter(item -> item.price >= minPrice && item.price <= maxPrice)
                .limit(limit)
                .collect(Collectors.toList());

        String responseBody = filteredItems.stream()
                .map(item -> item.name + ": $" + item.price)
                .collect(Collectors.joining("<br>"));

        response.sendResponse(200, "text/html", responseBody);
    }

    // Метод для додавання товару
    private static void handleAddItem(HttpExchange exchange) throws IOException {
        Request request = new Request(exchange);
        Response response = new Response(exchange);
        logRequest(request);

        Map<String, String> queryParams = request.getQueryParams();
        String name = queryParams.getOrDefault("name", "Unnamed");
        double price = queryParams.containsKey("price") ? Double.parseDouble(queryParams.get("price")) : 0;
        
        items.add(new Item(name, price));
        response.sendResponse(200, "text/plain", "Item added: " + name + " with price $" + price);
    }

    // Метод для обробки неіснуючих маршрутів
    private static void handleNotFound(HttpExchange exchange) throws IOException {
        Request request = new Request(exchange);
        Response response = new Response(exchange);
        logRequest(request);
        response.sendResponse(404, "text/plain", "404 Not Found");
    }

    // Логування запитів
    private static void logRequest(Request request) {
        logger.info(() -> String.format("Method: %s, Path: %s, User-Agent: %s", 
                    request.getMethod(), request.getUrl(), request.getHeaders().get("User-Agent")));
    }

    // Клас для товару
    static class Item {
        String name;
        double price;

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }
}
