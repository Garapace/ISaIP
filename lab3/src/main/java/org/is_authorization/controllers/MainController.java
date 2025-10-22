package org.is_authorization.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.Main;
import org.is_authorization.data.Const;

public class MainController {

    private static ResultSet userData;

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
    private PasswordField passwordField;

    @FXML
    private Button singInButton;

    @FXML
    private Button singUpButton;

    @FXML
    void initialize() {
        userData = null;

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

    private void authUser(String loginText, String passwordText)
            throws SQLException, ClassNotFoundException {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet resultSet = databaseHandler.getUser(loginText, passwordText);

        resultSet.next();
        try {
            if (resultSet.getString(Const.USER_LOGIN).equals(loginText) && resultSet.getString(Const.USER_PASSWORD).equals(passwordText)) {
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
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
