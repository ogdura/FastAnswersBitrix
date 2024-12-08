import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс WordsRead используется для чтения слов-исключений из файла words.txt
 */
public class WordsRead {
    /**
     * Метод для получения списка слов-исключений
     * @return Список слов-исключений
     * @throws FileNotFoundException если возникнет ошибка обработки файла
     */
    public List<String> getWords() throws FileNotFoundException {
        List <String> str = new ArrayList<>();
        String file_path = "Files/words.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(file_path))){
            String line;
            while ((line = br.readLine()) != null) {
                str.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}
