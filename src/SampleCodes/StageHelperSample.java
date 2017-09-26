package SampleCodes;/**
 * Created by CRM on 1/4/1980.
 */

import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StageHelperSample extends Application {

    int stageCount = 1;

    ObservableList<Stage> stages;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        stages = FXCollections.observableArrayList(
                stage -> new Observable[]{
                        stage.focusedProperty()
                });
        Bindings.bindContent(stages, StageHelper.getStages());
        Label label = new Label();

        stages.addListener((ListChangeListener.Change<? extends Stage> c) -> {
            label.setText(
                    stages
                            .stream()
                            .filter(Stage::isFocused)
                            .findAny()
                            .map(Stage::getTitle)
                            .orElse(""));
        });

        Button newStageButton = new Button("New Window");
        newStageButton.setOnAction(event -> {
            newStage();


        });

        Scene scene = new Scene(new BorderPane(new StackPane(newStageButton), label, null,
                null, null), 300, 180);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Primary");
        primaryStage.show();

    }

    void newStage() {
        Stage stage = new Stage();
        stage.setTitle("Window " + (++stageCount));
        Button close = new Button("Close");
        close.setOnAction(e -> {
            stage.hide();
            for (Stage stage1 : stages) {

                System.out.println(stage1.getTitle());
            }
        });

        stage.setScene(new Scene(new StackPane(close), 300, 80));
        stage.show();
    }
}
