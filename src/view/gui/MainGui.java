// java
package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainGui extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader programListLoader = new FXMLLoader();
            programListLoader.setLocation(MainGui.class.getResource("/GUI/ProgramChooserController.fxml"));
            Parent programListRoot = programListLoader.load();
            ProgramChooserController programChooserController = programListLoader.getController();

            FXMLLoader programExecutorLoader = new FXMLLoader();
            programExecutorLoader.setLocation(MainGui.class.getResource("/GUI/ProgramExecutorController.fxml"));
            Parent programExecutorRoot = programExecutorLoader.load();
            ProgramExecutorController programExecutorController = programExecutorLoader.getController();

            programChooserController.setProgramExecutorController(programExecutorController);

            HBox root = new HBox(programListRoot, programExecutorRoot);
            root.setSpacing(10);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Interpreter GUI");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
