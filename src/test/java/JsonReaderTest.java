import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/* Приложеный файл нужно проверять на:
- Что он удовлетворяет JSON требованиям
- Что структура соответствует заданной
- Что отсутствуют лишние пробелы, переносы, символы и любые другие артефакты
- Что в вопросах не повторяются тексты ответов
- Что в вопросах с картинкой, картинка есть в описании*/



//Нужно проверять, что длинну ответов, если число ответов равно:
// 2: то в ответе будет не более 120 символов,
// 3: 90,
// 4: 60
public class JsonReaderTest {

    @Test
    public void LengthOfAnswers() {

        try (FileReader reader = new FileReader("src/test/java/questions.json")) {
            StringBuilder jsonString = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                jsonString.append((char) character);
            }
            JSONArray jsonArray = new JSONArray(jsonString.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String uniqueId = jsonObject.getString("uniqueId");
                JSONArray options = jsonObject.getJSONArray("options"); //  Этот метод извлекает из jsonObject массив по ключу "options". Ключ "options" указывает на массив, содержащий доступные варианты ответов на некоторый вопрос, хранящийся в JSON-файле.
                int numAnswers = options.length();
                int maxLength = 0;

                if (numAnswers == 2) {
                    maxLength = 120;
                } else if (numAnswers == 3) {
                    maxLength = 90;
                } else if (numAnswers == 4) {
                    maxLength = 60;
                }

                for (int j = 0; j < options.length(); j++) {
                    JSONObject answer = options.getJSONObject(j);
                    String text = answer.getString("text");

                    if (text.length() > maxLength) {
                        // System.out.println(" Peremennaya ravna " + text.length());
                        Assert.assertTrue("В блоке с UniqueID: " + uniqueId + " Длина ответа \"" + text + "\" превышает максимально допустимую длину.", maxLength > text.length());
                        // System.out.println("Длина ответа \"" + text + "\" превышает максимально допустимую длину.");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }


    //- Что все названия уникальны
    @Test
    public void UniqueNamesChecker() {

        String filePath = "src/test/java/questions.json";
        Map<String, List<Integer>> uniqueIdOccurrences = new HashMap<>();
        StringBuilder jsonContentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                jsonContentBuilder.append(line).append("\n");

                if (line.contains("\"uniqueId\"")) {
                    String trimmedLine = line.trim();
                    int startIndex = trimmedLine.indexOf(":") + 1;
                    String uniqueId = trimmedLine.substring(startIndex).replaceAll("[\", ]", "");

                    if (uniqueIdOccurrences.containsKey(uniqueId)) {
                        uniqueIdOccurrences.get(uniqueId).add(lineNumber);
                    } else {
                        List<Integer> occurrences = new ArrayList<>();
                        occurrences.add(lineNumber);
                        uniqueIdOccurrences.put(uniqueId, occurrences);
                    }
                }
            }

            JSONArray jsonArray = new JSONArray(jsonContentBuilder.toString());

            boolean duplicatesFound = false;
            for (Map.Entry<String, List<Integer>> entry : uniqueIdOccurrences.entrySet()) {
                String uniqueId = entry.getKey();
                List<Integer> occurrences = entry.getValue();
                if (occurrences.size() > 1) {
                    duplicatesFound = true;
                    System.out.print("Duplicate uniqueId '" + uniqueId + "' found on lines: ");
                    for (Integer lineNum : occurrences) {
                        System.out.print(lineNum + ", ");
                    }
                    System.out.println();
                }
            }

            if (!duplicatesFound) {
                System.out.println("No duplicate uniqueIds found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// - Что в вопросах только один правильный ответ







