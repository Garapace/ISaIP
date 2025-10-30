import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lab6Vigener {
    private static final String ABC = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ";

    public static void main(String[] args) {
        String text = Lab4.read("input.txt").toUpperCase();
        String cipher = Lab4.encryptVigener(text, "АРКА");
        System.out.println(keyLenght(cipher));
        System.out.println(breakCipherVigener(cipher));
    }

    static String breakCipherVigener (String text) {
        int lenght = keyLenght(text);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lenght; i++) {
            int groupIndex = i;
            String group = IntStream.range(0, text.length())
                    .filter(k -> k % lenght == groupIndex)
                    .mapToObj(text::charAt)
                    .map(Object::toString)
                    .collect(Collectors.joining());

            int offset = Lab6Caesar.breakCipher(group);
            stringBuilder.append(ABC.charAt(offset));

            //System.out.println(ABC.charAt(offset));
        }
        return stringBuilder.toString();
    }

    static int keyLenght (String text) {
        double[] indexes = new double[10];
        for (int i = 1; i <= 10; i++) {
            int possibleLength = i;
            double sumIC = 0;

            // Разделяем текст на possibleLength групп
            for (int j = 0; j < possibleLength; j++) {
                int groupIndex = j;
                String group = IntStream.range(0, text.length())
                        .filter(k -> k % possibleLength == groupIndex)
                        .mapToObj(text::charAt)
                        .map(Object::toString)
                        .collect(Collectors.joining());

                // Вычисляем индекс совпадений для группы
                sumIC += indexOfMatch(group);
            }

            // Усредняем индекс совпадений для текущей длины ключа
            indexes[i - 1] = sumIC / possibleLength;
        }

        double average = 0, max = 0;
        for (double value : indexes) {
            average += value;
            if (value > max) {
                max = value;
            }
        }

        average /= indexes.length;
        average = (max + average) / 2;

        if((max - average) < 0.0035) return 1;

        int length = 0;
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] > average) {
                length = i + 1;
                break;
            }
        }

        return length;
    }

    static double indexOfMatch (String text) {
        int length = text.length();
        if (length == 1) {
            return 0;
        }

        double sum = 0;
        long[] lettersFrequencies = Lab6Caesar.observedLettersFrequencies(text);
        for (int i = 0; i < lettersFrequencies.length; i++) {
            if (lettersFrequencies[i] == 0) {
                continue;
            }

            sum += Math.pow((double) lettersFrequencies[i] / length, 2);
        }

        return sum;
    }
}
