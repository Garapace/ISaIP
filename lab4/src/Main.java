import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String ABC = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final Map<Character, Integer> charToIndex = new HashMap<>();
    private static final Map<Integer, Character> indexToChar = new HashMap<>();

    static {
        for (int i = 0; i < ABC.length(); i++) {
            charToIndex.put(ABC.charAt(i), i);
            indexToChar.put(i, ABC.charAt(i));
        }
    }

    private static Character[][] squareVigener = new Character[ABC.length()][ABC.length()];

    static {
        for (int i = 0; i < ABC.length(); i++) {
            for (int j = 0; j < ABC.length(); j++) {
                squareVigener[i][j] = ABC.charAt((i + j) % ABC.length());
            }
        }
    }

    public static void main(String[] args) {
        String inputFile = "assets/input.txt";
        String outputFileCaesar = "assets/outputCaesar.txt";
        String outputFileVigener = "assets/outputVigener.txt";
        String returnCaesar = "assets/returnCaesar.txt";
        String returnVigener = "assets/returnVigener.txt";

        // Шифрование
        String input = read(inputFile).toUpperCase();

        // Для Цезаря
        String cipherCaesar = encryptCaesar(input, 6);
        write(outputFileCaesar, cipherCaesar);

        // Для виженера
        String cipherVigener = encryptVigener(input, "МЕЧТА");
        write(outputFileVigener, cipherVigener);

        // Дешифрование
        write(returnCaesar, decryptCaesar(cipherCaesar, 6));
        write(returnVigener, decryptVigener(cipherVigener, "МЕЧТА"));

        // Записываем квадрат Виженера в файл
        StringBuilder vigenerTable = new StringBuilder();

        for (int i = 0; i < squareVigener.length; i++) {
            for (int j = 0; j < squareVigener[0].length; j++) {
                vigenerTable.append(squareVigener[i][j]).append('\t');
            }
            vigenerTable.append('\n');
        }

        // Путь к файлу, куда сохраняется квадрат
        String vigenerSquareFile = "assets/vigenerSquare.txt";
        write(vigenerSquareFile, vigenerTable.toString());
        System.out.println("Квадрат Виженера записан в файл: " + vigenerSquareFile);
    }
    public static String read(String inputFile) {
        String text = "";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text += line;
            }
        } catch (FileNotFoundException e ) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return text;
    }

    public static void write(String outputFile, String text) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))){
            bufferedWriter.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String encryptVigener (String text, String key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            char keyChar = key.charAt(i % key.length());
            if (charToIndex.containsKey(currentChar)) {
                int originalIndex = charToIndex.get(currentChar);
                int keyIndex = charToIndex.get(keyChar);
                result.append(indexToChar.get((originalIndex + keyIndex) % ABC.length()));
            } else {
                // если алфавит не содержит символа
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    public static String decryptVigener (String text, String key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            char keyChar = key.charAt(i % key.length());
            if (charToIndex.containsKey(currentChar)) {
                int originalIndex = charToIndex.get(currentChar);
                int keyIndex = charToIndex.get(keyChar);
                result.append(indexToChar
                        .get(((originalIndex - keyIndex + ABC.length()) % ABC.length())));
            } else {
                // если алфавит не содержит символа
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    public static String encryptCaesar(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if(charToIndex.containsKey(currentChar)) {
                int originalIndex = charToIndex.get(currentChar);
                int newIndex = (originalIndex + shift) % ABC.length();
                result.append(indexToChar.get(newIndex));
            } else {
                // если алфавит не содержит символа
                result.append(currentChar);
            }
        }

        return result.toString();
    }
    public static String decryptCaesar(String text, int shift) {
        return encryptCaesar(text, ABC.length() - shift);
    }

}