package com.sample.flixtracker;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieInfoController{
    @FXML
    private ImageView addImageView;
    @FXML
    private ImageView likeImageView;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private ImageView cancelImageView;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label imdbRatingsLabel;
    @FXML
    private Label movieLabel;

    Movie movieObj;
    APIServices serviceObject = new APIServices();
    private boolean isClickedLike = false;
    private boolean isClickedPlus = false;

    public void mousePressedOnCancel2(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelImageView.getScene().getWindow();
        stage.close();
    }

    public void mousePressedOnPlayTrailer(MouseEvent mouseEvent) throws Exception {
        //Method to play trailer on MouseEvent when user hits play
        HttpURLConnection connection = null;
        final String mykey = serviceObject.getAPI_KEY();
        boolean adult = true;

        URL url = new URL("http://api.themoviedb.org/3/movie/" + movieObj.getId() + "/videos?api_key=" + mykey);
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

        JSONObject jsonObject1 = new JSONObject(result);
        JSONArray jsonArray = jsonObject1.getJSONArray("results");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String video = "https://www.youtube.com/embed/" + jsonObject.getString("key");

        Stage trailerStage = new Stage();
        trailerStage.setTitle("Trailer");
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(video);
        Scene scene = new Scene(webView, 930, 545);
        trailerStage.setScene(scene);
        trailerStage.initModality(Modality.APPLICATION_MODAL);
        trailerStage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow() );
        trailerStage.show();
    }

    public void mousePressedOnAddToWatchList(MouseEvent mouseEvent) throws Exception{
        isClickedPlus = !isClickedPlus;
        if(isClickedPlus == true) {
            System.out.println("movie added to watchList");
            Image image = new Image(getClass().getResource("images/filledPlusIcon.png").toURI().toString());
            addImageView.setImage(image);
        }else{
            System.out.println("movie removed from watchList");
            Image image = new Image(getClass().getResource("images/unFilledPlusIcon.png").toURI().toString());
            addImageView.setImage(image);
        }
        addImageView.setFitWidth(29);
        addImageView.setFitHeight(30);
        DropShadow d = new DropShadow();
        d.setSpread(0.66);
        addImageView.setEffect(d);
        System.out.println(movieObj.getName());
        PlayListController playListController = new PlayListController();
        playListController.addMovietoPlaylist(movieObj);
    }

    public void mousePressedOnAddToFavorites(MouseEvent mouseEvent) throws Exception {
        isClickedLike = !isClickedLike;
        if(isClickedLike == true) {
            System.out.println("movie added to the favorites");
            Image image = new Image(getClass().getResource("images/blueLove.png").toURI().toString());
            likeImageView.setImage(image);
            likeImageView.setFitWidth(29);
            likeImageView.setFitHeight(30);

            LikeController likeController = new LikeController();
            likeController.addLikedMovietoDB(movieObj);
        }else{
            System.out.println("movie removed from his favorites");
            Image image = new Image(getClass().getResource("images/unfilledLike.png").toURI().toString());
            likeImageView.setImage(image);
            likeImageView.setFitWidth(29);
            likeImageView.setFitHeight(30);

            LikeController likeController = new LikeController();
            likeController.removeLikedMovieFromDB(movieObj);
        }
        DropShadow d = new DropShadow();
        d.setSpread(0.66);
        likeImageView.setEffect(d);
    }

    public void setEverythingInMovieInfo(Movie movie) throws Exception{
        movieObj = movie;
        JSONObject jsonObject = movie.getJsonObject();
        Image image = new Image("https://image.tmdb.org/t/p/w500"+jsonObject.getString("backdrop_path"));
        backgroundImageView.setImage(image);
        movieLabel.setText( jsonObject.getString("original_title"));
        String overview = jsonObject.getString("overview");
        descriptionTextArea.setText(overview);
    }

}
