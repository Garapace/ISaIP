import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static int alphabetPower;               // Мощность алфавита: a-z + A-Z = 52.  | N = 52
    private static double passwordPerSecond = 10;  // Скорость перебора паролей в секунду | S
    private static int maxIncorrectAttempts = 2;    // Количество неправильных попыток     | T
    private static int delay = 10;                   // Пауза, после T неправильных попыток | V

    private static final Set<Character> specialChars = new HashSet<>();

    // символ двойные ковычки ["] и обратный слэш [\] нужно экранировать -> ["] = [\"], [\] -> [\\]
    static {
        for (char ch : " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray()) { 
            specialChars.add(ch);
        }
    }
    
    public static void main(String[] args) {
        print(10, 8);
        print(26, 8);
        print(36, 7);
        print(95, 6);


        Scanner s = new Scanner(System.in);
        System.out.println("Введите ваш пароль:\n");
        String userPassword = s.nextLine();
        s.close();
        int[] userPasswordInfo = userPasswordCheck(userPassword);
        print(userPasswordInfo[0], userPasswordInfo[1]);
    }

    public static void print(int alphabetSize, int passwordLength) {
        alphabetPower = alphabetSize;
        double[] temp = timeCheck(passwordLength);
        System.out.println("Мощность алфавита:\t" + alphabetPower);
        System.out.println("Длина пароля:\t\t" + passwordLength);
        System.out.println("Количество комбинаций:\t" + temp[0]);
        System.out.println("Время перебора:\t\t" + timeFormat((long) temp[1]));
        System.out.println();
    }
    
    public static boolean[] reliabilityCheck(String password) {
        boolean lengthCheck = password.length() >= 8; // если пароль 8 символов и более
        boolean lowercaseCheck = false;
        boolean uppercaseCheck = false;
        boolean digitCheck = false;
        boolean specialCharsCheck = false;
        boolean anotherCharsCheck = false;

        for (char ch : password.toCharArray()) {
            if (ch >= 'a' && ch <='z') lowercaseCheck = true;
            else if (ch >= 'A' && ch <='Z') uppercaseCheck = true;
            else if (Character.isDigit(ch)) digitCheck = true;
            else if (specialChars.contains(ch)) specialCharsCheck = true;
            else anotherCharsCheck = false;
        }
        
        return new boolean[] {lengthCheck, lowercaseCheck, uppercaseCheck, digitCheck, specialCharsCheck, anotherCharsCheck};
    }

    public static double[] timeCheck(int passwordLength) {
        double totalCombinations = Math.pow(alphabetPower, passwordLength);               // M = N^L - мощность пространства паролей
        double timeWODelay = totalCombinations / passwordPerSecond;                       // количество секунд для проверки всех паролей
        double timeWDelay = Math.floor(totalCombinations / maxIncorrectAttempts) * delay; // количество секунд для проверки всех паролей с учётом паузы при неправильных попытках
        
        if (alphabetPower % maxIncorrectAttempts == 0) timeWDelay -= delay;

        return new double[] {totalCombinations, timeWDelay + timeWODelay};
    }

    public static String timeFormat(long seconds) {
        if (seconds < 60) return seconds + " секунд";

        long minutes = (seconds / 60);
        seconds %= 60;
        if (minutes < 60) return minutes + " минут " + seconds + " секунд";

        long hours = minutes / 60;
        minutes %= 60;
        if (hours < 24) return hours + " часов " + minutes + " минут " + seconds + " секунд";

        long days = hours / 24;
        hours %= 24;
        if (days < 30) return days + " дней " + hours + " часов " + minutes + " минут " + seconds + " секунд";

        long month = days / 30;
        days %= 30;
        if (month < 12) return month + " месяцев " + days + " дней " + hours + " часов " + minutes + " минут " + seconds + " секунд";

        long years = month / 12;
        month %= 12;
        return years + " лет " + month + " месяцев " + days + " дней " + hours + " часов " + minutes + " минут " + seconds + " секунд";
    }

    public static int[] userPasswordCheck(String password) {
        alphabetPower = 0;
        boolean lowercaseCheck = false;
        boolean uppercaseCheck = false;
        boolean digitCheck = false;
        boolean specialCharsCheck = false;

        for (char ch : password.toCharArray()) {
            if (ch >= 'a' && ch <='z') lowercaseCheck = true;
            else if (ch >= 'A' && ch <='Z') uppercaseCheck = true;
            else if (Character.isDigit(ch)) digitCheck = true;
            else if (specialChars.contains(ch)) specialCharsCheck = true;
            else throw new IllegalArgumentException("Недопустимый символ в пароле: '" + ch + "'");
        }

        if (lowercaseCheck) alphabetPower += 26;
        if (uppercaseCheck) alphabetPower += 26;
        if (digitCheck) alphabetPower += 10;
        if (specialCharsCheck) alphabetPower += 33;
        
        return new int[] {alphabetPower, password.length()};
    }
}
