package androidlabs.gsheets2.model;



public class MyDataModel {

    private String name;

    @Override
    public String toString() {
        return  country+"."+name;
    }

    private String country;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }



}