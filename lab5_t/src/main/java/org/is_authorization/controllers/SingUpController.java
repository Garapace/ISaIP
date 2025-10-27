package org.is_authorization.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;

public class SingUpController {
    protected static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        SingUpController.userData = userData;
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
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private Button singUpButton;

    @FXML
    private Button backButton;
    
    @FXML
    void initialize() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        //отмена
        backButton.setOnAction(actionEvent -> {
            try {
                if (userData != null) {
                    if (userData.getString(Const.USER_LOGIN).equals(Const.ADMIN_LOGIN)) {
                        MainController.load(backButton, "UI/adminWindow.fxml");
                    }
                } else {
                    MainController.load(backButton, "UI/mainWindow.fxml");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        //создать
        singUpButton.setOnAction(actionEvent -> {
            String loginText = loginField.getText().trim();
            String passwordText = passwordField.getText();
            String repeatPasswordText = repeatPasswordField.getText();

            if (!loginText.equals("") && !passwordText.equals("") && !repeatPasswordText.equals("")) {
                if (!passwordText.equals(repeatPasswordText)) {
                    MainController.alert("Ошибка", "Пароли не совпадают");
                } else {
                    try {
                        dbHandler.signUpUser(loginText,passwordText);
                        ResultSet resultSet = dbHandler.getUser(loginText, passwordText);

                        resultSet.next();

                        if (userData != null) {
                            if (userData.getString(Const.USER_LOGIN).equals(Const.ADMIN_LOGIN)) {
                                MainController.alert("Успешно", "Пользователь создан");
                                MainController.load(singUpButton, "UI/adminWindow.fxml");
                            }
                        } else {
                            DefaultUserController.setUserData(resultSet);
                            MainController.load(singUpButton, "UI/defaultUserWindow.fxml");
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        MainController.alert("Ошибка", "Пользователь с таким логином уже существует");
                    }
                }
            } else {
                MainController.alert("Ошибка", "Не все поля заполнены");
            }
        });
    }

}
