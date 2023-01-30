package com.sample.flixtracker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MovieController {

    @FXML
    private Label genreLabel;

    @FXML
    private ImageView likeImageView;

    @FXML
    private ImageView poster;

    @FXML
    private Label titleLabel;

    @FXML
    private Label yearLabel;

    protected Movie movie;
    public boolean clicked = false;

    public void setData(Movie movie){
        // Method to set data for movies
        this.movie = movie;
        titleLabel.setText(movie.getName());
        genreLabel.setText(movie.getGenre());
        yearLabel.setText(movie.getYear());
        movie.setJsonObject(movie.getJsonObject());
        Image image = new Image(movie.getImageSource());
        poster.setImage(image);
    }
    public void mousePressedOnPoster(MouseEvent mouseEvent) throws Exception{
        System.out.println("user clicked on movie, show him movie description");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmlfiles/movieInfo.fxml"));
        Parent root = loader.load();
        MovieInfoController movieInfoController = loader.getController();
        movieInfoController.setEverythingInMovieInfo(movie);
        Stage movieInfoStage = new Stage();
        movieInfoStage.setResizable(false);
        movieInfoStage.initStyle(StageStyle.UNDECORATED);
        movieInfoStage.setTitle("Info");
        movieInfoStage.setScene(new Scene(root, 930, 545));
        movieInfoStage.initModality(Modality.APPLICATION_MODAL);
        movieInfoStage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow() );
        movieInfoStage.show();
    }

    public void mousePressedOnLike(MouseEvent mouseEvent) throws Exception {
        clicked = !clicked;
        if(clicked == true) {
            Image image = new Image(getClass().getResource("images/blueLove.png").toURI().toString());
            likeImageView.setImage(image);
            likeImageView.setFitWidth(29);
            likeImageView.setFitHeight(30);
            LikeController likeController = new LikeController();
            likeController.addLikedMovietoDB(movie);
        }else{
            Image image = new Image(getClass().getResource("images/unfilledLike.png").toURI().toString());
            likeImageView.setImage(image);
            likeImageView.setFitWidth(29);
            likeImageView.setFitHeight(30);
            LikeController likeController = new LikeController();
            likeController.removeLikedMovieFromDB(movie);
        }
        DropShadow d = new DropShadow();
        d.setSpread(.7);
        likeImageView.setEffect(d);
    }

}
