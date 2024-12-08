import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Класс WebHook используется для отправки HTTP POST запросов на указанный URL-адрес с JSON-данными.
 */
public class WebHook {
    /**
     * URL вебхука.
     */
    String webhookUrl;
    /**
     * Устанавливает URL вебхука.
     *
     * @param webhookUrl URL вебхука
     */
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    /**
     * Отправляет JSON-данные на URL вебхука и возвращает ответ. Может быть использован в GetJson
     *
     * @param jsonInputString JSON-строка с настройкой, которую нужно отправить, вебхука.
     * @return ответ от вебхука в формате {@link StringBuilder}
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public StringBuilder getData(String jsonInputString) throws IOException {
        if (this.webhookUrl!=null && !this.webhookUrl.isEmpty()){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL url = new URL(this.webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            if (jsonInputString != null){
                byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response;
        }
    }else{
            throw new IllegalArgumentException("URL не заполнен");
        }


    }
    /**
     * Получает JSON ответ в виде строки.
     *
     * @param jsonInputString JSON-строка с настройкой, которую нужно отправить, вебхука.
     * @return строка ответа от вебхука
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public String getJson(String jsonInputString) throws IOException {
        StringBuilder responseChat = this.getData(jsonInputString);
        System.out.println(responseChat);
        if(responseChat.indexOf("[")==-1){
            return responseChat.toString();
        }
        String chatStr = responseChat.toString().substring(responseChat.toString().indexOf("["));
        return chatStr;
    }


}
