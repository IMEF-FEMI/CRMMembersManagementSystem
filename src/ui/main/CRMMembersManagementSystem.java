package ui.main;

import Util.dbConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;

public class CRMMembersManagementSystem extends Application {
    public static Boolean isSplashLoaded = false;
    private Connection conn;
    private Statement stmt;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/main/main.fxml"));
        primaryStage.setTitle("CRM Members Management System");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/ui/Pics/LOGO.jpg")));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.centerOnScreen();
        primaryStage.setScene(new Scene(root, 590, 386));
        primaryStage.show();

        new Thread(() -> {
            //set up tables in new thread to not slow down the program
            setUpMembersTable();
            setUpLocationTable();
            setUpAttendanceTable();
            System.out.println("Tables created");
        }).start();

    }

    private void setUpAttendanceTable() {
        //create location table to hold members location

        String TABLE_NAME = "MONTHLY_ATTENDANCE";
        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {

                System.out.println("Table " + TABLE_NAME + " Already exists and ready to go");

            } else {

                String monthlyAttendance = "CREATE TABLE monthly_attendance (\n" +
                        "    id    VARCHAR (20) PRIMARY KEY\n" +
                        "                        COLLATE NOCASE,\n" +
                        "    year  INTEGER (4),\n" +
                        "    month VARCHAR (20)  COLLATE NOCASE,\n" +
                        "    data  INTEGER (20) \n" +
                        ");";
                String weeklyAttendance = "CREATE TABLE weekly_attendance (\n" +
                        "    id   VARCHAR (20) REFERENCES monthly_attendance (id),\n" +
                        "    week VARCHAR (30) COLLATE NOCASE,\n" +
                        "    data INTEGER (20) \n" +
                        ");";

                stmt.execute(monthlyAttendance);
                stmt.close();
                //to ensure monthlyAttendance table is created before weekly_attendance table
                stmt = conn.createStatement();
                stmt.execute(weeklyAttendance);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //close statement and connection
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpLocationTable() {

        //create location table to hold members location

        String TABLE_NAME = "LOCATION";
        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {

                System.out.println("Table " + TABLE_NAME + " Already exists and ready to go");

            } else {

                String locationSql = "CREATE TABLE LOCATION ( location_name varchar(200) );";
                stmt.execute(locationSql);
                stmt.execute("INSERT INTO location (location_name) VALUES ('Ungwan Romi');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Kudenda');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Nassarawa');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Kabala West');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Ungwan Biji');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Gonin Gora');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Sabo');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Kakuri');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Trikania');");
                stmt.execute("INSERT INTO location (location_name) VALUES ('Television');");
                ResultSet rs = stmt.executeQuery("select sqlite_version();");
                System.out.println(rs.getString(1));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //close statement and connection
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void setUpMembersTable() {
        //table to contain member info
        try {
            conn = dbConnection.getConnection();
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            String TABLE_NAME = "MEMBERS";
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {

                System.out.println("Table " + TABLE_NAME + " Already exists and ready to go");

            } else {
                String membersSql = "CREATE TABLE members (\n" +
                        "    id             varchar(200) PRIMARY KEY,\n" +
                        "    fName          varchar(200) COLLATE NOCASE,\n" +
                        "    lName          varchar(200) COLLATE NOCASE,\n" +
                        "    phone_number   varchar(15)  COLLATE NOCASE,\n" +
                        "    sex            varchar(200) COLLATE NOCASE,\n" +
                        "    address        varchar(200) COLLATE NOCASE,\n" +
                        "    location       varchar(200) COLLATE NOCASE,\n" +
                        "    birthday       varchar(200) COLLATE NOCASE,\n" +
                        "    marital_status varchar(200) COLLATE NOCASE,\n" +
                        "    department     varchar(200) COLLATE NOCASE,\n" +
                        "    position       varchar(200) COLLATE NOCASE,\n" +
                        "    wedding_anniversary varchar(200) COLLATE NOCASE\n" +
                        ");";

                stmt.execute(membersSql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                //close statement and connection
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
