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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;

public class RegistrationPageController {
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField ageTextField;

    public void onRegisterPressed(ActionEvent actionEvent) throws Exception{
        String username = usernameTextField.getText();
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection myConn = DriverManager.getConnection(url, "root", ""); //Connect to database (Requires JDBC) [Default username:root, pw empty]
        Statement statement= myConn.createStatement();

        try{
            String query="INSERT INTO `user`(`First Name`, `Last Name`, `Age`, `Username`, `Password`) VALUES (?,?,?,?,?)";
            PreparedStatement prepStatement = myConn.prepareStatement(query);
            prepStatement.setString(1,firstNameTextField.getText());
            prepStatement.setString(2,lastNameTextField.getText());
            prepStatement.setString(3,ageTextField.getText());
            prepStatement.setString(4,usernameTextField.getText());
            prepStatement.setString(5,passwordField.getText());
            if(!firstNameTextField.getText().isEmpty()
            && !ageTextField.getText().isEmpty()
            && !usernameTextField.getText().isEmpty()
            && !passwordField.getText().isEmpty()
            && !confirmPasswordField.getText().isEmpty()
            && passwordField.getText().equals(confirmPasswordField.getText())){
                prepStatement.executeUpdate();
                GlobalData.setUserID(username);

                Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/genreSelection.fxml"));

                Stage genreStage = new Stage();
                genreStage.setResizable(false);
                genreStage.initStyle(StageStyle.DECORATED);
                genreStage.setTitle("Genres");
                genreStage.setScene(new Scene(root));
                genreStage.show();

                Stage registerStage = (Stage) registerButton.getScene().getWindow();
                registerStage.close();
            }
            else{
                errorLabel.setText("Please fill all the data");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
