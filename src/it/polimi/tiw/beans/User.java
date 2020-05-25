package it.polimi.tiw.beans;

//import it.polimi.tiw.utility.JsonSupport;

public class User /*implements JsonSupport*/ {
    private int id;
    private String username;

    public User() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

   /* @Override
    public String convertToJSON() {
        String result ="{";
        result += "\"id\":\""+id+"\",";
        result += "\"username\":\""+username+"\",";
        result += "\"email\":\""+email+"\",";
        result += "\"role\":\""+role+"\",";
        if(level != null){
            result += "\"experience\":\""+level+"\",";
        }
        result += "\"photo\":\""+imageURL+"\"";
        result += "}";
        return result;
    }*/

}
