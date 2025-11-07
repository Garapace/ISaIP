import java.io.*;

public class StegoBMP {

    private static final int BMP_HEADER_SIZE = 54;

    public static boolean encodeTextToImage(String imageOriginal, String imageStego, String textToStego, int degree) {
        if (degree != 1 && degree != 2 && degree != 4 && degree != 8) {
            System.out.println("Degree value can be only 1/2/4/8");
            return false;
        }

        File imageFile = new File(imageOriginal);
        File textFile = new File(textToStego);

        long textLen = textFile.length();
        long imgLen = imageFile.length();

        if (textLen >= imgLen * degree/8 - BMP_HEADER_SIZE) {
            System.out.println("too long text");
            return false;
        }

        try (
            FileInputStream inputImage = new FileInputStream(imageFile);
            FileOutputStream outputImage = new FileOutputStream(imageStego);
            FileReader textReader = new FileReader(textFile)
        ) {
            // Копируем заголовок BMP
            byte[] bmpHeader = new byte[BMP_HEADER_SIZE];
            inputImage.read(bmpHeader);
            outputImage.write(bmpHeader);

            int[] masks = createMasks(degree);
            int textMask = masks[0];
            int imgMask = masks[1];

            int symbol;
            while ((symbol = textReader.read()) != -1) {
                for (int byteAmount = 0; byteAmount < 8; byteAmount += degree) {
                    int imgByte = inputImage.read();
                    if (imgByte == -1) break;

                    imgByte &= imgMask;
                    int bits = symbol & textMask;
                    bits >>= (8 - degree);
                    imgByte |= bits;

                    outputImage.write(imgByte);
                    symbol <<= degree;
                }
            }

            // Записываем оставшуюся часть изображения без изменений
            inputImage.transferTo(outputImage);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decodeTextFromImage(String imageStego, String stegoOutputText, int symbolsToRead, int degree) {
        if (degree != 1 && degree != 2 && degree != 4 && degree != 8) {
            System.out.println("Degree value can be only 1/2/4/8");
            return false;
        }

        File encodedFile = new File(imageStego);
        long imgLen = encodedFile.length();

        if (symbolsToRead >= imgLen * degree / 8 - BMP_HEADER_SIZE) {
            System.out.println("Too much symbols to read");
            return false;
        }

        try (
            FileInputStream encodedBMP = new FileInputStream(encodedFile);
            FileWriter textWriter = new FileWriter(stegoOutputText)
        ) {
            encodedBMP.skip(BMP_HEADER_SIZE);

            int[] masks = createMasks(degree);
            int imgMask = ~masks[1];

            int read = 0;
            while (read < symbolsToRead) {
                int symbol = 0;

                for (int bitsRead = 0; bitsRead < 8; bitsRead += degree) {
                    int imgByte = encodedBMP.read();
                    if (imgByte == -1) break;

                    imgByte &= imgMask;
                    symbol <<= degree;
                    symbol |= imgByte;
                }

                textWriter.write(symbol);
                read++;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int[] createMasks(int degree) {
        int textMask = 0b11111111;
        int imgMask = 0b11111111;

        textMask <<= (8 - degree);
        textMask %= 256;
        imgMask >>= degree;
        imgMask <<= degree;

        return new int[]{textMask, imgMask};
    }
}
