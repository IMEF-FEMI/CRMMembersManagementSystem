package models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by CRM on 9/12/2017.
 */
public class Attendance extends RecursiveTreeObject<Attendance> {
    private final StringProperty ID;

    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getWeek() {
        return week.get();
    }

    public StringProperty weekProperty() {
        return week;
    }

    public void setWeek(String week) {
        this.week.set(week);
    }

    public String getData() {
        return data.get();
    }

    public StringProperty dataProperty() {
        return data;
    }

    public void setData(String data) {
        this.data.set(data);
    }

    private final StringProperty week;
    private final StringProperty data;

    public Attendance(String id, String week, String data){
        this.ID = new SimpleStringProperty(id);
        this.week = new SimpleStringProperty(week);
        this.data = new SimpleStringProperty(data);
    }
}
