package SampleCodes;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class viewAttendanceSampleCode implements Initializable{

    @FXML
    private FlowPane main;

    @FXML
    private JFXTreeTableView<User> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<User> users = FXCollections.observableArrayList();
        users.add(new User("Femi", "23", "Executive"));
        users.add(new User("Sam", "20", "Management"));
        users.add(new User("Funmi", "21", "HR"));
        users.add(new User("Oteez", "24", "Oteex"));
        users.add(new User("Imef", "22", "CEO"));
        users.add(new User("Femi", "23", "CEO"));
        users.add(new User("Femi", "23", "Executive"));

        JFXTreeTableColumn<User, String> department = new JFXTreeTableColumn<>("Department");
        department.setPrefWidth(150);
        department.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param) {
                return param.getValue().getValue().department;
            }
        });

        JFXTreeTableColumn<User, String> age = new JFXTreeTableColumn<>("Age");
        age.setPrefWidth(150);
        age.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param) {
                return param.getValue().getValue().age;
            }
        });
        JFXTreeTableColumn<User, String> nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setPrefWidth(150);
        nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param) {
                return param.getValue().getValue().userName;
            }
        });


//        final TreeItem<User> root = new RecursiveTreeItem<User>(users, RecursiveTreeObject::getChildren);
        final TreeItem<User> root = new TreeItem<User>(new User("2016", "", ""));
        users.stream().forEach((user) -> {
            root.getChildren().add(new TreeItem<>(user));
        });
        root.setExpanded(true);

        treeView.setRoot(root);
        treeView.setShowRoot(true);
        treeView.getColumns().setAll(nameCol, department, age);

    }
    class User extends RecursiveTreeObject<User> {
        StringProperty userName;
        StringProperty age;
        StringProperty department;

        public User(String userName, String age, String department){
            this.userName = new SimpleStringProperty(userName);
            this.age = new SimpleStringProperty(age);
            this.department = new SimpleStringProperty(department);


        }
    }
}
