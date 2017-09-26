package ui.viewMember;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by CRM on 1/4/1980.
 */
public class drawerContentController implements Initializable {
    @FXML
    private JFXButton home;

    @FXML
    private JFXButton birthdays;

    @FXML
    private JFXButton viewNumbers;

    @FXML
    private JFXButton viewMembers;

    @FXML
    private JFXButton statistics;

    @FXML
    private JFXButton exit;
    private double dragDeltaX, dragDeltaY;

    @FXML
    void statisticsAction(ActionEvent event) {

        loadWindow("/ui/statistics/show_statistics.fxml", statistics);
    }

    @FXML
    void birthdayAction(ActionEvent event) {
        loadWindow("/ui/viewMember/view_birthdays.fxml", birthdays);
    }

    @FXML
    void exitActon(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void homeAction(ActionEvent event) {
        // home scene
        loadWindow("/ui/main/main.fxml", home);


    }

    @FXML
    void viewMembers(ActionEvent event) {
        loadWindow("/ui/viewMember/view_member.fxml", viewMembers);
    }

    @FXML
    void viewNumberAction(ActionEvent event) {

        loadWindow("/ui/viewMember/view_numbers.fxml", viewNumbers);
    }


    private void loadWindow(String location, Node node) {
        Parent parent;
        try {
            parent = FXMLLoader.load(getClass().getResource(location));
            parent.setOnMousePressed(e1 -> {
                parent.setCursor(Cursor.CLOSED_HAND);
                dragDeltaX = parent.getScene().getWindow().getX() - e1.getScreenX();
                dragDeltaY = parent.getScene().getWindow().getY() - e1.getScreenY();
            });

            parent.setOnMouseDragged(e1 -> {
                parent.getScene().getWindow().setX(e1.getScreenX() + dragDeltaX);
                parent.getScene().getWindow().setY(e1.getScreenY() + dragDeltaY);
            });

            parent.setOnMouseReleased(e1 -> {
                parent.setCursor(Cursor.DEFAULT);
            });
            node.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
