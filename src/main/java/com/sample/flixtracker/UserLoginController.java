package com.sample.flixtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserLoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;

    public void isLoginCorrect() throws IOException {
        if(!usernameTextField.getText().isEmpty() && !passwordField.getText().isEmpty()){
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/flixtracker";
                Connection connection = DriverManager.getConnection(url, "root", "");

                String username = usernameTextField.getText();
                String password = passwordField.getText();

                Statement statement = connection.createStatement();

                String query = "select * from user where Username='" + username + "' and Password='" + password + "'";
                ResultSet result = statement.executeQuery(query);

                if(result.next()){
                    GlobalData.setUserID(username);
                    int age = result.getInt("Age");
                    GlobalData.setUserAge(age);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlfiles/userDashboard.fxml"));
                    Parent root = loader.load();
                    DashboardController dashboardController = loader.getController();
                    dashboardController.setUserNameInDashboardController(usernameTextField.getText());
                    Stage dashboardStage = new Stage();
                    dashboardStage.setResizable(false);
                    dashboardStage.initStyle(StageStyle.DECORATED);
                    dashboardStage.setTitle("FlixTracker");
                    dashboardStage.setScene(new Scene(root));
                    dashboardStage.show();

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.close();
                }
                else{
                    errorMessageLabel.setText("Entered Username/Password is wrong");
                    usernameTextField.setText("");
                    passwordField.setText("");
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void enterPressedOnPasswordField(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            isLoginCorrect();
        }
    }

    public void onLoginPressed(ActionEvent actionEvent) throws IOException {
        isLoginCorrect();
    }

    public void onSignUpPressed(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlfiles/registrationPage.fxml"));
        Stage registrationStage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        registrationStage.setResizable(false);
        registrationStage.setTitle("Create Your Account");
        registrationStage.setScene(scene);
        registrationStage.show();
    }
}