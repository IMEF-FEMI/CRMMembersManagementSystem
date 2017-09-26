package ui.editMemberDetails;

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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Member;
import org.controlsfx.control.PopOver;
import tray.notification.NotificationType;
import ui.notification.Notification;
import ui.addMember.Sex;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by CRM on 9/1/2017.
 */
public class editMemberDetailsController implements Initializable {

    @FXML
    private StackPane root_pane;

    @FXML
    private AnchorPane root_anchor;

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
    private JFXTextField member_address;

    @FXML
    private JFXComboBox<String> locationCombo;

    @FXML
    private JFXDatePicker birthday;

    @FXML
    private HBox hbox_sunday_school;

    @FXML
    private JFXRadioButton children_teenagers_radio;

    @FXML
    private JFXRadioButton single_radio;

    @FXML
    private JFXRadioButton married_radio;

    @FXML
    private JFXRadioButton existing_yes;

    @FXML
    private JFXRadioButton existing_no;

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
    private JFXCheckBox dept_nil;

    @FXML
    private VBox vbox_position;

    @FXML
    private JFXRadioButton pastor;

    @FXML
    private JFXRadioButton deacon;

    @FXML
    private JFXRadioButton deaconess;

    @FXML
    private JFXRadioButton position_nil;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private MaterialDesignIconView iconClose;

    ArrayList<String> dept = new ArrayList<>();

    //fields to save member data
    String location = null;
    String marital_staus = null;
    String position_str = null;

    private String weddingAnniversary = null;

    //DIALOG BUTTONS
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton cancel;
    private JFXButton okay;
    //toggle groups
    private ToggleGroup maritalStatus;
    private ToggleGroup existingMembers;
    private ToggleGroup position;
    private Member member;

    @FXML
    void saveUpdate(ActionEvent event) {

        //save Update

        content = new JFXDialogLayout();
        dialog = new JFXDialog(root_pane, content, JFXDialog.DialogTransition.CENTER);
        okay = new JFXButton("Okay");
        cancel = new JFXButton("Cancel");

        content.setHeading(new Text("Confirm Save Operation"));
        content.setBody(new Text("Are you sure you want Save ?"));
        okay.setOnAction(event1 -> {
            dialog.close();

            boolean flag = fName.getText().isEmpty() || lName.getText().isEmpty()
                    || phone_number.getText().isEmpty() || sexCombo.getSelectionModel().isEmpty()
                    || member_address.getText().isEmpty() || locationCombo.getSelectionModel().isEmpty()
                    || birthday.getValue().toString().isEmpty() || !(maritalStatus.getSelectedToggle().isSelected());

            if (flag) {
                //fields not filled
                //Tray Notification
                Notification.notify("Error", "Please Enter All Fields", NotificationType.WARNING);
            } else {
                String sqlInsert = "UPDATE members SET fname = ?, lname = ?, phone_number = ?, sex = ?, address = ?, " +
                        "location = ?, birthday = ?, marital_status = ?, department = ?, " +
                        "position = ?, wedding_anniversary = ? where id = ?";

                try {
                    Connection conn = dbConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlInsert);
                    stmt.setString(1, fName.getText());
                    stmt.setString(2, lName.getText());
                    stmt.setString(3, phone_number.getText());
                    stmt.setString(4, sexCombo.getSelectionModel().getSelectedItem().value());
                    stmt.setString(5, member_address.getText());
                    stmt.setString(6, locationCombo.getSelectionModel().getSelectedItem());
                    stmt.setString(7, birthday.getValue().toString());
                    if (married_radio.isSelected()) {
                        stmt.setString(8, "MARRIED");
                    }else if(single_radio.isSelected()){
                        stmt.setString(8, "SINGLE");
                    }else {
                        stmt.setString(8, "CHILDREN / TEENAGERS");

                    }

                    if (existing_no.isSelected()) {
                        stmt.setString(9, "--");
                        stmt.setString(10, "--");
                    } else {
                        //get/concat all strings from the dept array
                        String s = "";
                        for(String str: dept){

                            s+= str + ", ";
                        }

                        //use concat string as entry
                        stmt.setString(9, s.isEmpty() ? "--" : s);
                        stmt.setString(10, position.getSelectedToggle() == null ? "--" : position_str);
                    }
                    stmt.setString(11, married_radio.isSelected() ? weddingAnniversary:"--");
                    stmt.setString(12, member.getID());

                    //execute and close
                    stmt.execute();
                    stmt.close();
                    conn.close();

                    //Tray notification
                    Notification.notify("Success", "Save Successful", NotificationType.SUCCESS);

                    Stage stage = (Stage) iconClose.getScene().getWindow();
                    stage.close();
                } catch (SQLException e) {
                    //Tray Notification
                    Notification.notify("Error", "Unable To save", NotificationType.ERROR);
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

    @FXML
    void cancelSave(ActionEvent event) {
        Stage stage = (Stage) iconClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    void disableOtherInfo(ActionEvent event) {
        //disable other infos
        disableOtherInformation();

    }

    private void disableOtherInformation() {
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

        enableOtherInforMations();

    }

    void enableOtherInforMations() {
        //enable other infos
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

        //set existing member default as no
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
        disableOtherInformation();
    }

    public void initToggleGroups() {
        //group all radio buttons and checkboxes
        maritalStatus = new ToggleGroup();
        maritalStatus.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            //get selected radio button
            RadioButton btn = (RadioButton) newValue.getToggleGroup().getSelectedToggle();
            marital_staus = btn.getText();
        });

        existingMembers = new ToggleGroup();

        position = new ToggleGroup();
        position.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            //get selected radio button
            RadioButton btn = (RadioButton) newValue.getToggleGroup().getSelectedToggle();
            position_str = btn.getText();
        });

        children_teenagers_radio.setToggleGroup(maritalStatus);
        single_radio.setToggleGroup(maritalStatus);
        married_radio.setToggleGroup(maritalStatus);
        married_radio.setOnAction(event -> {


            JFXDatePicker weddingAnniversaryDatePicker = new JFXDatePicker();

            weddingAnniversaryDatePicker.setOnAction(event1 -> {
                weddingAnniversary = weddingAnniversaryDatePicker.getValue().toString();
            });
            PopOver popOver = new PopOver(weddingAnniversaryDatePicker);
            popOver.setPrefHeight(200);
            popOver.setWidth(100);
            popOver.setTitle("  Select Anniversary Date");
            popOver.setDetached(true);
            popOver.show(married_radio);

            if (!weddingAnniversary.equals("--")) {
                String[] annDate = weddingAnniversary.split("-");
                Platform.runLater(() -> {
                    weddingAnniversaryDatePicker.setValue(LocalDate.of(Integer.parseInt(annDate[0]),
                            Integer.parseInt(annDate[1]), Integer.parseInt(annDate[2])));
                });


            }
        });



        existing_yes.setToggleGroup(existingMembers);
        existing_no.setToggleGroup(existingMembers);

        pastor.setToggleGroup(position);
        deacon.setToggleGroup(position);
        deaconess.setToggleGroup(position);
        position_nil.setToggleGroup(position);

        for (Node checkBox : department_grid.getChildren()) {
            ((JFXCheckBox)checkBox).setOnMouseClicked(event -> {


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

    public void initData(Member rowData) {
        //save Member data
        member = rowData;
        //fill all nodes with saved values
        fName.setText(rowData.getFirstName());
        lName.setText(rowData.getLastName());
        phone_number.setText(rowData.getPhoneNumber());
        sexCombo.setValue(Sex.fromValue(rowData.getSex()));
        member_address.setText(rowData.getAddress());
        locationCombo.setValue(rowData.getLocation());

        String[] date = rowData.getBirthday().split("-");
        birthday.setValue(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));

        weddingAnniversary = rowData.getWedding_anniversary();


        Platform.runLater(() -> {
        
            //auto select sunday school class
            for (Toggle j : maritalStatus.getToggles()) {

                if (j.toString().contains(rowData.getMarital_status())) {

                    j.setSelected(true);
                }
            }
        });

        //auto select department
        if ((!rowData.getDepartment().contains("--")) && !(rowData.getPosition().equals("--"))) {
            //first enable the necessary nodes
            existingMemberYes(rowData);
        }else if ((!rowData.getDepartment().contains("--")) || !(rowData.getPosition().equals("--"))){
            existingMemberYes(rowData);
        }else if((rowData.getDepartment().contains("--")) && (rowData.getPosition().equals("--"))){

            existing_no.setSelected(true);
            disableOtherInformation();
        }

    }

    private void existingMemberYes(Member rowData) {
        Platform.runLater(() -> {
            existing_yes.setSelected(true);
            enableOtherInforMations();

        });

        String[] deptt = rowData.getDepartment().split(", ");

        //do a nested loop to check all department against all nodes
        for (String dep : deptt) {
            dept.add(dep);
            for (Node node : department_grid.getChildren()) {
                if (node.toString().contains(dep)) {
                    ((JFXCheckBox) node).setSelected(true);
                }
            }
        }

        for (Node node : vbox_position.getChildren()) {
            //select actual position
            if (node.toString().contains(rowData.getPosition())) {
                ((JFXRadioButton) node).setSelected(true);
            }else if(rowData.getPosition().equals("--") && node instanceof JFXRadioButton &&
                    ((JFXRadioButton)node).getText().equals("NIL")){
                ((JFXRadioButton)node).setSelected(true);
            }
        }
    }

}
