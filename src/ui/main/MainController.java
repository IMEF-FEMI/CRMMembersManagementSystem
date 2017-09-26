package ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.sun.javafx.stage.StageHelper;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import factory.SceneFactory;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ui.main.CRMMembersManagementSystem;
import ui.viewMember.ViewMemberController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {


    @FXML
    private HBox menusHolder;

    @FXML
    public JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private AnchorPane ADDMEMBER;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private AnchorPane VIEWMEMBERS;

    @FXML
    private Group groupTrains;

    @FXML
    private AnchorPane STATISTICS;

    @FXML
    private Group groupTrains1;

    @FXML
    private HBox infoOverlayHolder;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private ImageView logoImageView;

    @FXML
    private MaterialDesignIconView iconClose;

    @FXML
    private AnchorPane anchorPane;


    @FXML
    private JFXButton attendance;

    private double dragDeltaX, dragDeltaY;
    HamburgerBackArrowBasicTransition burgerTask2;

    private void setUpRipples() {
        JFXRippler fXRippler = new JFXRippler(ADDMEMBER, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        JFXRippler fXRippler1 = new JFXRippler(VIEWMEMBERS, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        JFXRippler fXRippler2 = new JFXRippler(STATISTICS, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        menusHolder.getChildren().addAll(fXRippler, fXRippler1, fXRippler2);
        menusHolder.setSpacing(30);
        menusHolder.setAlignment(Pos.CENTER);

    }

    @FXML
    void addNewMember(MouseEvent event) {
        loadWindow("/ui/addMember/addMember.fxml", "Add New Member");

    }

    @FXML
    void openAttendance(ActionEvent event) {

        SceneFactory sceneFactory = SceneFactory.getInstance();
        if (sceneFactory.stageOpened("View Attendance")) {
            sceneFactory.getStage("View Attendance").show();

        } else {
            loadWindow("/ui/attendance/view_attendance.fxml", "View Attendance");

        }
    }

    @FXML
    void viewMembers(MouseEvent event) {

            changeScene("/ui/viewMember/view_member.fxml", VIEWMEMBERS);

    }

    @FXML
    private void hideStage(MouseEvent event) {
        Stage stage = (Stage) iconClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    void showAnalysis(MouseEvent event) {
        changeScene("/ui/statistics/show_statistics.fxml", STATISTICS);
    }

    @FXML
    void minimizeAction(MouseEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (!CRMMembersManagementSystem.isSplashLoaded) {
            loadSplashScreen();
        }

        setUpRipples();

        drawer.setDisable(true);

        VBox vBox = null;

        try {
        //init drawer
            vBox = FXMLLoader.load(getClass().getResource("/ui/viewMember/drawercontent.fxml"));

            for (Node j : vBox.getChildren()) {

                j.setOnMouseClicked(event -> {

                            closeDrawerAction();

                        }

                );
            }

            drawer.setSidePane(vBox);


        } catch (IOException e) {
            e.printStackTrace();
        }


        burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
        burgerTask2.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();

            if (drawer.isShown()) {
                drawer.close();
            } else {
                drawer.open();
                drawer.setDisable(false);
            }

        });

    }

    @FXML
    void closeDrawerAction() {
        if (drawer.isShown()) {
            drawer.close();
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();
            drawer.setDisable(false);
        }
    }
    @FXML
    void loadInfo(MouseEvent event) {

        try {
            //dim overlay on new stage opening
            Region veil = new Region();
            veil.setPrefSize(1100, 650);
            veil.setStyle("-fx-background-color:rgba(0,0,0,0.4)");
            Stage newStage = new Stage();
            Parent parent = FXMLLoader.load(getClass().getResource("/ui/about/About.fxml"));

            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            newStage.setScene(scene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.getScene().getRoot().setEffect(new DropShadow());
            newStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    anchorPane.getChildren().add(veil);
                } else if (anchorPane.getChildren().contains(veil)) {
                    anchorPane.getChildren().removeAll(veil);
                }

            });
            newStage.centerOnScreen();
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ViewMemberController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    void loadSplashScreen() {
        try {
            CRMMembersManagementSystem.isSplashLoaded = true;
            StackPane pane = FXMLLoader.load(getClass().getResource("splashFXML.fxml"));
            anchorPane.setStyle("-fx-background-color: #ffffff;");
            anchorPane.getChildren().setAll(pane);


            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), pane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), pane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeIn.play();
            fadeIn.setOnFinished(event -> {
                fadeOut.play();
            });

            fadeOut.setOnFinished(e -> {

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                    anchorPane.getChildren().setAll(root);
                    anchorPane.getScene().getWindow().setWidth(997);
                    anchorPane.getScene().getWindow().setHeight(584);
                    anchorPane.getScene().getWindow().centerOnScreen();

                    root.setOnMousePressed(e1 -> {
                        root.setCursor(Cursor.CLOSED_HAND);
                        dragDeltaX = anchorPane.getScene().getWindow().getX() - e1.getScreenX();
                        dragDeltaY = anchorPane.getScene().getWindow().getY() - e1.getScreenY();
                    });

                    root.setOnMouseDragged(e1 -> {
                        anchorPane.getScene().getWindow().setX(e1.getScreenX() + dragDeltaX);
                        anchorPane.getScene().getWindow().setY(e1.getScreenY() + dragDeltaY);
                    });

                    root.setOnMouseReleased(e1 -> {
                        root.setCursor(Cursor.DEFAULT);
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    void loadWindow(String location, String title) {
        try {

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/ui/Pics/LOGO.jpg")));

            Parent root = FXMLLoader.load(getClass().getResource(location));
            root.setOnMousePressed(event -> {
                root.setCursor(Cursor.CLOSED_HAND);
                dragDeltaX = stage.getX() - event.getScreenX();
                dragDeltaY = stage.getY() - event.getScreenY();
            });

            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() + dragDeltaX);
                stage.setY(event.getScreenY() + dragDeltaY);
            });

            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void changeScene(String location, Node node){
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
}
