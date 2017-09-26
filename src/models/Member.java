package models;

import javafx.beans.property.*;

/**
 * Created by CRM on 8/5/2017.
 */
public class Member {
    private final StringProperty ID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty phoneNumber;
    private final StringProperty sex;
    private final StringProperty address;
    private final StringProperty location;
    private final StringProperty birthday;
    private final StringProperty marital_status;
    private final StringProperty department;
    private final StringProperty position;
    private final StringProperty wedding_anniversary;

    public String getWedding_anniversary() {
        return wedding_anniversary.get();
    }

    public StringProperty wedding_anniversaryProperty() {
        return wedding_anniversary;
    }

    public void setWedding_anniversary(String wedding_anniversary) {
        this.wedding_anniversary.set(wedding_anniversary);
    }

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

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getSex() {
        return sex.get();
    }

    public StringProperty sexProperty() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getBirthday() {
        return birthday.get();
    }

    public StringProperty birthdayProperty() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday.set(birthday);
    }

    public String getMarital_status() {
        return marital_status.get();
    }

    public StringProperty marital_statusProperty() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status.set(marital_status);
    }

    public String getDepartment() {
        return department.get();
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public String getPosition() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
    }



    public Member(String id, String fName, String lName, String number, String mSex, String mAddress,
                  String mLocation, String mBirthday, String mMarital_status, String mDepartment, String mPosition, String wedding_anniversary) {
        ID = new SimpleStringProperty(id);
        firstName = new SimpleStringProperty(fName);
        lastName = new SimpleStringProperty(lName);
        phoneNumber = new SimpleStringProperty(number);
        sex = new SimpleStringProperty(mSex);
        address = new SimpleStringProperty(mAddress);
        location = new SimpleStringProperty(mLocation);
        birthday = new SimpleStringProperty(mBirthday);
        marital_status = new SimpleStringProperty(mMarital_status);
        department = new SimpleStringProperty(mDepartment);
        position = new SimpleStringProperty(mPosition);
        this.wedding_anniversary = new SimpleStringProperty(wedding_anniversary);
    }
}
