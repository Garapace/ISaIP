package org.is_authorization.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.Main;
import org.is_authorization.data.Const;

public class MainController {

    private static MainController instance;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    private static ResultSet userData;

    private volatile boolean isBruteForceRunning = false;
    private volatile boolean stopBruteForce = false;
    private volatile long bruteForceStartTime;

    public void setUserData(ResultSet userData) {
        this.userData = userData;
    }

    public static ResultSet getUserData() {
        return userData;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private Button singInButton;

    @FXML
    private Button singUpButton;

    @FXML
    private Button bruteForceButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    void initialize() {
        userData = null;

        bruteForceButton.setOnAction(event -> startVisualBruteForce());

        // авторизация
        singInButton.setOnAction(actionEvent -> {
            String loginText = loginField.getText().trim();
            String passwordText = passwordField.getText();

            if (!loginText.equals("") && !passwordText.equals("")) {
                try {
                    authUser(loginText, passwordText);
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.println(e);
                }
            }
            else {
                alert("Ошибка", "Не указан логин или пароль");
            }
        });

        // переход на регистрацию
        singUpButton.setOnAction(actionEvent -> {
            SingUpController.setUserData(userData);
            load(singInButton, "UI/singUpWindow.fxml");
        });
    }

    private static final String ENGLISH = "qwertyuiopasdfghjklzxcvbnm";

    private void startVisualBruteForce() {

        loginField.setText(Const.ADMIN_LOGIN);
        
        isBruteForceRunning = true;
        stopBruteForce = false;
        bruteForceStartTime = System.currentTimeMillis();

        String login = "admin";
        String alphabet = ENGLISH + "1234567890";
        int maxLen = 4;
        long startTime = System.currentTimeMillis();

        new Thread(() -> {
            try {
                outer:
                for (int len = 1; len <= maxLen; len++) {
                    int[] idx = new int[len];
                    while (true) {
                        if (stopBruteForce) break outer;

                        StringBuilder sb = new StringBuilder(len);
                        for (int i = 0; i < len; i++) sb.append(alphabet.charAt(idx[i]));
                        String password = sb.toString();

                        javafx.application.Platform.runLater(() -> {
                            passwordField.setText(password);
                            singInButton.fire();
                        });

                        Thread.sleep(250);

                        int pos = len - 1;
                        while (pos >= 0) {
                            if (++idx[pos] < alphabet.length()) break;
                            idx[pos] = 0;
                            pos--;
                        }
                        if (pos < 0) break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            double duration = (System.currentTimeMillis() - startTime) / 1000.0;
            isBruteForceRunning = false;

            // просто сообщение в консоль
            if (!stopBruteForce) {
                System.out.println("[×] Перебор окончен. Пароль не найден. Время: " + duration + " сек.");
            }
            // окно alert 
            // javafx.application.Platform.runLater(() ->
            //         alert("Перебор окончен", "Пароль не найден.\nВремя: " + duration + " сек."));
        }).start();
    }

    private void authUser(String loginText, String passwordText)
            throws SQLException, ClassNotFoundException {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet resultSet = databaseHandler.getUser(loginText, passwordText);

        resultSet.next();
        try {
            if (resultSet.getString(Const.USER_LOGIN).equals(loginText) && resultSet.getString(Const.USER_PASSWORD).equals(passwordText)) {
                // Остановим перебор (если он шел)
                stopBruteForce = true;
                isBruteForceRunning = false;

                // Посчитаем время перебора (если был запущен)
                if (bruteForceStartTime > 0L) {
                    double durationSec = (System.currentTimeMillis() - bruteForceStartTime) / 1000.0;
                    System.out.println("\nПароль найден:\t" + passwordText + "\nвремя перебора:\t" + durationSec + " сек.\n");
                } else {
                    System.out.println("Пароль найден: " + passwordText);
                }

                setUserData(resultSet);

                if (resultSet.getString(Const.USER_LOGIN).equals(Const.ADMIN_LOGIN)) {
                    AdminController.setUserData(resultSet);
                    load(singInButton, "UI/adminWindow.fxml");
                } else {
                    DefaultUserController.setUserData(resultSet);
                    load(singInButton, "UI/defaultUserWindow.fxml");
                }
            }
        } catch (SQLException e) {
            alert("Ошибка", "Логин или пароль указаны не корректно");
        }
    }

    public static void alert(String title, String text) {
        // достаём ссылку на контроллер, если нужно — можно через singleton или static
        if (title.equals("Ошибка") && getInstance().isBruteForceRunning) {
            // если перебор активен — просто не показываем окно
            System.out.println("[!] Ошибка во время перебора: " + text);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);

        // если перебор активен, окно сразу закрывается само
        if (getInstance().isBruteForceRunning) {
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    javafx.application.Platform.runLater(alert::close);
                } catch (InterruptedException ignored) {}
            }).start();
        }

        alert.showAndWait();
    }


    public static void load(Button button, String resoursePath) {
        try {
            button.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(resoursePath));
            Scene scene = new Scene(fxmlLoader.load(), 450, 300);

            Stage stage = new Stage();
            stage.setTitle("IS");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
