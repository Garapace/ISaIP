package org.is_authorization.controllers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;


public class ChangePasswordController {
    private static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        ChangePasswordController.userData = userData;
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
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private Label textLabel;

    @FXML
    void initialize() throws SQLException {
        DatabaseHandler databaseHandler = new DatabaseHandler();

        // отмена
        backButton.setOnAction(actionEvent -> {
            try {
                if (userData.getString(Const.USER_LOGIN).equals(Const.ADMIN_LOGIN)) {
                    MainController.load(backButton, "UI/adminWindow.fxml");
                } else {
                    MainController.load(backButton, "UI/defaultUserWindow.fxml");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // смена пароля
        acceptButton.setOnAction(actionEvent -> {
            String passwordText = passwordField.getText();
            String repeatPasswordText = repeatPasswordField.getText();

            if (!passwordText.equals("") && !repeatPasswordText.equals("")) {
                if (!passwordText.equals(repeatPasswordText)) {
                    MainController.alert("Ошибка", "Пароли не совпадают");
                } else {
                    try {
                        databaseHandler.updatePassword(userData.getString(Const.USER_LOGIN), passwordField.getText());
                        MainController.alert("Успешно", "Пароль изменён");

                        ResultSet resultSet = databaseHandler.getUser(userData.getString(Const.USER_LOGIN), passwordField.getText());
                        resultSet.next();

                        if (userData.getString(Const.USER_LOGIN).equals(Const.ADMIN_LOGIN)) {
                            AdminController.setUserData(resultSet);
                            MainController.load(acceptButton, "UI/adminWindow.fxml");
                        } else {
                            DefaultUserController.setUserData(resultSet);
                            MainController.load(acceptButton, "UI/defaultUserWindow.fxml");
                        }

                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                MainController.alert("Ошибка", "Не все поля заполнены");
            }
        });
    }

}
