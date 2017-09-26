package models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Number extends RecursiveTreeObject<Number> {

    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

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

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    StringProperty ID;
    StringProperty firstName;
    StringProperty lastName;
    StringProperty number;

    public String getDepartmemt() {
        return departmemt.get();
    }

    public StringProperty departmemtProperty() {
        return departmemt;
    }

    public void setDepartmemt(String departmemt) {
        this.departmemt.set(departmemt);
    }

    StringProperty departmemt;

    public Number(String ID, String firstName, String lastName, String number, String department) {
        this.ID = new SimpleStringProperty(ID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.number = new SimpleStringProperty(number);
        this.departmemt = new SimpleStringProperty(department);
    }

}