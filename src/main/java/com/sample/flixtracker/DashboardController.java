package com.sample.flixtracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class DashboardController implements Initializable {
    @FXML
    private TextField movieSearchField;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label myProfileLabel;
    @FXML
    private Label moviesLabel;
    @FXML
    private Label watchListLabel;
    @FXML
    private Label playlistLabel;
    @FXML
    private Label feedLabel;
    @FXML
    private Label friendsLabel;
    @FXML
    private Button signOutButton;
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TextField searchByYearField;
    @FXML
    private MenuButton genresMenuButton;
    @FXML
    private ImageView refreshImageView;
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private FlowPane mainFlowPane;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Pagination pagination;
    @FXML
    private ScrollPane sideScrollPane;
    @FXML
    private FlowPane sideFlowPane;

    APIServices apiServiceObject =new APIServices();
    Map<String, String > genreIdMap = new HashMap<String, String>();
    int currentPage = 1;
    int searchPage = 1;
    private List<Movie> dashboardMovies ; //List of movies on dashboard

    public List<Movie> getData(String tmdbURL) throws Exception{
        URL url = new URL(tmdbURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder response = new StringBuilder();
        String line = null;
        while ((line= reader.readLine())!=null){
            response.append(line);
            response.append("\r");
        }
        reader.close();
        String result = response.toString();
        JSONObject jsonObject1 = new JSONObject(result);
        JSONArray jsonArray = jsonObject1.getJSONArray("results");

        List<Movie> movies = new ArrayList<>();
        Movie movie;
        for(int i=0;i<jsonArray.length() && i < 20;i++){
            movie = new Movie();

            movie.setGenre("Action");
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            movie.setJsonObject(jsonObject);
            try{
                //int genreLength = jsonObject.getString("genre_ids").length();
                //String str = jsonObject.getString("genre_ids").substring(1, genreLength-2);
                String arr = jsonObject.getJSONArray("genre_ids").toString();
                String[] genreString = arr.split(",");
                movie.setGenre("Other");
                for(String s : genreString){
                    if(genreIdMap.get(s) != null){
                        movie.setGenre(genreIdMap.get(s));
                        break;
                    }
                }
            }catch (Exception e){
                movie.setGenre("Other");
            }

            movie.setName( jsonObject.getString("original_title"));
            movie.setImageSource( "https://image.tmdb.org/t/p/w500"+jsonObject.getString("poster_path"));

            String year = jsonObject.getString("release_date");
            movie.setYear(year.split("-")[0]);
            movie.setId(jsonObject.getInt("id"));

            movies.add(movie);
        }
        return movies;
    }

    public void updateMoviesOnDashboard(String tmdbURL){
        //Display the movies on the dashboard
        try {
            boolean adult = false;
            final String mykey = apiServiceObject.getAPI_KEY();
            dashboardMovies.addAll(getData(tmdbURL));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            for (Movie movie : dashboardMovies) {
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //it displays the latest release of the year on right side of dashboard
    private final List<Movie> tryNewMovies = new ArrayList<>();
    public void updateSideMovieOnDashboard(){
        //Method to display movies on the side on dashboard
        try {
            final String mykey = apiServiceObject.getAPI_KEY();
            boolean adult = true;
            tryNewMovies.addAll(getData("https://api.themoviedb.org/3/movie/upcoming?api_key=" + mykey + "&language=en-US&page=1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            for (Movie tryNewMovie : tryNewMovies) {
                FXMLLoader sidefxmlLoader = new FXMLLoader();
                sidefxmlLoader.setLocation(getClass().getResource("fxmlfiles/newMovies.fxml"));

                VBox vBox = sidefxmlLoader.load();
                NewMoviesController tryNewMovieController = sidefxmlLoader.getController();
                tryNewMovieController.setData(tryNewMovie);

                Platform.runLater(()->{
                    sideFlowPane.getChildren().add(vBox);
                });
                FlowPane.setMargin(vBox, new Insets(15));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //this is used to store the movie array
    private List<Movie> searchMoviesArray ;
    //it displays the movie list which was searched by user
    public void updateMoviesOnDashboardOnSearch(String tmdbURL){
        //Method to handle search on dashboard
        try {
            final String myKey = apiServiceObject.getAPI_KEY();
            boolean adult = true;
            searchMoviesArray.addAll(getData(tmdbURL));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{

            for (Movie movie : searchMoviesArray) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("fxmlfiles/movie.fxml"));

                VBox anchorPane = fxmlLoader.load();
                MovieController movieController = fxmlLoader.getController();
                movieController.setData(movie);

                Platform.runLater(()->{
                    mainFlowPane.getChildren().add(anchorPane);
                });
                FlowPane.setMargin(anchorPane, new Insets(10));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // this update dashboard by genre which is selected by user
    public void updateMoviesByGenre(String id){
        //Method to update movies by genre, takes string id as argument
        dashboardMovies = new ArrayList<>();
        mainFlowPane.getChildren().clear();
        new Thread(new Runnable() {
            @Override public void run() {
                boolean adult = false;
                final String mykey = apiServiceObject.getAPI_KEY();
                System.out.println(id);
                updateMoviesOnDashboard("http://api.themoviedb.org/3/genre/"+id+ "/movies?api_key=" + mykey);
            }
        }).start();

    }
    public void setUserNameInDashboardController(String text) {
        welcomeUserLabel.setText("Welcome " + text);
    }

    public void onSignOutPressed(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sign Out!");
        alert.setHeaderText("You're about to sign out");
        alert.setContentText("Do you want to exit?");

        if(alert.showAndWait().get()==ButtonType.OK) {
            Stage stage = (Stage) signOutButton.getScene().getWindow();
            System.out.println("You successfully logged out!");
            stage.close();
            GlobalData.setUserID("");
            GlobalData.setUserAge(0);
            Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/userLogin.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Login to FlixTracker");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();
        }
    }

    public void onPrevPressed(ActionEvent actionEvent) {
        //Method for when previous button is clicked
        currentPage = currentPage - 1;
        if(currentPage == 1){
            prevButton.setDisable(true);
        }
        dashboardMovies = new ArrayList<>();
        mainFlowPane.getChildren().clear();
        new Thread(new Runnable() {
            @Override public void run() {
                boolean adult = false;
                APIServices apiServicesObject = new APIServices();
                final String mykey = apiServicesObject.getAPI_KEY();
                updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                        "&language=en-US&page=" + currentPage);
            }
        }).start();
    }

    public void onNextPressed(ActionEvent actionEvent) {
        //Method for when the next Button is clicked
        currentPage = currentPage + 1;
        prevButton.setDisable(false);
        dashboardMovies = new ArrayList<>();
        mainFlowPane.getChildren().clear();
        new Thread(new Runnable() {
            @Override public void run() {
                boolean adult = false;
                APIServices apiServicesObject = new APIServices();
                final String mykey = apiServicesObject.getAPI_KEY();
                updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                        "&language=en-US&page=" + currentPage);

            }
        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardMovies = new ArrayList<>();
        Thread thread1 = new Thread(new Runnable() {
            @Override public void run() {
                boolean adult = false;
                final String mykey = apiServiceObject.getAPI_KEY();
                updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                        "&language=en-US&page=" + currentPage);
                System.out.println("updateMovies Successful");
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override public void run() {
                updateSideMovieOnDashboard();
            }
        });

        thread1.start();
        thread2.start();

        prevButton.setDisable(true);

        genreIdMap.put("28","Action");
        genreIdMap.put("35","Comedy");
        genreIdMap.put("18","Drama");
        genreIdMap.put("80","Crime");
        genreIdMap.put("14","Fantasy");
        genreIdMap.put("27","Horror");
        genreIdMap.put("9648","Mystery");
        genreIdMap.put("10749","Romance");
        genreIdMap.put("53","Thriller");

        dashboardMovies = new ArrayList<>();

        genreIdMap.forEach((id, genre) -> {
            System.out.println(id + " : " + (genre));
            MenuItem menuItem = new MenuItem(genre);
            menuItem.addEventHandler(ActionEvent.ACTION, action ->{
                System.out.println(menuItem.getText() + " : is selected");
                updateMoviesByGenre(id);
            });
            genresMenuButton.getItems().add(menuItem);

        });

        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->{
            currentPage = (int) newIndex + 1;
            dashboardMovies = new ArrayList<>();
            mainFlowPane.getChildren().clear();
            new Thread(new Runnable() {
                @Override public void run() {
                    boolean adult = true;
                    final String mykey = apiServiceObject.getAPI_KEY();
                    updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                            "&language=en-US&page=" + currentPage);
                    System.out.println("updateMovies Successful");
                }
            }).start();

        });
    }

    public void keyPressedOnSearchMovies(KeyEvent keyEvent) {
        //Method to handle keyevent when key pressed on search movies
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            searchMoviesArray = new ArrayList<>();
            System.out.println(Objects.equals(movieSearchField.getText(), ""));
            if(movieSearchField.getText() == ""){
                dashboardMovies = new ArrayList<>();
                mainFlowPane.getChildren().clear();
                new Thread(new Runnable() {
                    @Override public void run() {
                        boolean adult = false;
                        APIServices apiServicesObject = new APIServices();
                        final String mykey = apiServicesObject.getAPI_KEY();
                        updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                                "&language=en-US&page=" + currentPage);
                    }
                }).start();
            }else{
                System.out.println("You searched movie having name " + movieSearchField.getText());
                mainFlowPane.getChildren().clear();
                new Thread(new Runnable() {
                    @Override public void run() {
                        APIServices apiServicesObject = new APIServices();
                        final String myKey = apiServicesObject.getAPI_KEY();
                        boolean adult = true;
                        updateMoviesOnDashboardOnSearch("https://api.themoviedb.org/3/search/movie?api_key=" + myKey + "&language=en-US&page=" + searchPage + "&include_adult="+ adult + "&query=" + movieSearchField.getText());
                    }
                }).start();
            }

        }
    }

    public void mousePressedOnWatchList(MouseEvent mouseEvent) throws IOException {
        //Method to load new stage when watchlist is clicked on sidebar
        System.out.println("here you'll find all your saved watchlist");
        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/watchListPage.fxml"));
        Stage watchListStage = new Stage();
        watchListStage.setScene(new Scene(root));
        watchListStage.setTitle("WatchList Page");
        watchListStage.setResizable(false);
        watchListStage.initModality(Modality.APPLICATION_MODAL);
        watchListStage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow() );
        watchListStage.show();
    }

    public void mousePressedOnPlaylist(MouseEvent mouseEvent) throws IOException {
        //Method to load new stage when playlist is clicked on sidebar
        System.out.println("Here you'll find all your playlist movies");
        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/playlistPage.fxml"));
        Stage playListStage = new Stage();
        playListStage.setScene(new Scene(root));
        playListStage.setTitle("Playlist Page");
        playListStage.setResizable(false);
        playListStage.initModality(Modality.APPLICATION_MODAL);
        playListStage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow() );
        playListStage.show();
    }

    public void mousePressedOnFeed(MouseEvent mouseEvent) throws IOException {
        //Method to load new stage when feed is clicked on sidebar
        System.out.println("Here you'll find all your feed movies");
        Parent root = FXMLLoader.load(getClass().getResource("fxmlfiles/feedPage.fxml"));
        Stage FeedStage = new Stage();
        FeedStage.setScene(new Scene(root));
        FeedStage.setResizable(false);
        FeedStage.setTitle("Feed Page");
        FeedStage.initModality(Modality.APPLICATION_MODAL);
        FeedStage.initOwner(((Node)mouseEvent.getSource()).getScene().getWindow() );
        FeedStage.show();
    }

    public void mousePressedOnRefreshImageView(MouseEvent mouseEvent) {
        System.out.println("refresh to get new recommendations");
        mainFlowPane.getChildren().clear();
        new Thread(new Runnable() {
            @Override public void run() {
                boolean adult = false;
                final String mykey = apiServiceObject.getAPI_KEY();
                System.out.println("Trying to run updateMovies");
                updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                        "&language=en-US&page=" + currentPage);
                System.out.println("updateMovies Successful");
            }
        }).start(); 
    }

    public void keyPressedOnSearchByYear(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            searchMoviesArray = new ArrayList<>();
            System.out.println(searchByYearField.getText()=="");
            if(searchByYearField.getText() == ""){
                dashboardMovies = new ArrayList<>();
                mainFlowPane.getChildren().clear();
                new Thread(new Runnable() {
                    @Override public void run() {
                        boolean adult = false;
                        final String mykey = apiServiceObject.getAPI_KEY();
                        updateMoviesOnDashboard("https://api.themoviedb.org/3/movie/popular?api_key="+ mykey +
                                "&language=en-US&page=" + currentPage);
                    }
                }).start();
            }else{
                System.out.println("You searched movie having name " + searchByYearField.getText());
                mainFlowPane.getChildren().clear();
                new Thread(new Runnable() {
                    @Override public void run() {
                        final String myKey = apiServiceObject.getAPI_KEY();
                        boolean adult = false;

                        String tmdbURL = "https://api.themoviedb.org/3/discover/movie?api_key=" + myKey + "&language=en-US"
                                + "&include_adult=" + adult + "&page="+ searchPage + "&include_adult="+ adult  +
                                "&primary_release_year=" + searchByYearField.getText();

                        updateMoviesOnDashboardOnSearch(tmdbURL);
                    }
                }).start();
            }

        }
    }
}
