public class Main {
    public static void main(String[] args) {
        int degree = 8  ; // сколько бит в байте будут заменяться на бит(ы) символа из сообщения
        StegoBMP.encodeTextToImage("assets/img/cat.bmp", "assets/img/stego.bmp", "assets/txt/message.txt", degree);
        StegoBMP.decodeTextFromImage("assets/img/stego.bmp", "assets/txt/message_from_image.txt", 50, degree);
    }
}
