package ui.addMember;


public enum Sex {
    Male, Female;

    private Sex(){}

    public String value(){
        return name();
    }

    public static Sex fromValue(String v){
        return valueOf(v);
    }

}
