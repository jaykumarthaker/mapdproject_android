package remotedoorway.byteme.com.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import remotedoorway.byteme.com.R;
import remotedoorway.byteme.com.activity.LoginActivity;
import remotedoorway.byteme.com.models.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String picturePath;
    Boolean dpchanged=false;

    ImageView userDP;
    EditText etusername;
    TextView tvmobile,tvemail;
    UserInfo currentuser=null;

    String dpu;

    Button btnupdate,btnsignout,btnchangedp;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        userDP = (ImageView) view.findViewById(R.id.imgvsettings);
        etusername= (EditText) view.findViewById(R.id.etsettingsname);
        tvemail= (TextView) view.findViewById(R.id.tvsettingsemailaddress);
        tvmobile= (TextView) view.findViewById(R.id.tvsettingsphonenumber);

        btnupdate = (Button) view.findViewById(R.id.btnsettingsupdate);
        btnsignout = (Button) view.findViewById(R.id.btnsettingssignout);
        btnchangedp = (Button) view.findViewById(R.id.btnsettingschangedp);


        final DatabaseReference UserInfoTable = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        UserInfoTable.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot userinforows : dataSnapshot.getChildren())
                    {

                        UserInfo currentUser = userinforows.getValue(UserInfo.class);
                        currentUser.setUserId(userinforows.getKey().toString());
                        if(currentUser.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                        {
                            String urlProfileImg=currentUser.getDPURL();
                            etusername.setText("" + currentUser.getFullName());
                            tvemail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            tvmobile.setText(currentUser.getMobile());

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.firebasestorageurl));

                            StorageReference dpref = storageRef.child(currentUser.getDPURL());

                            Glide.with(getActivity())
                                    .using(new FirebaseImageLoader())
                                    .load(dpref)
                                    .into(userDP);



                            // showing dot next to notifications label
                            //navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);


                            break;
                        }
                    }
                }
                UserInfoTable.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(dpchanged) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.firebasestorageurl));

                    Uri file = Uri.fromFile(new File(picturePath));
                    StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dpu=taskSnapshot.getMetadata().getDownloadUrl().getLastPathSegment().toString();

                            DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("UserInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                            Map<String,Object> map2 = new HashMap<String, Object>();
                            map2.put("FullName",etusername.getText().toString());
                            map2.put("DeviceInfo",getDeviceName());
                            map2.put("DPURL", dpu);
                            root.updateChildren(map2);

                            Toast.makeText(getActivity(), "Your information updated!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("FullName", etusername.getText().toString());
                    map2.put("DeviceInfo", getDeviceName());

                    root.updateChildren(map2);

                    Toast.makeText(getActivity(), "Your information updated!!", Toast.LENGTH_SHORT).show();
                }
            }



        });



        btnchangedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });



        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Sign-Out?")
                        .setMessage("Are you sure you want sign-out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();




            }
        });


        return view;
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void pickImage() {
        Log.i("camera", "startCameraActivity()");
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == getActivity().RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


            //image.setImageBitmap(BitmapFactory.decodeFile(picturePath));


            if (BitmapFactory.decodeFile(picturePath) != null) {
                dpchanged=true;
                ImageView rotate = userDP;
                rotate.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }

        } else {

            Log.i("SonaSys", "resultCode: " + resultCode);
            switch (resultCode) {
                case 0:
                    Log.i("SonaSys", "User cancelled");
                    break;


            }

        }

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
