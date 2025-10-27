package org.is_authorization.controllers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;

public class PasswordSettingController {
    private static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        PasswordSettingController.userData = userData;
    }

    public static ResultSet getUserData() {
        return userData;
    }
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button acceptButton;

    @FXML
    private Button backButton;

    @FXML
    private CheckBox lowerCaseBox;

    @FXML
    private TextField passwordLengthField;

    @FXML
    private CheckBox specialBox;

    @FXML
    private Label textLabel;

    @FXML
    private CheckBox upperCaseBox;

    @FXML
    private TextField userField;

    @FXML
    void initialize() {
        // отмена
        backButton.setOnAction(actionEvent -> {
            AdminController.setUserData(userData);
            MainController.load(backButton, "UI/adminWindow.fxml");
        });

        // принять
        acceptButton.setOnAction(actionEvent -> {
            DatabaseHandler databaseHandler = new DatabaseHandler();

            if (userField.getText().equals("")) {
                MainController.alert("Ошибка", "Поля логина пустое");
                return;
            }

            if (!passwordLengthField.getText().matches("\\d+")) {
                MainController.alert("Ошибка", "Поле длина пароля содержит не только цифры");
                return;
            }

            try {
                ResultSet resultSet = databaseHandler.getUserAdmin(userField.getText());
                resultSet.next();

                String login = resultSet.getString(Const.USER_LOGIN);
                int passwordLength = Integer.parseInt(passwordLengthField.getText());
                boolean isUpperCase = upperCaseBox.isSelected();
                boolean isLowerCase = lowerCaseBox.isSelected();
                boolean isSpecial = specialBox.isSelected();

                databaseHandler.updatePasswordSetting(login, passwordLength, isUpperCase, isLowerCase, isSpecial);
                MainController.alert("Успешно", "Настройки для пароля установлены");
                MainController.load(acceptButton, "UI/adminWindow.fxml");
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(e);
                MainController.alert("Ошибка", "Такого пользователя не существует");
            }
        });
    }

}
