package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static javax.print.attribute.standard.MediaSizeName.C;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Pane anchorPane = fxmlLoader.load();
        controller = fxmlLoader.getController();
        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        controller.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
