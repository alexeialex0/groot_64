import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrootBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final AIClient aiClient;
    private final Map<String, Integer> attentionTargets = new HashMap<>();
    private final Random random = new Random();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public GrootBot(String botToken, String botUsername, String apiKey) {
        super(new DefaultBotOptions());
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.aiClient = new AIClient(apiKey);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            String chatId = message.getChatId().toString();

            if (text.toLowerCase().contains("грут")) {
                if (text.toLowerCase().contains("фас")) {
                    handleFasCommand(message);
                } else if (text.trim().equalsIgnoreCase("грут")) {
                    sendMessage(chatId, generateGrootPhrase());
                } else {
                    handleGeneralQuery(message);
                }
            }
        }
    }

    private void handleGeneralQuery(Message message) {
        executor.submit(() -> {
            try {
                String query = extractQuery(message.getText());
                if (!query.isEmpty()) {
                    String response = aiClient.generateResponse(query);
                    sendMessage(message.getChatId().toString(), response);
                } else {
                    sendMessage(message.getChatId().toString(), generateGrootPhrase());
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(message.getChatId().toString(), "Я есть Грут... (ошибка нейросети)");
            }
        });
    }

    private String extractQuery(String text) {
        return text.replaceFirst("(?i)грут", "").trim();
    }

    private void handleFasCommand(Message message) {
        String chatId = message.getChatId().toString();
        String targetUsername = "";
        String displayName = "";

        if (message.isReply()) {
            User targetUser = message.getReplyToMessage().getFrom();
            targetUsername = targetUser.getUserName() != null ?
                    targetUser.getUserName() :
                    targetUser.getId().toString();
            displayName = getDisplayName(targetUser);
        } else {
            String text = message.getText();
            String[] parts = text.split("\\s+");
            for (String part : parts) {
                if (part.startsWith("@")) {
                    targetUsername = part.substring(1);
                    displayName = part;
                    break;
                }
            }
        }

        if (!targetUsername.isEmpty()) {
            int count = attentionTargets.getOrDefault(targetUsername, 0) + 1;
            attentionTargets.put(targetUsername, count);

            String response = "👁️‍🗨️ Внимание на " + displayName + "!\n" +
                    generateGrootPhrase() + "\n" +
                    "Всего вниманий: " + count;

            sendMessage(chatId, response);
        } else {
            sendMessage(chatId, "Я есть Грут? 🤔 Не вижу цели. Ответь на сообщение или укажи @username");
        }
    }

    private String getDisplayName(User user) {
        if (user.getUserName() != null && !user.getUserName().isEmpty()) {
            return "@" + user.getUserName();
        }
        return user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
    }

    private String generateGrootPhrase() {
        String[] phrases = {
                "Я есть Грут!",
                "Я есть Грут?",
                "Я есть Грут...",
                "Я есть Грут 🪵",
                "Я есть Грут 🌱",
                "Я есть Грут! (сердито)",
                "Я есть Грут? (любопытно)",
                "Я есть Грут! (радостно)",
                "Я есть Грут... (задумчиво)",
                "Я есть Грут! 🚀"
        };
        return phrases[random.nextInt(phrases.length)];
    }

    private void sendMessage(String chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }
}