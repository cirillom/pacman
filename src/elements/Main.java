package elements;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pacman.fxml")); //loads the fxml file with interface configuration
        Parent root = loader.load();

        primaryStage.setTitle("PacMan");

        Controller controller = loader.getController();
        root.setOnKeyPressed(controller);

        Font.loadFont(getClass().getResourceAsStream("fonts/Pixeboy.ttf"), 16); //loads the default font
        double sceneWidth = controller.getBoardWidth() + 20.0;
        double sceneHeight = controller.getBoardHeight() + 200.0;
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.getStylesheets().add(getClass().getResource("pacman.css").toExternalForm()); //configure the default font

        primaryStage.setScene(scene);
        primaryStage.show();
        
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
