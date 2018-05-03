package remotedoorway.byteme.com.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.kyleduo.switchbutton.SwitchButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.models.Doors;
import remotedoorway.byteme.com.models.Status;

public class DoorListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoorListFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static DoorListFragment newInstance(String param1, String param2) {
        DoorListFragment fragment = new DoorListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    MqttAndroidClient mqttAndroidClient;

    List<Doors> OthersDoorsList =new ArrayList<Doors>();
    List<Doors> OwnerDoorsList =new ArrayList<Doors>();

    ListView ownerlistview, otherlistview;
    ArrayAdapter<Doors> owneradaptor,otheradaptor;

    TextView doorlist_bottomstatus;


    public DoorListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        populateDoorInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doorlist, container, false);


        owneradaptor = new OwnerDoorAdaptor();
        otheradaptor = new OtherDoorAdaptor();

        ownerlistview = (ListView) view.findViewById(R.id.lv_fragment_doorlist_ownerdoorlist);
        otherlistview = (ListView) view.findViewById(R.id.lv_fragment_doorlist_otherdoorlist);
        doorlist_bottomstatus = (TextView) view.findViewById(R.id.tv_fragment_doorlist_bottomstatus);
        connectToMQTT();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ownerlistview.setAdapter(owneradaptor);
        otherlistview.setAdapter(otheradaptor);
    }

    private void populateDoorInfo()
    {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        DatabaseReference Doors = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userid).child("Doors");

        // now lets get all his friends id
        Doors.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // now seprating DataSnapshot according Others and Owner

                DataSnapshot DSOthersDoors = dataSnapshot.child("Others");
                DataSnapshot DSOwnerDoors = dataSnapshot.child("Owner");
                OwnerDoorsList.clear();
                OthersDoorsList.clear();

                //Log.v("W got:",dataSnapshot.child("Owner").toString());
                for (DataSnapshot doorRows : DSOthersDoors.getChildren()) {
                    final Doors doors=doorRows.getValue(Doors.class);
                    doors.setDoorId(doorRows.getKey());

                    //now let's find owner's name
                    DatabaseReference drowner=FirebaseDatabase.getInstance().getReference().child("UserInfo").child(doorRows.child("OwnerId").getValue().toString()).child("FullName");
                    drowner.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.v("Actually I got", "" + dataSnapshot.getValue());
                            String ownername = "" + dataSnapshot.getValue();
                            if(!ownername.equals("null"))
                                doors.setOwnerName(ownername);
                            else
                                doors.setOwnerName("Your Friend");

                            OthersDoorsList.add(doors);
                            Log.v("Others got:",doors.toString());
                            otherlistview.setAdapter(otheradaptor);
                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


                for (DataSnapshot doorRows : DSOwnerDoors.getChildren()) {
                    final Doors doors=doorRows.getValue(Doors.class);
                    doors.setDoorId(doorRows.getKey());
                    OwnerDoorsList.add(doors);
                    Log.v("Owners got:",doors.toString());
                }


                ownerlistview.setAdapter(owneradaptor);

                //now setting listener on door Status
                setStatusListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private class OwnerDoorAdaptor extends ArrayAdapter<Doors>
    {
        public OwnerDoorAdaptor() {
            super(getActivity().getBaseContext(),R.layout.ownerdoorlistlistrowview, OwnerDoorsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.ownerdoorlistlistrowview,parent,false);
            }


            final Doors currentDoor= OwnerDoorsList.get(position);
            TextView tvdoorname=(TextView) itemview.findViewById(R.id.tvownerdoorlistdoorname);
            tvdoorname.setText(currentDoor.getDoorName());

            final SwitchButton ownerSwitch=(SwitchButton) itemview.findViewById(R.id.switchownerdorrlist);
            if(currentDoor.getCurrentStatus()!=null) {
                ownerSwitch.setAlpha((float) 0.1);
                if (currentDoor.getCurrentStatus().equals("1")) {
                    ownerSwitch.setChecked(true);
                }
                else if (currentDoor.getCurrentStatus().equals("0")) {
                    ownerSwitch.setChecked(false);
                }
            }


            ownerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        ownerSwitch.setChecked(false);
                        publishMessage(currentDoor.getDoorId(),"1" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    }
                    else
                    {
                        ownerSwitch.setChecked(true);
                        publishMessage(currentDoor.getDoorId(),"0" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    }
                }
            });





            return itemview;
        }
    }



    private class OtherDoorAdaptor extends ArrayAdapter<Doors>
    {
        public OtherDoorAdaptor() {
            super(getActivity().getBaseContext(),R.layout.otherdoorlistlistrowview, OthersDoorsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemview=convertView;
            if(itemview==null)
            {
                itemview=getActivity().getLayoutInflater().inflate(R.layout.otherdoorlistlistrowview,parent,false);
            }


            final Doors currentDoor= OthersDoorsList.get(position);
            TextView tvdoorname=(TextView) itemview.findViewById(R.id.tvotherdoorlistdoorname);
            TextView tvownermane=(TextView) itemview.findViewById(R.id.tvotherdoorlistownername);

            final SwitchButton otherswitch=(SwitchButton) itemview.findViewById(R.id.switchotherdorrlist);
            if(currentDoor.getCurrentStatus()!=null) {
                if (currentDoor.getCurrentStatus().equals("1")) {
                    otherswitch.setChecked(true);
                }
                else if (currentDoor.getCurrentStatus().equals("0")) {
                    otherswitch.setChecked(false);
                }
            }

            otherswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        otherswitch.setChecked(false);
                        publishMessage(currentDoor.getDoorId(),"1" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    }
                    else
                    {
                        otherswitch.setChecked(true);
                        publishMessage(currentDoor.getDoorId(),"0" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    }
                }
            });

            tvdoorname.setText(currentDoor.getDoorName());
            tvownermane.setText("Shared by " + currentDoor.getOwnerName());
            return itemview;
        }
    }


    private void setStatusListener()
    {
        DatabaseReference doorStatus = FirebaseDatabase.getInstance().getReference().child("Status");

        // now lets get all his friends id
        doorStatus.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot statusRows : dataSnapshot.getChildren()) {
                    Status status = statusRows.getValue(Status.class);
                    status.setDoorId(statusRows.getKey());
                    for(int i=0;i<OthersDoorsList.size();i++)
                    {
                        if(OthersDoorsList.get(i).getDoorId().equals(status.getDoorId()))
                        {
                            OthersDoorsList.get(i).setCurrentStatus(status.getCurrentStatus());
                            Log.v("Others_change",OthersDoorsList.get(i).toString());
                        }


                    }

                    for(int i=0;i<OwnerDoorsList.size();i++)
                    {
                        if(OwnerDoorsList.get(i).getDoorId().equals(status.getDoorId()))
                        {
                            OwnerDoorsList.get(i).setCurrentStatus(status.getCurrentStatus());
                            Log.v("Owner_change",OwnerDoorsList.get(i).toString());
                        }

                    }
                }

                owneradaptor.notifyDataSetChanged();
                otheradaptor.notifyDataSetChanged();
                ownerlistview.setAdapter(owneradaptor);
                otherlistview.setAdapter(otheradaptor);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void connectToMQTT()
    {
        if(mqttAndroidClient!=null) {
            mqttAndroidClient.disconnect();
        }
        mqttAndroidClient = new MqttAndroidClient(getContext(), getResources().getString(R.string.mqttServerUri), MqttClient.generateClientId());
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    doorlist_bottomstatus.setText("Reconnected");
                    //not subscribing as we dont need it right now
                    // /subscribeToTopic("firsttry");
                } else {
                    doorlist_bottomstatus.setText("Connected to: " + serverURI);
                    //Toast.makeText(getContext(),"yes connected to " + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void connectionLost(Throwable cause) {
                doorlist_bottomstatus.setText("The Connection was lost.");
                //Toast.makeText(getContext(),"The Connection was lost." + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                doorlist_bottomstatus.setText("Incoming message: " + new String(message.getPayload()));
                //Toast.makeText(getContext(),"The Connection was lost." + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        mqttConnectOptions.setUserName(getResources().getString(R.string.mqttUserName));
        mqttConnectOptions.setPassword(getResources().getString(R.string.mqttPassword).toCharArray());





        //addToHistory("Connecting to " + serverUri);
        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                //not subscribing as we dont need it right now
                //subscribeToTopic("firsttry");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(getContext(),"Failed To Connect" + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void subscribeToTopic(String subscriptionTopic){
        mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                doorlist_bottomstatus.setText("Subscribed!");
                //Toast.makeText(getContext(),"Subscribed" + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                doorlist_bottomstatus.setText("Failed to subscribe");
                //Toast.makeText(getContext(),"Failed to subscribe" + getResources().getString(R.string.mqttServerUri) ,Toast.LENGTH_SHORT).show();
            }
        });

        // THIS DOES NOT WORK!
        mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // message Arrived!
                final String msg = new String(message.getPayload());
                //Log.i(TAG, "Message Arrived: " + msg);
                getActivity().runOnUiThread(new Runnable(){
                    public void run() {
                        Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
                    }
                });
                //System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
            }
        });
    }

    public void publishMessage(String publishTopic, String publishMessage){
        MqttMessage message = new MqttMessage();
        message.setPayload(publishMessage.getBytes());
        mqttAndroidClient.publish(publishTopic, message);
        //doorlist_bottomstatus.setText("Message Published");
        Snackbar.make(getView(), "Your Request Initiated..!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        if(!mqttAndroidClient.isConnected()){
            //doorlist_bottomstatus.setText(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
        }
    }
}
