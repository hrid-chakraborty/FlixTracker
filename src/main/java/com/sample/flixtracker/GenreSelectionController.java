package com.sample.flixtracker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

public class GenreSelectionController implements Initializable {
    @FXML
    private CheckBox actionCheckBox;
    @FXML
    private CheckBox comedyCheckBox;
    @FXML
    private CheckBox dramaCheckBox;
    @FXML
    private CheckBox fantasyCheckBox;
    @FXML
    private CheckBox horrorCheckBox;
    @FXML
    private CheckBox mysteryCheckBox;
    @FXML
    private CheckBox romanceCheckBox;
    @FXML
    private CheckBox thrillerCheckBox;
    @FXML
    private Button submitButton;

    List<CheckBox> genreCheckBox = new ArrayList<CheckBox>();
    Map<String, Boolean> genreMap = new HashMap<String, Boolean>();
    Map<String,Integer> genreRating = new HashMap<String,Integer>();
    String username = GlobalData.getUserId();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.setDisable(true);
        final int[] cnt = {0};
        genreCheckBox.add(actionCheckBox);
        genreCheckBox.add(comedyCheckBox);
        genreCheckBox.add(dramaCheckBox);
        genreCheckBox.add(fantasyCheckBox);
        genreCheckBox.add(horrorCheckBox);
        genreCheckBox.add(mysteryCheckBox);
        genreCheckBox.add(romanceCheckBox);
        genreCheckBox.add(thrillerCheckBox);

        for (CheckBox language : genreCheckBox) {
            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    if (language.isSelected()) {
                        System.out.println(language.getText() + " is Selected");
                        cnt[0]++;
                    }else{
                        System.out.println(language.getText() + " is not Selected");
                        cnt[0]--;
                    }
                    if(cnt[0] > 2){
                        submitButton.setDisable(false);
                    }else{
                        submitButton.setDisable(true);
                    }
                }
            };
            language.setOnAction(event);
        }
        System.out.println(cnt[0]);
    }

    public void onSubmitPressed(ActionEvent actionEvent) throws Exception {
        for (CheckBox genre: genreCheckBox){
            if(genre.isSelected()){
                genreMap.put(genre.getText(),true);
            }
            else{
                genreMap.put(genre.getText(),false);
            }
        }
        setGenreInRegistrationController();
        saveGenres(username);

        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/userLogin.fxml"));
        Stage loginStage = new Stage();
        loginStage.setResizable(false);
        loginStage.initStyle(StageStyle.DECORATED);
        loginStage.setTitle("Login to FlixTracker");
        loginStage.setScene(new Scene(root));
        loginStage.show();

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    public void setGenreInRegistrationController() throws Exception {
        for (Map.Entry<String, Boolean> entry : genreMap.entrySet()) {
            if(entry.getValue())
                genreRating.put(entry.getKey(),15);
            else
                genreRating.put(entry.getKey(),0);
        }
    }

    public void saveGenres(String username) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection myConn = DriverManager.getConnection(url, "root", ""); //Connect to database (Requires JDBC) [Default username:root, pw empty]
        String queryGenre="INSERT INTO `genre`(`Username`, `Action`, `Comedy`, `Drama`, `Fantasy`, `Horror`, `Mystery`, `Romance`, `Thriller`) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement prepStatementGenre = myConn.prepareStatement(queryGenre);

        prepStatementGenre.setString(1,username);
        prepStatementGenre.setInt(2,genreRating.get("Action"));
        prepStatementGenre.setInt(3,genreRating.get("Comedy"));
        prepStatementGenre.setInt(4,genreRating.get("Drama"));
        prepStatementGenre.setInt(5,genreRating.get("Fantasy"));
        prepStatementGenre.setInt(6,genreRating.get("Horror"));
        prepStatementGenre.setInt(7,genreRating.get("Mystery"));
        prepStatementGenre.setInt(8,genreRating.get("Romance"));
        prepStatementGenre.setInt(9,genreRating.get("Thriller"));

        prepStatementGenre.executeUpdate();
    }
}
