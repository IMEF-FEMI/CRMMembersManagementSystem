package ui.statistics;

import Util.dbConnection;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import factory.SceneFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Member;
import ui.viewMember.ViewMemberController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShowStatisticsController implements Initializable {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private MaterialDesignIconView info;

    @FXML
    private MaterialDesignIconView home;

    @FXML
    private MaterialDesignIconView iconClose;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXToggleButton statistics_toggle;

    @FXML
    private AnchorPane chartArea;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label caption;

    @FXML
    private ChoiceBox<String> select_year;
    private Connection conn;
    private ObservableList<Member> membersData;
    private ResultSet rs;
    HamburgerBackArrowBasicTransition burgerTask2;
    private double dragDeltaX, dragDeltaY;



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
    private void initDrawer() {
        drawer.toFront();
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
    void minimizeAction(MouseEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDrawer();
        showMemberStatistics();

        statistics_toggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                showMemberStatistics();
            } else {
                showAttendanceStatistics(select_year.getValue() == null ? "2017" :
                        select_year.getValue());
            }
        });

        select_year.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                showAttendanceStatistics(newValue);
            System.out.println(newValue);
        });
    }

    private void showAttendanceStatistics(String year) {
        chartArea.getChildren().clear();
        caption.setText("");
        select_year.getItems().clear();

        try {
            conn = dbConnection.getConnection();
            String query = "SELECT year FROM monthly_attendance";
            rs = conn.createStatement().executeQuery(query);
            select_year.getItems().clear();

            while (rs.next()) {
                if (!select_year.getItems().contains(rs.getString(1)))
                    select_year.getItems().add(rs.getString(1));
            }
            rs.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            conn = dbConnection.getConnection();
            String query = "SELECT * FROM monthly_attendance where year = '" + year + "'";
            rs = conn.createStatement().executeQuery(query);

            //set up chart
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> bc =
                    new BarChart<>(xAxis, yAxis);
            bc.setPrefWidth(998);
            bc.setTitle("Attendance");
            xAxis.setLabel("Month");
            yAxis.setLabel("Attendance");
            XYChart.Series series1 = new XYChart.Series();

            while (rs.next()) {
                series1.getData().add(new XYChart.Data(rs.getString(3), Integer.parseInt(rs.getString(4))));
            }

            bc.getData().clear();
            bc.getData().setAll(series1);

            rs.close();
            conn.close();

            chartArea.getChildren().add(bc);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMemberStatistics() {
        int children_teenagers_count = 0;
        int singles_count = 0;
        int married_count = 0;
        //get chart data
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT * FROM members";
            membersData = FXCollections.observableArrayList();
            rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                membersData.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
            }
            rs.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Member member : membersData) {

            if (member.getMarital_status().equals("CHILDREN / TEENAGERS"))
                children_teenagers_count += 1;
            else if (member.getMarital_status().equals("SINGLE"))
                singles_count += 1;
            else if (member.getMarital_status().equals("MARRIED"))
                married_count += 1;
        }

        int total = children_teenagers_count + singles_count + married_count;
        //calculate percentage
        int children_teenagers_percentage = 0;
        int singles_percentage = 0;
        int married_percentage = 0;
        if (!(total == 0)) {
             children_teenagers_percentage = ((children_teenagers_count * 100) / total);
             singles_percentage = ((singles_count * 100) / total);
             married_percentage = ((married_count * 100) / total);
        }

        caption.setText("CHILDREN: " + children_teenagers_count + " SINGLES: " + singles_count + " MARRIED: " + married_count
                + " TOTAL: " + total + "\n"
                + "CHILDREN: " + children_teenagers_percentage + "% SINGLES: " + singles_percentage + "% MARRIED: " + married_percentage + "%");
        chartArea.getChildren().clear();
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("CHILDREN", children_teenagers_count),
                        new PieChart.Data("SINGLES", singles_count),
                        new PieChart.Data("MARRIED", married_count));

        pieChart.getData().setAll(pieChartData);
        pieChart.setLayoutX(180);
        pieChart.setLayoutY(15);
        pieChart.setTitle("Member Statistics");
        pieChart.setLabelLineLength(10);
        pieChart.setLegendSide(Side.LEFT);
        chartArea.getChildren().add(pieChart);


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
