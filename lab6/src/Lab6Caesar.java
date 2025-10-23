import java.util.Arrays;

public class Lab6Caesar {
    private static final String ABC = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ";
    private static final double[] russianLettersProbabilities = {
            0.062, 0.014, 0.038, 0.013, 0.025, 0.072,  0.072,
            0.007, 0.016, 0.062, 0.010,  0.028, 0.035,  0.026,
            0.053, 0.090, 0.023, 0.040, 0.045, 0.053, 0.021,
            0.002,  0.009, 0.004, 0.012, 0.006, 0.003, 0.014,
            0.016, 0.014, 0.003, 0.006, 0.018, 0.175
    };

    public static void main(String[] args) {
        String cipher = Lab4.encryptCaesar("ПРИВЕТ МИР", 24);
        System.out.println(breakCipher(cipher));
    }

    public static int breakCipher (String text) {
        double[] expectedLettersFrequencies = Arrays.stream(russianLettersProbabilities)
                .map(probability -> probability * text.length())
                .toArray();

        double[] chiSquares = new double[34];

        for (int i = 0; i < chiSquares.length; i++) {
            String decipheredMessage = Lab4.decryptCaesar(text, i);
            long[] lettersFrequencies = observedLettersFrequencies(decipheredMessage);
            double chiSquare = chiSquare(expectedLettersFrequencies, lettersFrequencies);
            chiSquares[i] = chiSquare;
        }

        int probableOffset = 0;
        for (int i = 0; i < chiSquares.length; i++) {
            // System.out.println((String.format("Chi-Square for offset %d: %.2f", i, chiSquares[i])));
            if (chiSquares[i] < chiSquares[probableOffset]) {
                probableOffset = i;
            }
        }

        return probableOffset;
    }

    static long[] observedLettersFrequencies(String message) {
        return ABC.chars()
                .mapToLong(letter -> countLetter((char) letter, message))
                .toArray();
    }

    static long countLetter(char letter, String message) {
        return message.chars()
                .filter(character -> character == letter)
                .count();
    }

    static double chiSquare(double[] expected, long[] observed) {
        double sum = 0.0;
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] > 0) {
                double diff = observed[i] - expected[i];
                sum += (diff * diff) / expected[i];
            }
        }
        return sum;
    }
}