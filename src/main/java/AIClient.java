import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class AIClient {
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final String apiKey;

    public AIClient(String apiKey) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    public String generateResponse(String query) {
        try {
            // Формирование тела запроса
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "mistralai/mistral-7b-instruct:free");

            // Обязательно для бесплатных ключей
            JsonObject transforms = new JsonObject();
            transforms.addProperty("provider", "free");
            requestBody.add("transforms", transforms);

            JsonArray messages = new JsonArray();

            // Системное сообщение
            JsonObject systemMsg = new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", "Ты — Грут из Стражи Галактики. Отвечай кратко, используя 1-2 предложения. Всегда начинай с 'Я есть Грут'.");
            messages.add(systemMsg);

            // Пользовательский запрос
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", query);
            messages.add(userMsg);

            requestBody.add("messages", messages);
            requestBody.addProperty("max_tokens", 256);
            requestBody.addProperty("temperature", 0.7);

            // Формирование запроса
            RequestSpecification request = RestAssured.given()
                    .baseUri(OPENROUTER_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://groot-bot.com")
                    .header("X-Title", "Groot Bot")
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString());

            // Отправка запроса
            Response response = request.post();

            // Обработка ответа
            if (response.getStatusCode() != 200) {
                return "Я есть Грут... (ошибка API: " + response.getStatusCode() + ")";
            }

            return response.jsonPath().getString("choices[0].message.content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Я есть Грут... (ошибка: " + e.getMessage() + ")";
        }
    }
}