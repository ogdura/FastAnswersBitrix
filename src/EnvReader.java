import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс EnvReader используется для чтения env файла
 */
public class EnvReader {
    /**
     * Словарь для данных в env файле
     * **/
    private final Map<String, String> envVariables;

    /** Конструктор, который загружает .env файл
     *
     * @param filePath Путь до env файла
     * @throws IOException если произойдет ошибка при обработке файла.
     */
    public EnvReader(String filePath) throws IOException {
        envVariables = new HashMap<>();
        loadEnvFile(filePath);
    }

    /** Метод загрузки .env файла
     *
     * @param filePath путь до env-файла
     * @throws IOException если произойдет ошибка при обработке файла. **/
    private void loadEnvFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(".env file not found at: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Убираем пробелы и табуляции
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Пропускаем пустые строки и комментарии
                }
                String[] parts = line.split("=", 2); // Разделяем строку на ключ и значение
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envVariables.put(key, value); // Сохраняем в Map
                }
            }
        }
    }

    /**
     * Возвращает словарь с данными из env файла
     * @param key Ключ по которому находится значение
     * @return словарь с данными из env файла
     */
    public String getValue(String key) {
        return envVariables.getOrDefault(key, null);
    }
}
