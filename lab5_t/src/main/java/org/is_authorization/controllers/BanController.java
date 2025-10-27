package org.is_authorization.controllers;


import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;

public class BanController {
    private static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        BanController.userData = userData;
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
    private Label textLabel;

    @FXML
    private TextField userField;

    @FXML
    void initialize() {
        DatabaseHandler databaseHandler = new DatabaseHandler();

        // отмена
        backButton.setOnAction(actionEvent -> {
            MainController.load(backButton, "UI/adminWindow.fxml");
        });

        // принять
        acceptButton.setOnAction(actionEvent -> {
            String login = userField.getText();
            if (!login.equals("")) {
                try {
                    if (login.equals(Const.ADMIN_LOGIN)) {
                        MainController.alert("Ошибка", "Этого пользователя нельзя заблокировать");
                        return;
                    }
                    ResultSet resultSet = databaseHandler.getUserAdmin(login);
                    resultSet.next();
                    if (resultSet.getString(Const.USER_LOGIN).equals(login)) {
                        databaseHandler.banUser(login);
                        MainController.alert("Успешно", "Пользователь заблокирован");
                        MainController.load(acceptButton, "UI/adminWindow.fxml");
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    MainController.alert("Ошибка", "Пользователь с таким логином уже существует");
                }
            } else {
                MainController.alert("Ошибка", "Поле логин не заполненно");
            }
        });

    }

}
