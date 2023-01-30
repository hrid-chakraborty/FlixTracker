package com.sample.flixtracker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class WatchListController implements Initializable {
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private FlowPane mainFlowPane;
    String username = GlobalData.getUserId();
    APIServices apiServicesObject = new APIServices();

    public ResultSet findLikedMoviesInDB() throws Exception{
        //Method to find the likedMovies IDs from DB for particular user & return ResultSet
        System.out.println("here you'll find all your saved watchlist");
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/flixtracker";
        Connection connection = DriverManager.getConnection(url, "root", "");


        Statement stm = connection.createStatement();

        String sql = "select * from movie where Username='" + username + "'";
        return stm.executeQuery(sql);
    }

    public List<Movie> searchLikedMovies(ResultSet res) throws Exception{
        //Method to take the IDs of all the liked movies and run API calls to get the movies & return the movies
        HttpURLConnection connection = null;
        final String mykey = apiServicesObject.getAPI_KEY();
        boolean adult = true;
        List<Movie> likedMoviesArray = new ArrayList<>();

        while (res.next()) {
            String movieid = res.getString("MovieID");
            String username = res.getString("Username");
            System.out.println("   "+movieid+" "+username);
            URL url = new URL("https://api.themoviedb.org/3/movie/" + movieid + "?api_key=" + mykey + "&language=en-US");
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\r");
            }
            reader.close();
            String result = response.toString();
            JSONObject jsonObject = new JSONObject(result);
            System.out.println(jsonObject);
            Movie movie;
            movie = new Movie();
            movie.setJsonObject(jsonObject);
            movie.setName( jsonObject.getString("original_title"));
            movie.setImageSource( "https://image.tmdb.org/t/p/w500"+jsonObject.getString("poster_path"));
            String year = jsonObject.getString("release_date");
            movie.setYear(year.split("-")[0]);
            likedMoviesArray.add(movie);
        }
        return likedMoviesArray;
    }

    public void updateLikedMovies(List<Movie> likedMoviesArray){
        //Method to display all the liked movies
        AtomicInteger col = new AtomicInteger();
        int row = 1;
        try{
            for (Movie movie : likedMoviesArray) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("fxmlfiles/movie.fxml"));

                VBox anchorPane = fxmlLoader.load();
                MovieController movieController = fxmlLoader.getController();
                movieController.setData(movie);

                Platform.runLater(()->{
                    mainFlowPane.getChildren().add(anchorPane);
                });

                FlowPane.setMargin(anchorPane, new Insets(15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            welcomeUserLabel.setText("Welcome to your Watchlist");
            ResultSet res = findLikedMoviesInDB();
            List<Movie> likedMoviesArray = searchLikedMovies(res);

            mainFlowPane.getChildren().clear();
            new Thread(new Runnable() {
                @Override public void run() {
                    updateLikedMovies(likedMoviesArray);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
