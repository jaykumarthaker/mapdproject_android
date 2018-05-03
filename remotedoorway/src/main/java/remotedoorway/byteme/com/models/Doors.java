package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Jay on 11/3/2016.
 */

@IgnoreExtraProperties
public class Doors implements Serializable{

    private String DoorId;
    private String DoorName;
    private String AccessDate;
    private String InstallDate;
    private String CurrentStatus;
    private String OwnerName;
    private String status;


    public Doors() {
    }

    public Doors(String doorId, String doorName, String accessDate, String installDate, String currentStatus, String ownerName, String status) {
        DoorId = doorId;
        DoorName = doorName;
        AccessDate = accessDate;
        InstallDate = installDate;
        CurrentStatus = currentStatus;
        OwnerName = ownerName;
        this.status = status;
    }

    public String getDoorId() {
        return DoorId;
    }

    public void setDoorId(String doorId) {
        DoorId = doorId;
    }

    public String getDoorName() {
        return DoorName;
    }

    public void setDoorName(String doorName) {
        DoorName = doorName;
    }

    public String getAccessDate() {
        return AccessDate;
    }

    public void setAccessDate(String accessDate) {
        AccessDate = accessDate;
    }

    public String getInstallDate() {
        return InstallDate;
    }

    public void setInstallDate(String installDate) {
        InstallDate = installDate;
    }

    public String getCurrentStatus() {
        return CurrentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        CurrentStatus = currentStatus;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Doors{" +
                "DoorId='" + DoorId + '\'' +
                ", DoorName='" + DoorName + '\'' +
                ", AccessDate='" + AccessDate + '\'' +
                ", InstallDate='" + InstallDate + '\'' +
                ", CurrentStatus='" + CurrentStatus + '\'' +
                ", OwnerName='" + OwnerName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
