package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Jay on 11/3/2016.
 */

@IgnoreExtraProperties
public class UserInfo implements Serializable{
    private String UserId;
    private String DPURL;
    private String DeviceInfo;
    private String Email;
    private String FullName;
    private String Lat;
    private String Long;
    private String Mobile;
    private String Gender;

    public UserInfo() {
    }

    public UserInfo(String userId, String DPURL, String deviceInfo, String email, String fullName, String lat, String aLong, String mobile, String gender) {
        UserId = userId;
        this.DPURL = DPURL;
        DeviceInfo = deviceInfo;
        Email = email;
        FullName = fullName;
        Lat = lat;
        Long = aLong;
        Mobile = mobile;
        Gender = gender;
    }

    public String getGender() {
        return Gender;
    }

    public String getDPURL() {
        return DPURL;
    }

    public String getDeviceInfo() {
        return DeviceInfo;
    }

    public String getEmail() {
        return Email;
    }

    public String getFullName() {
        return FullName;
    }

    public String getLat() {
        return Lat;
    }

    public String getLong() {
        return Long;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "DPURL='" + DPURL + '\'' +
                ", DeviceInfo='" + DeviceInfo + '\'' +
                ", Email='" + Email + '\'' +
                ", FullName='" + FullName + '\'' +
                ", Lat='" + Lat + '\'' +
                ", Long='" + Long + '\'' +
                ", Mobile='" + Mobile + '\'' +
                '}';
    }
}
