package ui.attendance;

import com.jfoenix.controls.*;
import Util.dbConnection;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import factory.SceneFactory;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import models.Attendance;
import models.Member;
import tray.notification.NotificationType;
import ui.notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class viewAttendanceController implements Initializable {
    @FXML
    private JFXTreeTableView<Attendance> treeView;
    @FXML
    private JFXButton refresh;

    @FXML
    private FontAwesomeIconView close;

    @FXML
    private FontAwesomeIconView minimize;

    @FXML
    private FontAwesomeIconView refreshIcon;

    @FXML
    private FontAwesomeIconView deleteIcon;

    @FXML
    private FontAwesomeIconView addIcon;

    @FXML
    void refreshAction(ActionEvent event) {
        loadData();
    }

    private Connection conn;
    private ResultSet rs;

    //create dummy rootNode and a list to hold the actual root nodes
    final TreeItem<Attendance> dummyRoot = new TreeItem<>(new Attendance("dummy", "dummy", "dummy"));
    private ObservableList<TreeItem> rootNodes;
    private double dragDeltaY, dragDeltaX;
    private JFXDialogLayout content;
    private JFXDialog dialog;
    private StackPane stackPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        loadData();

        //setOnclick for the FontAwesomeIcons
        refreshIcon.setOnMouseClicked(e->loadData());
        addIcon.setOnMouseClicked(e->loadAddAttendanceDataView());
        deleteIcon.setOnMouseClicked(e-> deleteAttendanceData());
    }

    private void deleteAttendanceData() {
        Attendance attendance = treeView.getSelectionModel().getSelectedItem().getValue();
        if (attendance.getWeek().contains("Week")) {
            int selectedData = Integer.parseInt(attendance.getData());
            int selectedDataParent = Integer.parseInt(treeView.getSelectionModel().getSelectedItem().getParent().getValue().getData());

            //delete selected Attendance from table
            String sqlQuery = "delete from weekly_attendance where id = ? and week = ?";
            try {
                Connection conn = dbConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(sqlQuery);
                pr.setString(1, attendance.getID());
                pr.setString(2, attendance.getWeek());
                pr.execute();
                pr.close();
                loadData();

                if ((selectedDataParent - selectedData) == 0 ){
                    String sqlQuery1 = "delete from monthly_attendance where id = ?";
                    pr = conn.prepareStatement(sqlQuery1);
                    pr.setString(1,attendance.getID());
                    pr.execute();
                    pr.close();
                    conn.close();
                    loadData();
                }else {
                    String sqlQuery1 = "UPDATE monthly_attendance SET year = year, month = month," +
                            " data = data - '" + selectedData + "' where id = '"+attendance.getID()+"'" ;
                    pr = conn.prepareStatement(sqlQuery1);
                    pr.execute();
                    pr.close();
                    conn.close();
                    loadData();

                    Notification.customNotify("Success", "Data successfully deleted",
                            NotificationType.NOTICE, getClass().getResource("/ui/Pics/garbage.png").toExternalForm());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void initColumns() {
        JFXTreeTableColumn<Attendance, String> week = new JFXTreeTableColumn<>("Week");
        week.setPrefWidth(225);
        week.setStyle("-fx-alignment: center;");
        week.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Attendance, String> param) {
                return param.getValue().getValue().weekProperty();
            }
        });

        JFXTreeTableColumn<Attendance, String> data = new JFXTreeTableColumn<>("Data");
        data.setPrefWidth(225);
        data.setStyle("-fx-alignment: center;");
        data.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Attendance, String> param) {
                return param.getValue().getValue().dataProperty();
            }
        });

        treeView.getColumns().setAll(week, data);
    }

    private void loadData() {
        //get rootNodes to fill the table
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT * FROM monthly_attendance";
            rs = conn.createStatement().executeQuery(query);
            rootNodes = FXCollections.observableArrayList();
            rootNodes.clear();
            dummyRoot.getChildren().clear();

            while (rs.next()) {
                String id = rs.getString(1);
                int year = rs.getInt(2);
                String month = rs.getString(3);
                int data = rs.getInt(4);
                final TreeItem<Attendance> root = new TreeItem<>(new Attendance(
                        id, month + " " + year, data + ""));



                String query1 = "SELECT * FROM weekly_attendance where id = '" + id + "'";
                ResultSet rs = conn.createStatement().executeQuery(query1);
                //A list to contain data for the obtained fields
                ObservableList<Attendance> weeklyData = FXCollections.observableArrayList();
                while (rs.next()){
                    weeklyData.add(new Attendance(rs.getString(1), rs.getString(2), rs.getInt(3)+""));
                }

                weeklyData.stream().forEach((attendance) -> {
                    root.getChildren().add(new TreeItem<>(attendance));
                });

                rootNodes.add(root);
            }
            rs.close();
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add created root nodes to dummy rootNode
        rootNodes.stream().forEach(treeItem -> {
            dummyRoot.getChildren().add(treeItem);
        });
        if(!dummyRoot.getChildren().isEmpty()) {
            dummyRoot.getChildren().get(0).setExpanded(true);
        }
        dummyRoot.setExpanded(true);


        treeView.setRoot(dummyRoot);
        treeView.setShowRoot(false);

    }

    @FXML
    void addData(ActionEvent event) {

        loadAddAttendanceDataView();
    }

    private void loadAddAttendanceDataView() {
        SceneFactory sceneFactory = SceneFactory.getInstance();
        if (sceneFactory.stageOpened("Add Attendance Data")) {
            sceneFactory.getStage("Add Attendance Data").show();

        } else {
            loadWindow("/ui/attendance/add_attendance.fxml", "Add Attendance Data");

        }
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        deleteAttendanceData();
    }

    @FXML
    void closeAction(MouseEvent event) {
        Stage stage = (Stage)close.getScene().getWindow();
        stage.close();
    }
    @FXML
    void minimizeAction(MouseEvent event){
        Stage stage = (Stage)close.getScene().getWindow();
        stage.setIconified(true);
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

}
