package ui.viewMember;

import Util.dbConnection;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import factory.SceneFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.BirthDayAnniversary;
import models.Number;
import tray.notification.NotificationType;
import ui.notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by CRM on 1/4/1980.
 */
public class ViewBirthdayController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private MaterialDesignIconView iconClose;

    @FXML
    private MaterialDesignIconView home;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private JFXButton copy;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private ChoiceBox<String> select_month;

    @FXML
    private JFXTreeTableView<BirthDayAnniversary> birthdayTreeTableView;

    @FXML
    private JFXTreeTableView<BirthDayAnniversary> anniversaryTreeTableView;
    HamburgerBackArrowBasicTransition burgerTask2;

    private ObservableList<BirthDayAnniversary> birthdayData;
    private ObservableList<BirthDayAnniversary> anniversaryData;
    final TreeItem<BirthDayAnniversary> birthdayRoot = new TreeItem<>(new BirthDayAnniversary("", "", "", "", ""));
    final TreeItem<BirthDayAnniversary> anniversaryRoot = new TreeItem<>(new BirthDayAnniversary("", "", "", "", ""));
    private Connection conn;
    private ResultSet rs;
    private ObservableList<BirthDayAnniversary> birthdayMonthData;
    private ObservableList<BirthDayAnniversary> anniversaryMonthData;

    private double dragDeltaY, dragDeltaX;


    @FXML
    void copyAction(ActionEvent event) {
        closeDrawerAction();
        String contentStr = "";

        contentStr+="Birthdays \n";
        //loop through the two list containing the data on focus and get the appropriate info
        for (BirthDayAnniversary data: birthdayMonthData){
            contentStr += data.getFirstName()+" "+ data.getLastName()+ ": "+
                    data.getDay()+" "+data.getMonth()+"\n";
        }

        contentStr+="\nWedding Anniversary \n";
        for (BirthDayAnniversary data: anniversaryMonthData){
            contentStr += data.getFirstName()+" "+ data.getLastName()+ ": "+
                    data.getDay()+" "+data.getMonth()+"\n";
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(contentStr);
        clipboard.setContent(content);
        Notification.notify("Success", "Information Copied", NotificationType.SUCCESS);
    }

    @FXML
    void hideStage(MouseEvent event) {
        Stage stage = (Stage) iconClose.getScene().getWindow();
        stage.close();
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
                    rootPane.getChildren().add(veil);
                } else if (rootPane.getChildren().contains(veil)) {
                    rootPane.getChildren().removeAll(veil);
                }

            });
            newStage.centerOnScreen();
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ViewMemberController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    @FXML
    void loadMain(MouseEvent event) {

        Parent parent;
        try {
            parent = FXMLLoader.load(getClass().getResource("/ui/main/main.fxml"));
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
            rootPane.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void minimizeAction(MouseEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void refreshAction(ActionEvent event) {
        closeDrawerAction();
        loadData();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDrawer();
        initChoiceBox();
        //load data
        initColumns();
        loadData();
    }

    private void initDrawer() {
        drawer.toFront();
        drawer.setDisable(true);

        VBox vBox = null;
        try {
//init drawer
            vBox = FXMLLoader.load(getClass().getResource("drawercontent.fxml"));

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
    private void initChoiceBox() {
        ObservableList<String> list = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH",
                "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
        select_month.setItems(list);
        select_month.setValue(list.get(0));

        select_month.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    //loop through both the main birthday and anniversary list to
                    //get the birthdays and wedding anniversary with the selected month
                    birthdayMonthData.clear();
                    birthdayRoot.getChildren().clear();
                    birthdayData.stream().forEach(birthDayAnniversary -> {
                        if (birthDayAnniversary.getMonth() == list.get(newValue.intValue()))
                            birthdayMonthData.add(birthDayAnniversary);
                    });
                    birthdayMonthData.stream().forEach(birthDayAnniversary -> {

                        birthdayRoot.getChildren().add(new TreeItem<>(birthDayAnniversary));
                    });


                    anniversaryMonthData.clear();
                    anniversaryRoot.getChildren().clear();
                    anniversaryData.stream().forEach(anniversary -> {
                        if (anniversary.getMonth() == list.get(newValue.intValue()))
                            anniversaryMonthData.add(anniversary);
                    });

                    anniversaryMonthData.stream().forEach(birthDayAnniversary -> {

                        anniversaryRoot.getChildren().add(new TreeItem<>(birthDayAnniversary));
                    });
                    System.out.println(list.get(newValue.intValue()));
                });
    }

    private void loadData() {
        birthdayRoot.getChildren().clear();
        anniversaryRoot.getChildren().clear();

        //fill the birthdays table first
        //get rootNodes to fill the table
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT id, fName, lName, birthday FROM members";
            rs = conn.createStatement().executeQuery(query);
            birthdayData = FXCollections.observableArrayList();
            birthdayMonthData = FXCollections.observableArrayList();
            birthdayData.clear();


            while (rs.next()) {
                String Id = rs.getString(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String[] birthday = rs.getString(4).split("-");
                LocalDate birthDayLocal = LocalDate.of(Integer.parseInt(birthday[0]), Integer.parseInt(birthday[1]), Integer.parseInt(birthday[2]));


                birthdayData.add(new BirthDayAnniversary(
                        Id, fName, lName, birthDayLocal.getMonth().toString(),
                        String.valueOf(birthDayLocal.getDayOfMonth())));

                if (birthDayLocal.getMonth().toString() == "JANUARY")
                    birthdayMonthData.add(new BirthDayAnniversary(
                            Id, fName, lName, birthDayLocal.getMonth().toString(),
                            String.valueOf(birthDayLocal.getDayOfMonth())));
            }

            birthdayMonthData.stream().forEach(birthDayAnniversary -> {

                birthdayRoot.getChildren().add(new TreeItem<>(birthDayAnniversary));
            });

            birthdayTreeTableView.setRoot(birthdayRoot);
            birthdayRoot.setExpanded(true);
            birthdayTreeTableView.setShowRoot(false);

            rs.close();
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //fill the anniversary table
        //get rootNodes to fill the table
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT id, fName, lName, wedding_anniversary FROM members where wedding_anniversary <> ?";
            PreparedStatement pr = conn.prepareStatement(query);
            pr.setString(1, "--");
            rs = pr.executeQuery();
//            rs = conn.createStatement().executeQuery(query);
            anniversaryData = FXCollections.observableArrayList();
            anniversaryMonthData = FXCollections.observableArrayList();
            anniversaryData.clear();


            while (rs.next()) {
                String Id = rs.getString(1);
                String fName = rs.getString(2);
                String lName = rs.getString(3);
                String[] birthday = rs.getString(4).split("-");
                LocalDate birthDayLocal = LocalDate.of(Integer.parseInt(birthday[0]), Integer.parseInt(birthday[1]), Integer.parseInt(birthday[2]));


                anniversaryData.add(new BirthDayAnniversary(
                        Id, fName, lName, birthDayLocal.getMonth().toString(),
                        String.valueOf(birthDayLocal.getDayOfMonth())));

                if (birthDayLocal.getMonth().toString() == "JANUARY")
                    anniversaryMonthData.add(new BirthDayAnniversary(
                            Id, fName, lName, birthDayLocal.getMonth().toString(),
                            String.valueOf(birthDayLocal.getDayOfMonth())));
            }

            anniversaryMonthData.stream().forEach(birthDayAnniversary -> {

                anniversaryRoot.getChildren().add(new TreeItem<>(birthDayAnniversary));
            });

            anniversaryTreeTableView.setRoot(anniversaryRoot);
            anniversaryRoot.setExpanded(true);
            anniversaryTreeTableView.setShowRoot(false);

            rs.close();
            pr.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initColumns() {
        //for birthdays table
        JFXTreeTableColumn<BirthDayAnniversary, String> firstName = new JFXTreeTableColumn<>("First Name");
        firstName.setPrefWidth(141);
        firstName.setStyle("-fx-alignment: center;");
        firstName.setCellValueFactory(param -> param.getValue().getValue().firstNameProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> lastName = new JFXTreeTableColumn<>("Last Name");
        lastName.setPrefWidth(141);
        lastName.setStyle("-fx-alignment: center;");
        lastName.setCellValueFactory(param -> param.getValue().getValue().lastNameProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> month = new JFXTreeTableColumn<>("Month");
        month.setPrefWidth(141);
        month.setStyle("-fx-alignment: center;");
        month.setCellValueFactory(param -> param.getValue().getValue().monthProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> day = new JFXTreeTableColumn<>("Day");
        day.setPrefWidth(141);
        day.setStyle("-fx-alignment: center;");
        day.setCellValueFactory(param -> param.getValue().getValue().dayProperty());
        birthdayTreeTableView.getColumns().setAll(firstName, lastName, month, day);


        //for anniversary table
        JFXTreeTableColumn<BirthDayAnniversary, String> firstName1 = new JFXTreeTableColumn<>("First Name");
        firstName1.setPrefWidth(141);
        firstName1.setStyle("-fx-alignment: center;");
        firstName1.setCellValueFactory(param -> param.getValue().getValue().firstNameProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> lastName1 = new JFXTreeTableColumn<>("Last Name");
        lastName1.setPrefWidth(141);
        lastName1.setStyle("-fx-alignment: center;");
        lastName1.setCellValueFactory(param -> param.getValue().getValue().lastNameProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> month1 = new JFXTreeTableColumn<>("Month");
        month1.setPrefWidth(141);
        month1.setStyle("-fx-alignment: center;");
        month1.setCellValueFactory(param -> param.getValue().getValue().monthProperty());

        JFXTreeTableColumn<BirthDayAnniversary, String> day1 = new JFXTreeTableColumn<>("Day");
        day1.setPrefWidth(141);
        day1.setStyle("-fx-alignment: center;");
        day1.setCellValueFactory(param -> param.getValue().getValue().dayProperty());
        anniversaryTreeTableView.getColumns().setAll(firstName1, lastName1, month1, day1);


    }
    void loadWindow(String location, String title) {
        try {

            Stage stage = new Stage(StageStyle.UNDECORATED);
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
}
