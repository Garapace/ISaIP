package org.is_authorization.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.is_authorization.DatabaseHandler;
import org.is_authorization.data.Const;

public class AdminController {

    private static ResultSet userData;

    public static void setUserData(ResultSet userData) {
        AdminController.userData = userData;
    }

    public static ResultSet getUserData() {
        return userData;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addUserButton;

    @FXML
    private Button banUserButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button dataBaseButton;

    @FXML
    private Button singOutButton;

    @FXML
    private Button passwordSettingButton;

    @FXML
    private Label textWithLogin;

    @FXML
    void initialize() throws SQLException {
        textWithLogin.setText("Добро пожаловать, " + userData.getString(Const.USER_LOGIN));
        DatabaseHandler databaseHandler = new DatabaseHandler();

        // смена пароля
        changePasswordButton.setOnAction(actionEvent -> {
            ChangePasswordController.setUserData(userData);
            MainController.load(changePasswordButton, "UI/changePasswordWindow.fxml");
        });

        dataBaseButton.setOnAction(actionEvent -> {
            try (FileWriter fileWriter = new FileWriter("userDatabase.txt")){
                ResultSet resultSet = databaseHandler.getAllUser();
                StringBuilder stringBuilder = new StringBuilder();

                addRow(stringBuilder, "id", "user_name", "password", "ban");
                while (resultSet.next()) {
                    addRow(stringBuilder,
                            resultSet.getString(Const.USERS_ID),
                            resultSet.getString(Const.USER_LOGIN),
                            resultSet.getString(Const.USER_PASSWORD),
                            resultSet.getString(Const.USER_FLAG));
                }

                fileWriter.write(stringBuilder.toString());
                MainController.alert("Успешно", "Данные записаны в файл userDatabase.txt");

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                MainController.alert("Ошибка", "Ошибка при записи в файл: " + e.getMessage());
            }
        });

        // добавить пользователя
        addUserButton.setOnAction(actionEvent -> {
            SingUpController.setUserData(userData);
            MainController.load(addUserButton, "UI/singUpWindow.fxml");
        });

        // бан
        banUserButton.setOnAction(actionEvent -> {
            BanController.setUserData(userData);
            MainController.load(banUserButton, "UI/banWindow.fxml");
        });

        passwordSettingButton.setOnAction(actionEvent -> {
            PasswordSettingController.setUserData(userData);
            MainController.load(passwordSettingButton, "UI/passwordSettingWindow.fxml");
        });

        // выход
        singOutButton.setOnAction(actionEvent -> {
            MainController.load(singOutButton, "UI/mainWindow.fxml");
        });
    }
    private static void addRow(StringBuilder stringBuilder, String id, String name, String password, String flag) {
        stringBuilder.append(String.format("%-5s %-20s %-20s %-1s%n", id, name, password, flag));
    }

}
