import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;


public class Main {
    private static final String RUSSIAN = "йцукенгшщзхъфывапролджэячсмитьбю.ё";
    private static final String ENGLISH = "qwertyuiop[]asdfghjkl;'zxcvbnm,./`";
    private static long startTime;
    private static long endTime;
    private static double duration;

    public static void main(String[] args) throws InterruptedException {
        String currentPassword = "qwerty";

        System.out.println(dictionaryAttack(currentPassword));
        System.out.println(bruteForceAttack(currentPassword, ENGLISH + "1234567890"));
        System.out.println(duration);
    }

    private static String convertToEngToRus(String russianWord) {
        StringBuilder result = new StringBuilder();
        for (char c : russianWord.toCharArray()) {
            int index = RUSSIAN.indexOf(Character.toLowerCase(c));
            if (index != -1) {
                char translatedChar = ENGLISH.charAt(index);
                // Сохраняем регистр
                if (Character.isUpperCase(c)) {
                    translatedChar = Character.toUpperCase(translatedChar);
                }
                result.append(translatedChar);
            } else {
                result.append(c); // Оставляем символ без изменений, если он не найден
            }
        }
        return result.toString();
    }

    // Метод для перебора по словарю
    private static String dictionaryAttack(String targetPassword) {
        startTime = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader("assets/passwordSet.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (convertToEngToRus(line).equals(targetPassword)) {
                    endTime = System.currentTimeMillis();
                    return line;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла словаря: " + e.getMessage());
        }
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime) / 1000.0;
        return "Пароль не найден.";
    }

    private static String bruteForceAttack(String targetPassword, String alphabet) throws InterruptedException {
        startTime = System.currentTimeMillis();
        int targetLen = targetPassword.length();
        // длина 0 отдельно
        if (targetLen == 0) {
            endTime = System.currentTimeMillis();
            duration = (endTime - startTime) / 1000.0;
            return "";
        }

        for (int len = 1; len <= targetLen; len++) {
            int[] idx = new int[len]; // все нули
            while (true) {
                // Собираем текущее слово
                StringBuilder sb = new StringBuilder(len);
                for (int k = 0; k < len; k++) sb.append(alphabet.charAt(idx[k]));

                String candidate = sb.toString();
                if (candidate.equals(targetPassword)) {
                    endTime = System.currentTimeMillis();
                    duration = (endTime - startTime) / 1000.0;
                    return candidate;
                }

                // Инкрементируем «одометр»
                int pos = len - 1;
                while (pos >= 0) {
                    if (++idx[pos] < alphabet.length()) break;
                    idx[pos] = 0;
                    pos--;
                }
                if (pos < 0) break; // все комбинации длины len пройдены
            }
        }

        endTime = System.currentTimeMillis();
        duration = (endTime - startTime) / 1000.0;
        return null;
    }
    
}