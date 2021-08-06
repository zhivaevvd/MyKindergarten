package Modules;


public class InvitationCode {

    public String ind, code;

    public InvitationCode(){}

    InvitationCode(String ID, String code){
        this.code = code;
        this.ind = ID;
    }

    public String getID() {
        return ind;
    }

    public void setID(String ID) {
        this.ind = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
