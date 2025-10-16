package app;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class App extends Application {
    private static Scene scene;
    private static Stage stage;
    private static double xOffset;
    private static double yOffset;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        Parent root = loadFXML("/ui/scenes/Login");

        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("ChookChak");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/ui/scenes/Images/logo1.png")));

        dragable(root);

        stage.show();
        stage.centerOnScreen();
    }

    public static void setRootWithFade(String fxml) throws IOException {
        Parent newRoot = loadFXML(fxml);
        newRoot.setOpacity(0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), scene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
        
        fadeOut.setOnFinished(e -> {
            scene.setRoot(newRoot);
            stage.sizeToScene();
            dragable(newRoot);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newRoot);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    private static void dragable(Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private static Parent loadFXML(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml")).load();
    }

    public static void main(String[] args) {
        launch();
    }
}
