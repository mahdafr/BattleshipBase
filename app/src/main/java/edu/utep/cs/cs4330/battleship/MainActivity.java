/*
 * @created Mahdokht Afravi on 04/08 S
 *
 * Starts the Battleship App by asking the user
 *   to choose a method of playing the game:
 *   against a local opponent, a network opponent,
 *   or the computer.
 *
 * @modified Mahdokht Afravi on 04/12 W
 */
package edu.utep.cs.cs4330.battleship;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Battleship game;
    Spinner p2p, ai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game = game.getGame();
        p2p = (Spinner) findViewById(R.id.P2Pspinner);
        setP2PSpinner();
        ai = (Spinner) findViewById(R.id.AIspinner);
        setAISpinner();
    }

    /** Show a toast message. */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /* Sets adapters to the Spinners */
    private void setP2PSpinner() {
        //P2P Adapter
        ArrayAdapter<CharSequence> p2pAdapter = ArrayAdapter.createFromResource(this,R.array.p2p,android.R.layout.simple_spinner_item);
        p2pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        p2p.setAdapter(p2pAdapter);
        p2p.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ( position ) {
                    case 0: //name: Play on Network
                        break;
                    case 1: //Bluetooth
                        startBTGame();
                        break;
                    case 2: //Wifi Direct
                        startWFDirectGame();
                        break;
                    case 3: //Wifi
                        startWFGame();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO idk maybe something pretty?
            }
        });
    }
    private void setAISpinner() {
        //AI Adapter
        ArrayAdapter<CharSequence> aiAdapter = ArrayAdapter.createFromResource(this,R.array.ai,android.R.layout.simple_spinner_item);
        aiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ai.setAdapter(aiAdapter);
        ai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ( position ) {
                    case 0: //name: Play AI
                        break;
                    case 1: //Smart
                        game.addSmartStrategy();
                        startGame();
                        break;
                    case 2: //Random
                        game.addRandomStrategy();
                        startGame();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO idk maybe something pretty?
            }
        });
    }

    /* Bluetooth Functionality */
    private boolean BTenabled() {
        //checks if BT is on
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if ( btAdapter!=null && btAdapter.isEnabled() )
            return true;
        return false;
    }
    private void turnOnBT() {
        //creates a window alert: user permission to turn on BT
        Intent intent = new Intent(BluetoothAdapter.getDefaultAdapter().ACTION_REQUEST_ENABLE);
        startActivity(intent);
    }
    private void startBTGame() {
        //activity (settings): connects to bluetooth
        if ( !BTenabled() )
            turnOnBT();
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        if ( btAdapter.getProfileConnectionState(BluetoothProfile.A2DP)==btAdapter.STATE_CONNECTED )
            startGame();
        else createTryAgainDialog("Bluetooth not connected!","Try Again","Cancel");
    }

    /* Wifi Functionality */
    private boolean WFenabled() {
        //checks if WF is on
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return m.isWifiEnabled();
    }
    private void turnOnWF() {
        //creates a window alert: user permission to turn on WF
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
    private void startWFGame() {
        //activity (settings): connects to wifi
        if ( !WFenabled() )
            turnOnWF();
        //fixme it jumps straight to error dialog before turnOnWF()
        /*
        ConnectivityManager m = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //deprecated
        if ( wifi.isConnected() ) startGame();*/
        // /*
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = m.getConnectionInfo();
        if ( wifi.getNetworkId()!=-1 ) startGame(); //*/
        else createTryAgainDialog("Wifi not connected!","Try Again","Cancel");
    }

    /* Wifi Direct Functionality */
    private void startWFDirectGame() {
        //todo extra credit boi
    }

    /* Creates a dialog for user confirmation */
    private void createTryAgainDialog(String msg, String positive, String negative) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setP2PSpinner(); //fixme what do when user wants to try connecting again?
                    }
                });
        alertDialogBuilder.setNegativeButton(negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        recreate(); //fixme what do when user doesn't want to connect?
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();}

    /* Starts the Battleship Game */
    private void startGame() {
        Intent intent = new Intent(getApplicationContext(),HumanDeployActivity.class);
        startActivity(intent);
    }
}
