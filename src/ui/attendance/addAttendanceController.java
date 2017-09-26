package ui.attendance;

import Util.dbConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tray.notification.NotificationType;
import ui.notification.Notification;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class addAttendanceController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField data;

    @FXML
    private JFXDatePicker date;

    @FXML
    private JFXComboBox<String> weekCombo;

    @FXML
    private JFXButton save;

    @FXML
    private JFXButton clear;

    @FXML
    private JFXButton done;

    @FXML
    void closeStage(ActionEvent event) {
        Stage stage = (Stage) done.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClear(ActionEvent event) {
        data.clear();
        date.setValue(null);
        weekCombo.setValue("");
    }

    @FXML
    void onSave(ActionEvent event) {

        int year = date.getValue().getYear();
        String month = (date.getValue().getMonth().toString());
        String id = month + year;
        int attData = Integer.parseInt(data.getText());


        try {
            Connection conn = dbConnection.getConnection();
            //query the db first if such record exist

            String sql = "SELECT * FROM monthly_attendance where id = '" + id + "'";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            if (rs.next()) {
                rs.close();
                //first check if week exists
                String sqlcheckWeek = "SELECT * FROM weekly_attendance where id = '" + id + "' and week = '"+weekCombo.getValue()+"'";
                rs = conn.createStatement().executeQuery(sqlcheckWeek);

                if (rs.next()){
                    //record exists
                    //Tray notification
                    Notification.notify("Error", "Record Exists", NotificationType.ERROR);
                    rs.close();
                }else {
                    rs.close();
                    //update monthly_attendance first
                    String sqlUpdate = "UPDATE monthly_attendance SET year = '" + year +
                            "', month = '"+month+"', data = data + '"+attData+"' where id = '"+id+"'";
                    ;

                    conn.createStatement().execute(sqlUpdate);

                    //insert into weekly_attendance
                    String sqlInsert = "INSERT INTO weekly_attendance(id, week, data) VALUES(?,?,?)";
                    PreparedStatement stmt = conn.prepareStatement(sqlInsert);

                    stmt.setString(1, id);
                    stmt.setString(2, weekCombo.getValue());
                    stmt.setInt(3, attData);

                    //close all
                    stmt.execute();
                    stmt.close();
                    conn.close();

                    //Tray notification
                    Notification.notify("Success", "Data Added", NotificationType.SUCCESS);
                    closeStage(event);
                }

            }else {


                String sqlInsert = "INSERT INTO monthly_attendance(id, year, month, data) VALUES(?,?,?,?)";
                String sqlInsert1 = "INSERT INTO weekly_attendance(id, week, data) VALUES(?,?,?)";

                //first execute for monthly
                PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                stmt.setString(1, id);
                stmt.setInt(2, year);
                stmt.setString(3, month);
                stmt.setInt(4, attData);
                stmt.execute();

                //execute for weekly
                stmt = conn.prepareStatement(sqlInsert1);
                stmt.setString(1, id);
                stmt.setString(2, weekCombo.getValue());
                stmt.setInt(3, attData);
                stmt.execute();

                //close
                stmt.close();
                conn.close();

                //Tray notification
                Notification.notify("Success", "Data Added", NotificationType.SUCCESS);
                closeStage(event);
            }

        } catch (SQLException e) {

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        weekCombo.setItems(FXCollections.observableArrayList("Week 1", "Week 2", "Week 3", "Week 4"));
    }
}
