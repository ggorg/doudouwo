package weixin.entity;

public class TyUser {
    private Integer id;
    private String tuTelphone;
    private String tuOpenId;
    private Boolean tuDisabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTuTelphone() {
        return tuTelphone;
    }

    public void setTuTelphone(String tuTelphone) {
        this.tuTelphone = tuTelphone;
    }

    public String getTuOpenId() {
        return tuOpenId;
    }

    public void setTuOpenId(String tuOpenId) {
        this.tuOpenId = tuOpenId;
    }

    public Boolean getTuDisabled() {
        return tuDisabled;
    }

    public void setTuDisabled(Boolean tuDisabled) {
        this.tuDisabled = tuDisabled;
    }
}
