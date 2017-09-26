package ui.viewMember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.sun.istack.internal.Nullable;
import Util.dbConnection;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Member;
import tray.notification.NotificationType;
import ui.editMemberDetails.editMemberDetailsController;
import ui.notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by Levono on 6/30/2017.
 */
public class ViewMemberController implements Initializable {
    @FXML
    private StackPane stackPane;

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
    private TextField tfsearch;

    @FXML
    private TableView<Member> memberTableView;

    @FXML
    private TableColumn<Member, String> fNameCol;

    @FXML
    private TableColumn<Member, String> lNameCol;

    @FXML
    private TableColumn<Member, String> numberCol;

    @FXML
    private TableColumn<Member, String> sexCol;

    @FXML
    private TableColumn<Member, String> birthdayCol;

    @FXML
    private TableColumn<Member, String> addressCol;

    @FXML
    private TableColumn<Member, String> locationCol;

    @FXML
    private TableColumn<Member, String> maritalStatusCol;

    @FXML
    private TableColumn<Member, String> departmentCol;

    @FXML
    private TableColumn<Member, String> positionCol;

    @FXML
    private TableColumn<Member, String> weddingAnniversaryCol;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private JFXButton edit;

    @FXML
    private JFXButton addMember;

    @FXML
    private JFXButton delete;

    @FXML
    public JFXDrawer drawer;
    @FXML
    private MenuButton view_members_by;

    @FXML
    private Menu location;

    @FXML
    private Menu department_menu;

    @FXML
    private Menu married_menu;

    @FXML
    private RadioMenuItem choir;

    @FXML
    private RadioMenuItem usher;

    @FXML
    private RadioMenuItem children_teachers;

    @FXML
    private RadioMenuItem drama;

    @FXML
    private RadioMenuItem security;

    @FXML
    private RadioMenuItem sanctuary_keepers;

    @FXML
    private Menu position;

    @FXML
    private RadioMenuItem pastor;

    @FXML
    private RadioMenuItem deacon;

    @FXML
    private RadioMenuItem deaconess;

    @FXML
    private Menu marital_status;

    @FXML
    private RadioMenuItem single;

    @FXML
    private RadioMenuItem children_teenagers;

    @FXML
    private RadioMenuItem men;

    @FXML
    private RadioMenuItem women;

    @FXML
    private RadioMenuItem all_married;
    private ToggleGroup locationToggle;
    private ToggleGroup departmentToggle;
    private ToggleGroup maritalStatusToggle;

    private ToggleGroup positionToggle;

    HamburgerBackArrowBasicTransition burgerTask2;
    Connection conn;
    private ObservableList<Member> data;
    private ObservableList<Member> searchData;
    ObservableList<RadioMenuItem> locationMenuItems;
    ObservableList<RadioMenuItem> allMenuItems;
    private double dragDeltaY, dragDeltaX;
    private ResultSet rs;

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

    @FXML
    void minimizeAction(MouseEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void editAction(ActionEvent event) {
        closeDrawerAction();
        loadEditMemberDetails(memberTableView.getSelectionModel().getSelectedItem());
    }

    //load edit window
    void loadEditMemberDetails(Member member) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/editMemberDetails/editMemberDetails.fxml"));
            Stage updateStage = new Stage(StageStyle.UNDECORATED);
            Parent root = (Parent) loader.load();
            root.setOnMousePressed(e1 -> {
                root.setCursor(Cursor.CLOSED_HAND);
                dragDeltaX = updateStage.getX() - e1.getScreenX();
                dragDeltaY = updateStage.getY() - e1.getScreenY();
            });

            root.setOnMouseDragged(e1 -> {
                updateStage.setX(e1.getScreenX() + dragDeltaX);
                updateStage.setY(e1.getScreenY() + dragDeltaY);
            });

            root.setOnMouseReleased(e1 -> {
                root.setCursor(Cursor.DEFAULT);
            });
            updateStage.setScene(new Scene(root));
            updateStage.setTitle("Update Member Information");
            updateStage.setResizable(false);

            editMemberDetailsController controller = loader.<editMemberDetailsController>getController();
            controller.initData(member);
            updateStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @FXML
    void refreshAction(ActionEvent event) {
        closeDrawerAction();
        loadData();
        uncheckMenuRadioItems(null);
    }



    private void uncheckMenuRadioItems(@Nullable RadioMenuItem exception) {
        // uncheck all radio buttions

        for (RadioMenuItem j : allMenuItems) {
            if (exception == j) {
                continue;
            }
            j.setSelected(false);
        }


    }

    @FXML
    void searchAction(MouseEvent event) {
        performSearchOP();
    }

    private void performSearchOP() {
        String[] searchKey = tfsearch.getText().split(" ");

        for (String searchK : searchKey) {

            //do search based on first name and last name
            String sqlQuery1 = "select * FROM members where fName = '" + searchK + "'";
            String sqlQuery2 = "select * FROM members where lName = '" + searchK + "'";
            String sqlQuery3 = "select * FROM members where phone_number = '" + searchK + "'";

            conn = dbConnection.getConnection();
            searchData = FXCollections.observableArrayList();
            ResultSet rs = null;

            if (isNumeric(searchK)) {
                //do search based on phone number

                conn = dbConnection.getConnection();
                searchData = FXCollections.observableArrayList();

                try {
                    rs = conn.createStatement().executeQuery(sqlQuery3);
                    while (rs.next()) {
                        searchData.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)), rs.getString(5),
                                rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                    }
                    rs.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                memberTableView.setItems(null);
                memberTableView.setItems(searchData);

                //clear search textfield
                tfsearch.clear();
                memberTableView.requestFocus();
              Notification.customNotify("Search Complete", searchData.size() + " Match found", NotificationType.SUCCESS,
                        getClass().getResource("/ui/Pics/search.png").toExternalForm());

            } else {
                try {
                    rs = conn.createStatement().executeQuery(sqlQuery1);
                    while (rs.next()) {
                        //search based on firstName
                        searchData.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)),
                                rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
                                rs.getString(10), rs.getString(11), rs.getString(12)));
                    }

                    rs = conn.createStatement().executeQuery(sqlQuery2);
                    while (rs.next()) {
                        searchData.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)), rs.getString(5),
                                rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                    }

                    rs.close();
                    conn.close();

                    memberTableView.setItems(null);
                    memberTableView.setItems(searchData);

                    //clear search textfield and show notification
                    tfsearch.clear();
                    memberTableView.requestFocus();
                    Notification.customNotify("Search Complete", searchData.size() + " Match found", NotificationType.SUCCESS,
                            getClass().getResource("/ui/Pics/search.png").toExternalForm());
                } catch (SQLException e) {
                    e.printStackTrace();


                }
            }
        }
    }

    @FXML
    void onRootMouseClicked(MouseEvent event) {
        closeDrawerAction();
    }

    @FXML
    void onTableClicked(MouseEvent event) {
        closeDrawerAction();
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        closeDrawerAction();

        //delete selected member from table
        Member selectedItem = memberTableView.getSelectionModel().getSelectedItem();
        String sqlQuery = "delete from members where id = ?";
        try {
            Connection conn = dbConnection.getConnection();
            PreparedStatement pr = conn.prepareStatement(sqlQuery);
            pr.setString(1, selectedItem.getID());
            pr.execute();
            pr.close();
            conn.close();
            loadData();
            Notification.customNotify("Success", "Member Was successfully deleted", NotificationType.NOTICE, getClass().getResource("/ui/Pics/garbage.png").toExternalForm());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void addMember() {

        closeDrawerAction();

        //show add member scene when add member button is clicked
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/addMember/addMember.fxml"));
            Stage updateStage = new Stage(StageStyle.UNDECORATED);
            Parent root = (Parent) loader.load();
            root.setOnMousePressed(event -> {

                root.setCursor(Cursor.CLOSED_HAND);
                dragDeltaX = updateStage.getX() - event.getScreenX();
                dragDeltaY = updateStage.getY() - event.getScreenY();
            });

            root.setOnMouseDragged(event -> {
                updateStage.setX(event.getScreenX() + dragDeltaX);
                updateStage.setY(event.getScreenY() + dragDeltaY);
            });

            root.setOnMouseReleased(event -> {
                root.setCursor(Cursor.DEFAULT);
            });
            updateStage.setScene(new Scene(root));
            updateStage.setResizable(false);

            ui.addMember.addMemberController controller = loader.getController();
            updateStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawer.setDisable(true);

        initMenuButton();
        initViewByRadioAction();
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
            //init onAction for search textfield
            tfsearch.setOnAction(event -> {
                closeDrawerAction();
                performSearchOP();
            });

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

        //load data
        initColumns();
        loadData();

        //initialize table double click
        memberTableView.setRowFactory(e -> {
            TableRow<Member> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Member rowData = row.getItem();
                    loadEditMemberDetails(rowData);
                }
            });
            return row;
        });



    }

    @FXML
    void closeDrawerAction(){
        if (drawer.isShown()) {
            drawer.close();
            burgerTask2.setRate(burgerTask2.getRate() * -1);
            burgerTask2.play();
            drawer.setDisable(true);
        }
    }

    private void initViewByRadioAction() {

        for (RadioMenuItem j : allMenuItems) {
            j.setOnAction(event -> {
                //first uncheck the others when one is clicked
                uncheckMenuRadioItems(j);

                //then do some other thing
            });
        }


        //set onaction for all menuRadioButton
        for (RadioMenuItem radioMenuItem : allMenuItems) {

            if (radioMenuItem.getParentMenu().getId().equals("married_menu") ||
                    radioMenuItem.getParentMenu().getId().equals("department_menu")) {

                //start with special cases of department and married

                switch (radioMenuItem.getParentMenu().getId()) {

                    case "married_menu":
                        if (radioMenuItem.getId().equals("men")) {

                            radioMenuItem.setOnAction(event -> {

                                String sqlQuery = "select * FROM members where marital_status = 'married' AND sex = 'male'";
                                executeViewMemberQuery(sqlQuery);
                                uncheckMenuRadioItems(men);

                            });


                        } else if (radioMenuItem.getId().equals("women")) {

                            radioMenuItem.setOnAction(event -> {

                                String sqlQuery = "select * FROM members where marital_status = 'married' AND sex = 'female'";
                                executeViewMemberQuery(sqlQuery);
                                uncheckMenuRadioItems(women);

                            });
                        } else {
                            radioMenuItem.setOnAction(event -> {
                                String sqlQuery = "select * FROM members where marital_status = 'married'";
                                executeViewMemberQuery(sqlQuery);
                                uncheckMenuRadioItems(all_married);
                            });
                        }
                        break;
                    case "department_menu":

                        if (radioMenuItem.getId().equals("choir")) {
                            choir.setOnAction(e -> {
                                getSelectedDepartment("choir");
                                uncheckMenuRadioItems(choir);

                            });

                        } else if (radioMenuItem.getId().equals("usher")) {
                            usher.setOnAction(e -> {
                                getSelectedDepartment("usher");
                                uncheckMenuRadioItems(usher);

                            });
                        } else if (radioMenuItem.getId().equals("security")) {
                            security.setOnAction(e -> {

                                getSelectedDepartment("security");
                                uncheckMenuRadioItems(security);
                            });

                        } else if (radioMenuItem.getId().equals("sanctuary_keepers")) {
                            sanctuary_keepers.setOnAction(e -> {
                                getSelectedDepartment("sanctuary_keepers");
                                uncheckMenuRadioItems(sanctuary_keepers);

                            });
                        } else if (radioMenuItem.getId().equals("children_teachers")) {
                            children_teachers.setOnAction(e -> {
                                getSelectedDepartment("children_teacher");
                                uncheckMenuRadioItems(children_teachers);

                            });
                        } else if (radioMenuItem.getId().equals("drama")) {
                            drama.setOnAction(e -> {

                                getSelectedDepartment("drama");
                                uncheckMenuRadioItems(drama);
                            });
                        }

                        break;
                    default:
                        //nothing to do here so just break
                        break;
                }
            }else if (radioMenuItem.getParentMenu().getId().equals("marital_status") &&
                    radioMenuItem.getId().equals("children_teenagers")){

                radioMenuItem.setOnAction(e -> {
                    String sqlQuery = "select * FROM members where " + radioMenuItem.getParentMenu().getId() +
                            "= '" + radioMenuItem.getText() + "'";
                    executeViewMemberQuery(sqlQuery);
                    uncheckMenuRadioItems(children_teenagers);
                });
            }else {
                if (radioMenuItem.getParentMenu().getId().equals("location")){
                    radioMenuItem.setOnAction(e -> {

                        String sqlQuery = "select * FROM members where " + radioMenuItem.getParentMenu().getId() +
                                "= '" + radioMenuItem.getText() + "'";
                        executeViewMemberQuery(sqlQuery);
                        uncheckMenuRadioItems(radioMenuItem);
                    });
                }else {
                    radioMenuItem.setOnAction(e -> {

                        String sqlQuery = "select * FROM members where " + radioMenuItem.getParentMenu().getId() +
                                "= '" + radioMenuItem.getId() + "'";
                        executeViewMemberQuery(sqlQuery);
                        uncheckMenuRadioItems(radioMenuItem);
                    });
                }
            }
        }
    }

    private void getSelectedDepartment(String department) {
        //loop through the tables data to pick members in a particular dept
        searchData = FXCollections.observableArrayList();
        for (Member member : data) {
            if (member.getDepartment().toLowerCase().contains(department)) {
                searchData.add(member);
            }
        }
        memberTableView.setItems(null);
        memberTableView.setItems(searchData);
    }

    private void executeViewMemberQuery(String sqlQuery) {
        conn = dbConnection.getConnection();
        searchData = FXCollections.observableArrayList();

        try {
            rs = conn.createStatement().executeQuery(sqlQuery);
            while (rs.next()) {
                searchData.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        memberTableView.setItems(null);
        memberTableView.setItems(searchData);

    }

    private void initMenuButton() {


        locationToggle = new ToggleGroup();

        String sql = "SELECT * FROM LOCATION";
        Connection conn = dbConnection.getConnection();
        ResultSet rs = null;
        PreparedStatement pr = null;
        locationMenuItems = FXCollections.observableArrayList();

        try {

            pr = conn.prepareStatement(sql);
            rs = pr.executeQuery();

            while (rs.next()) {
                String locationName = rs.getString("location_name");
                RadioMenuItem itemEffect = new RadioMenuItem(locationName);
                itemEffect.setOnAction(event -> {
                    // set sql command action here select from members where location = locationName
                });
                itemEffect.setToggleGroup(locationToggle);
                locationMenuItems.add(itemEffect);
            }
            location.getItems().setAll(locationMenuItems);


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

        //keep all radio items in one place
        allMenuItems = FXCollections.observableArrayList();
        allMenuItems.addAll(locationMenuItems);
        allMenuItems.addAll(children_teenagers, single, choir, usher, children_teachers,
                drama, security, sanctuary_keepers, pastor, deacon, deaconess, all_married,
                men, women);


        //init remaining menuitem menuRadiotoggle groups
        children_teenagers.setToggleGroup(maritalStatusToggle);
        single.setToggleGroup(maritalStatusToggle);
        men.setToggleGroup(maritalStatusToggle);
        women.setToggleGroup(maritalStatusToggle);


        choir.setToggleGroup(departmentToggle);
        usher.setToggleGroup(departmentToggle);
        sanctuary_keepers.setToggleGroup(departmentToggle);
        security.setToggleGroup(departmentToggle);
        children_teachers.setToggleGroup(departmentToggle);
        drama.setToggleGroup(departmentToggle);

        pastor.setToggleGroup(positionToggle);
        deacon.setToggleGroup(positionToggle);
        deaconess.setToggleGroup(positionToggle);


    }

    private void initColumns() {
        //initialize columns using names declared in the model

        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        numberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        birthdayCol.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        maritalStatusCol.setCellValueFactory(new PropertyValueFactory<>("marital_status"));
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        weddingAnniversaryCol.setCellValueFactory(new PropertyValueFactory<>("wedding_anniversary"));
    }

    private void loadData() {

        //get data to fill the table
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT * FROM members";
            data = FXCollections.observableArrayList();
            rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                data.add(new Member(rs.getString(1), rs.getString(2), rs.getString(3), (rs.getString(4)), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
            }
            rs.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        memberTableView.setItems(null);
        memberTableView.setItems(data);
    }

    public boolean isNumeric(String s) {
        return Pattern.matches("\\d+", s);
    }

}
