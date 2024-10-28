import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final HttpExchange exchange;

    public Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public String getMethod() {
        return exchange.getRequestMethod();
    }

    public String getUrl() {
        return exchange.getRequestURI().getPath();
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        exchange.getRequestHeaders().forEach((key, values) -> headers.put(key, values.get(0)));
        return headers;
    }

    public Map<String, String> getQueryParams() {
        Map<String, String> queryParams = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] param = pair.split("=");
                if (param.length == 2) {
                    queryParams.put(param[0], param[1]);
                }
            }
        }
        return queryParams;
    }
}
