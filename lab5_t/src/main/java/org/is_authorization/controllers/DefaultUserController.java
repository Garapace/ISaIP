package org.is_authorization.controllers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;


public class DefaultUserController {

    private static final Set<Character> SPECIAL_ABC = new HashSet<>();

    static  {
        for (char ch : "!@#$%^&*()_".toCharArray()) {
            SPECIAL_ABC.add(ch);
        }
    }

    private static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        DefaultUserController.userData = userData;
    }

    public static ResultSet getUserData() {
        return userData;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button singOutButton;

    @FXML
    private Label textWithLogin;

    @FXML
    void initialize() throws SQLException, ClassNotFoundException, InterruptedException {
        textWithLogin.setText("Добро пожаловать, " + userData.getString(Const.USER_LOGIN));

        Task<Boolean> passwordCheckTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (userData.getInt(Const.PASSWORD_ID) != 1) {
                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    ResultSet passwordSetting = databaseHandler.getPasswordSetting(userData.getString(Const.USER_LOGIN));
                    passwordSetting.next();

                    return passwordCheck(userData.getString(Const.USER_PASSWORD),
                            passwordSetting.getInt(Const.PASSWORD_LENGTH),
                            passwordSetting.getBoolean(Const.PASSWORD_UPPERCASE),
                            passwordSetting.getBoolean(Const.PASSWORD_LOWERCASE),
                            passwordSetting.getBoolean(Const.PASSWORD_SPECIAL));
                }
                return true; // Если проверка не требуется
            }
        };

        // Действие после завершения задачи
        passwordCheckTask.setOnSucceeded(event -> {
            boolean isPasswordValid = passwordCheckTask.getValue();
            if (!isPasswordValid) {
                MainController.alert("Уведомление", "Ваш пароль не удовлетворяет условиям");
                ChangePasswordController.setUserData(userData);
                MainController.load(changePasswordButton, "UI/changePasswordWindow.fxml");
            }
        });

        // Запуск задачи в фоновом потоке
        new Thread(passwordCheckTask).start();

        // смена пароля
        changePasswordButton.setOnAction(actionEvent -> {
            ChangePasswordController.setUserData(userData);
            MainController.load(changePasswordButton, "UI/changePasswordWindow.fxml");
        });

        // выход
        singOutButton.setOnAction(actionEvent -> {
            MainController.load(singOutButton, "UI/mainWindow.fxml");
        });
    }

    public static boolean passwordCheck (String password, int passwordLength, boolean isUpperCase, boolean isLowerCase, boolean isSpecial) {

        if (password.length() < passwordLength) {
            return false;
        }

        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                hasLowerCase = true;
            } else if (ch >= 'A' && ch <= 'Z') {
                hasUpperCase = true;
            } else if (SPECIAL_ABC.contains(ch)) {
                hasSpecial = true;
            }
        }

        // Проверка минимальных требований
        if (isLowerCase && !hasLowerCase) {
            return false; // Если требуется нижний регистр, но его нет
        }
        if (isUpperCase && !hasUpperCase) {
            return false; // Если требуется верхний регистр, но его нет
        }
        if (isSpecial && !hasSpecial) {
            return false; // Если требуется специальный символ, но его нет
        }

        // Если все требования выполнены
        return true;
    }
}
