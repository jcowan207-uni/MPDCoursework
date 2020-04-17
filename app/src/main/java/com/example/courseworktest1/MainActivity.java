package com.example.courseworktest1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
Fragment fragment;

    //allows for storage permission on the device
    private static final int STORAGE_PERMISSION_CODE = 1;

    //when the app first runs, this method is ran first
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState); //when app runs, the instance is saved
            setContentView(R.layout.activity_main);
            BottomNavigationView navView = findViewById(R.id.nav_view); //connects to the bottom nav bar (XML)

            //builds the nav bar
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder( //connects to the various navigation fragments for the nav bar
                    R.id.navigation_CurrentIncidents, R.id.navigation_roadworks, R.id.navigation_PlannedRw, R.id.navigation_maps)
                    .build();
            //setting up the nav controller
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            //checks for user permission in the manifest file, creates a notification pop up on the screen when location access has been granted
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                requestPermission();
            }
        } catch (Exception exe) {
            System.out.println("errorNavBar" + exe.getMessage());
        }
    }
    //main method for requesting permission
    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("TEXT")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override //displays a notification depending on the request result
    public void onRequestPermissionsResult(int StorageCode, @NonNull String[] permissions, @NonNull int[] permissionGranted) {
        if(StorageCode == STORAGE_PERMISSION_CODE){
            if(permissionGranted.length > 0 && permissionGranted[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void setSupportActionBar(Toolbar toolbar) {
    }


}
