package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Jay on 11/3/2016.
 */

@IgnoreExtraProperties
public class Status implements Serializable{

    private String CurrentStatus;
    private String DoorId;
    public Status() {
    }

    public Status(String currentStatus, String doorId) {
        CurrentStatus = currentStatus;
        DoorId = doorId;
    }

    public String getCurrentStatus() {
        return CurrentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        CurrentStatus = currentStatus;
    }

    public String getDoorId() {
        return DoorId;
    }

    public void setDoorId(String doorId) {
        DoorId = doorId;
    }

    @Override
    public String toString() {
        return "Status{" +
                "CurrentStatus='" + CurrentStatus + '\'' +
                ", DoorId='" + DoorId + '\'' +
                '}';
    }
}
