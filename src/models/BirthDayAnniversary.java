package models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by CRM on 1/4/1980.
 */
public class BirthDayAnniversary extends RecursiveTreeObject<BirthDayAnniversary> {
    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    private StringProperty ID;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty Month;
    private StringProperty day;

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getMonth() {
        return Month.get();
    }

    public StringProperty monthProperty() {
        return Month;
    }

    public void setMonth(String month) {
        this.Month.set(month);
    }

    public String getDay() {
        return day.get();
    }

    public StringProperty dayProperty() {
        return day;
    }

    public void setDay(String day) {
        this.day.set(day);
    }

    public BirthDayAnniversary(String id, String firstName, String lastName, String month, String day){
        this.ID = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.Month = new SimpleStringProperty(month);
        this.day = new SimpleStringProperty(day);
    }
}
