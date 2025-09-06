import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        // Получаем переменные окружения из Docker
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_USERNAME");
        String openRouterApiKey = System.getenv("OPENROUTER_API_KEY");

        // Если переменные не заданы, используем значения по умолчанию
        if (botToken == null || botToken.isEmpty()) {
            botToken = "8347269491:AAGzrx1y8nXxDpV2uiq5xLmgi0GgLpZ7z-k";
        }
        if (botUsername == null || botUsername.isEmpty()) {
            botUsername = "Groot64_bot";
        }
        if (openRouterApiKey == null || openRouterApiKey.isEmpty()) {
            openRouterApiKey = "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";
        }

        // Проверка формата ключа
        if (!openRouterApiKey.startsWith("sk-or-v1-")) {
            System.err.println("❌ Неверный формат ключа OpenRouter!");
            System.exit(1);
        }

        System.out.println("=== CONFIGURATION ===");
        System.out.println("BOT_TOKEN: ***" + botToken.substring(botToken.length() - 4));
        System.out.println("BOT_USERNAME: " + botUsername);
        System.out.println("OPENROUTER_API_KEY: ***" + openRouterApiKey.substring(openRouterApiKey.length() - 4));
        System.out.println("=====================");

        try {
            // Тестирование нейросети
            System.out.println("🟢 Тестирование нейросети...");
            AIClient testClient = new AIClient(openRouterApiKey);
            String testResponse = testClient.generateResponse("Привет! Как дела?");
            System.out.println("🤖 Ответ нейросети: " + testResponse);

            if (testResponse.contains("ошибка")) {
                System.err.println("🔴 Тест нейросети не пройден!");
                System.exit(1);
            }

            // Запуск бота
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new GrootBot(botToken, botUsername, openRouterApiKey));
                System.out.println("🤖 Бот Грут успешно запущен!");

                // Бесконечный цикл для поддержания работы
                while (true) {
                    Thread.sleep(1000);
                }
            } catch (TelegramApiException e) {
                System.err.println("🔴 Ошибка Telegram API:");
                e.printStackTrace();
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("🔴 Критическая ошибка запуска:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}