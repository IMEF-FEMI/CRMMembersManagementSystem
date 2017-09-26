package ui.addMember;

import com.jfoenix.controls.*;
import Util.dbConnection;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import tray.notification.NotificationType;
import ui.notification.Notification;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class addMemberController implements Initializable {

    @FXML
    private StackPane root_pane;

    @FXML
    private JFXTextField fName;

    @FXML
    private JFXTextField lName;

    @FXML
    private JFXTextField phone_number;

    @FXML
    private JFXComboBox<Sex> sexCombo;

    @FXML
    private VBox address;

    @FXML
    private JFXComboBox<String> locationCombo;

    @FXML
    private JFXDatePicker birthday;

    @FXML
    private JFXRadioButton children_teenagers_radio;

    @FXML
    private JFXRadioButton married_radio;

    @FXML
    private JFXRadioButton existing_yes;

    @FXML
    private JFXRadioButton existing_no;

    @FXML
    private JFXCheckBox dept_nil;

    @FXML
    private JFXRadioButton position_nil;

    @FXML
    private JFXRadioButton single_radio;

    @FXML
    private GridPane department_grid;

    @FXML
    private JFXCheckBox choir;

    @FXML
    private JFXCheckBox usher;

    @FXML
    private JFXCheckBox drama;

    @FXML
    private JFXCheckBox security;

    @FXML
    private JFXCheckBox sanctuary_keepers;

    @FXML
    private JFXCheckBox children_teacher;

    @FXML
    private JFXRadioButton pastor;

    @FXML
    private JFXRadioButton deacon;

    @FXML
    private JFXRadioButton deaconess;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private MaterialDesignIconView iconClose;

    @FXML
    private JFXTextField member_address;


    ArrayList<String> dept = new ArrayList<>();

    //fields to save member data
    String location = null;
    String marital_status = null;
    private String weddingAnniversary = null;
    private PopOver weddingAnniversaryPopover;

    String position_str = null;
    //DIALOG BUTTONS
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton cancel;
    private JFXButton okay;
    //toggle groups
    private ToggleGroup maritalStatus;
    private ToggleGroup existingMembers;
    private ToggleGroup position;


    @FXML
    void addMember(ActionEvent event) {
        // add new member

        content = new JFXDialogLayout();
        dialog = new JFXDialog(root_pane, content, JFXDialog.DialogTransition.CENTER);

        okay = new JFXButton("Okay");
        cancel = new JFXButton("Cancel");

        content.setHeading(new Text("Confirm Save Operation"));
        content.setBody(new Text("Are you sure you want to add Member ?"));
        okay.setOnAction(event1 -> {
            dialog.close();

            boolean flag = fName.getText().isEmpty() || lName.getText().isEmpty()
                    || phone_number.getText().isEmpty() || sexCombo.getSelectionModel().isEmpty()
                    || member_address.getText().isEmpty() || locationCombo.getSelectionModel().isEmpty()
                    || birthday.getValue().toString().isEmpty() || !(maritalStatus.getSelectedToggle().isSelected());

            if (flag) {
                //fields not filled
                //Tray Notification
                Notification.customNotify("Error", "Please Enter All Fields", NotificationType.WARNING, getClass().getResource("/ui/Pics/error.png").toExternalForm());
            } else {
                String sqlInsert = "INSERT INTO members(id, fName, lName, phone_number, sex, address," +
                        " location, birthday, marital_status, department, position, wedding_anniversary) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
                try {
                    Connection conn = dbConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                    stmt.setString(1, fName.getText().toLowerCase() + lName.getText().toLowerCase() + phone_number.getText());
                    stmt.setString(2, fName.getText());
                    stmt.setString(3, lName.getText());
                    stmt.setString(4, phone_number.getText());
                    stmt.setString(5, sexCombo.getSelectionModel().getSelectedItem().value());
                    stmt.setString(6, member_address.getText());
                    stmt.setString(7, locationCombo.getSelectionModel().getSelectedItem());
                    stmt.setString(8, birthday.getValue().toString());
                    if (married_radio.isSelected()) {
                        stmt.setString(9, "MARRIED");
                    } else if (single_radio.isSelected()) {
                        stmt.setString(9, "SINGLE");
                    } else if (children_teenagers_radio.isSelected()) {
                        stmt.setString(9, "CHILDREN / TEENAGERS");
                    }

                    if (existing_no.isSelected()) {
                        stmt.setString(10, "--");
                        stmt.setString(11, "--");
                    } else {
                        //get/concat all strings from the dept array
                        String s = "";
                        for (String str : dept) {
                            s += str + ", ";

                        }

                        //use concat string as entry
                        stmt.setString(10, s.isEmpty() ? "--" : s);
                        stmt.setString(11, position.getSelectedToggle() == null ? "--" : position_str);
                    }
                    System.out.println("wedding: "+weddingAnniversary);
                    stmt.setString(12, weddingAnniversary == null ? "--" : weddingAnniversary);

                    //close
                    stmt.execute();
                    conn.close();

                    //Tray notification
                    Notification.notify("Success", "Member Successfully Added", NotificationType.SUCCESS);
                    //hide marriage anniversary popover
                    weddingAnniversaryPopover.hide();

                    Platform.runLater(() -> clearFields());
                } catch (SQLException e) {
                    //Tray Notification
                    Notification.notify("Error", "Member was not added Please check details", NotificationType.ERROR);
                    e.printStackTrace();
                }
            }

        });
        cancel.setOnAction(event1 -> {
            dialog.close();


        });
        content.setActions(okay, cancel);
        dialog.show();

    }

    private void clearFields() {
        fName.setText("");
        lName.setText("");
        phone_number.setText("");
        sexCombo.setValue(null);
        member_address.setText("");
        locationCombo.setValue(null);
        birthday.setValue(null);
        maritalStatus.selectToggle(null);
        existing_no.setSelected(true);

        for (Node node : department_grid.getChildren()) {
            // uncheck and disable all selected department
            ((JFXCheckBox) node).setSelected(false);
            node.setDisable(true);
        }

        for (Toggle node : position.getToggles()) {
            // uncheck and disable elected position
            (node).setSelected(false);
            ((JFXRadioButton) node).setDisable(true);

        }

        dept.clear();
    }


    @FXML
    void cancelSave(ActionEvent event) {
        Stage stage = (Stage) iconClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    void disableOtherInfo(ActionEvent event) {
        //disable other infos
        for (Node checkbox : department_grid.getChildren()) {
            ((JFXCheckBox) checkbox).setDisable(true);
        }

        pastor.setDisable(true);
        deacon.setDisable(true);
        deaconess.setDisable(true);
        position_nil.setDisable(true);

    }

    @FXML
    void enableOtherInfo(ActionEvent event) {
//disable other infos
        for (Node checkbox : department_grid.getChildren()) {
            ((JFXCheckBox) checkbox).setDisable(false);
        }
        pastor.setDisable(false);
        deacon.setDisable(false);
        deaconess.setDisable(false);
        position_nil.setDisable(false);

    }

    @FXML
    void hideStage(MouseEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void minimizeAction(MouseEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        new Thread(() -> {
            initFields();
            initToggleGroups();
        }).start();
    }

    private void initFields() {
        //main
        fName.setEditable(true);
        lName.setEditable(true);
        phone_number.setEditable(true);
        member_address.setEditable(true);
        sexCombo.setItems(FXCollections.observableArrayList(Sex.values()));

        //set exxisting member default as no
        existing_no.setSelected(true);


        //fill the location combobox
        String sql = "SELECT * FROM LOCATION";
        Connection conn = dbConnection.getConnection();
        ResultSet rs = null;
        PreparedStatement pr = null;
        ObservableList<String> location = FXCollections.<String>observableArrayList();
        try {

            pr = conn.prepareStatement(sql);
            rs = pr.executeQuery();

            while (rs.next()) {
                location.add(rs.getString("location_name"));
            }

            locationCombo.setItems(location);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //close connection

                rs.close();
                pr.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //other
        for (Node checkbox : department_grid.getChildren()) {
            ((JFXCheckBox) checkbox).setDisable(true);
        }

        pastor.setDisable(true);
        deacon.setDisable(true);
        deaconess.setDisable(true);
        position_nil.setDisable(true);
    }

    public void initToggleGroups() {
        //group all radio buttons and checkboxes
        maritalStatus = new ToggleGroup();
        maritalStatus.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            //get selected radio button
            System.out.println("oldvalue: " + oldValue);
            System.out.println("newvalue: " + newValue);
            System.out.println("observable: " + observable);

            if (newValue == null || observable == null) {
                System.out.println("null encountered");
                return;
            } else {
                RadioButton btn = (RadioButton) newValue.getToggleGroup().getSelectedToggle();
                marital_status = btn.getText().replace('_', ' ');
                System.out.println("Marital Status: " + marital_status);
            }
        });

        existingMembers = new ToggleGroup();

        position = new ToggleGroup();
        position.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            //get selected radio button
            RadioButton btn = (RadioButton) newValue.getToggleGroup().getSelectedToggle();
            position_str = btn.getText();
        });

        children_teenagers_radio.setToggleGroup(maritalStatus);
        children_teenagers_radio.setOnAction( e-> weddingAnniversaryPopover.hide());
        single_radio.setToggleGroup(maritalStatus);
        single_radio.setOnAction(e-> {
            if (weddingAnniversaryPopover != null)
                weddingAnniversaryPopover.hide();
        });
        married_radio.setToggleGroup(maritalStatus);
        married_radio.setOnAction(event -> {
            DatePicker datePicker = new JFXDatePicker();
            datePicker.setOnAction(event1 -> {
                weddingAnniversary = datePicker.getValue().toString();
            });

            weddingAnniversaryPopover = new PopOver(datePicker);
            weddingAnniversaryPopover.setPrefHeight(200);
            weddingAnniversaryPopover.setWidth(100);
            weddingAnniversaryPopover.setTitle("  Select Anniversary Date");
            weddingAnniversaryPopover.setDetached(true);
            weddingAnniversaryPopover.show(married_radio);
        });

        existing_yes.setToggleGroup(existingMembers);
        existing_no.setToggleGroup(existingMembers);

        pastor.setToggleGroup(position);
        deacon.setToggleGroup(position);
        deaconess.setToggleGroup(position);
        position_nil.setToggleGroup(position);

        for (Node checkBox : department_grid.getChildren()) {
            checkBox.setOnMouseClicked(event -> {

                //selected checkbox
                String str = ((JFXCheckBox) checkBox).textProperty().get();

                //check if selected checkbox is already in list
                if (!dept.contains(str)) {

                    dept.add(str);
                    System.out.println(dept);
                } else {

                    dept.remove(str);
                    System.out.println(dept);
                    str = null;
                }

                //when used in database, loop through the data in observable list or in db directly
            });


        }
    }
}
