package factory;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SceneFactory {

    private static SceneFactory sceneFactory = null;
    public static ObservableList<Stage> stages;

    private SceneFactory() {
        setUpSceneFactory();
    }

    private void setUpSceneFactory() {
        stages = FXCollections.observableArrayList(
                stage -> new Observable[]{
                        stage.focusedProperty()
                });
        Bindings.bindContent(stages, StageHelper.getStages());
    }


    public static SceneFactory getInstance() {
        if (sceneFactory == null) {
            sceneFactory = new SceneFactory();
        }
        return sceneFactory;
    }

    public static boolean stageOpened(String stageTitle){
        for (Stage stage : stages) {

            if (stage.getTitle() == stageTitle) {
                return true;
            }
        }
        return false;
    }

    public static Stage getStage(String stageTitle){
        for (Stage stage: stages){
            if (stage.getTitle() == stageTitle)
                return stage;
        }
        return null;
    }

    public static ObservableList<Stage> getStages() {
        //return currently open stages
        return stages;
    }
}
