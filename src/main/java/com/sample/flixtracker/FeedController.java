package com.sample.flixtracker;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class FeedController implements Initializable {
    @FXML
    public Label welcomeUserLabel;
    @FXML
    public FlowPane mainFlowPane;

    Map<String, String > genreIdMap = new HashMap<String, String>();

    Map<String,Integer> genreRatings = new HashMap<String,Integer>();

    List<Movie> feedMovies = new ArrayList<Movie>();

    APIServices serviceObject = new APIServices();

    String username = GlobalData.getUserId();

    public List<Movie> searchFeed() throws Exception{
        //Method to fetch movies for feed using API

        List<String> genreArr = new ArrayList<String>();
        for (Map.Entry<String,Integer> entry : genreRatings.entrySet())
        {
            if(entry.getValue()>=15)
                genreArr.add(genreIdMap.get(entry.getKey()));
        }
        int currPage = 1;
        HttpURLConnection connection = null;
        for(String genre:genreArr)
        {
            System.out.println(genre);
            final String mykey = serviceObject.getAPI_KEY();
            String tmdbURL = ("https://api.themoviedb.org/3/discover/movie?api_key=" + mykey + "&language=en-US" + "&with_genres=" + genre + "&page=" + currPage);
            System.out.println(tmdbURL);
            DashboardController dashboardController = new DashboardController();
            feedMovies = dashboardController.getData(tmdbURL);
        }
        System.out.println("Size: " + feedMovies.size());
        return feedMovies;
    }

    public void updateFeedMovies(){
        //Method to display all the liked movies

        try{
            for (Movie movie : feedMovies) {
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GenreMapController genreMapController = new GenreMapController();
        try {
            welcomeUserLabel.setText("Welcome to your Feed");
            genreRatings = genreMapController.getMap(username);
            genreIdMap = genreMapController.getStringtoIDMap();
            searchFeed();

            mainFlowPane.getChildren().clear();
            new Thread(new Runnable() {
                @Override public void run() {
                    boolean adult = false;
                    final String mykey = serviceObject.getAPI_KEY();
                    updateFeedMovies();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
