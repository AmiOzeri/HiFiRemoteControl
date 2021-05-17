package com.example.hifiremotecontrol;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    final boolean DEBUG = false;

    ImageButton button_volumeUP;
    ImageButton button_volumeDown;
    ImageButton button_power;
    Switch switch_volumeMute;
    RadioGroup radio_spkSelection;
    Vibrator vibrator;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    TextView textViewState;
    String IPAddr = "10.100.102.183";
    Integer Port = 4555;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_power =  (ImageButton) findViewById(R.id.imageButtonPower);
        button_volumeUP = (ImageButton) findViewById(R.id.imageButtonVolUp);
        button_volumeDown = (ImageButton) findViewById(R.id.imageButtonVolDown);
        switch_volumeMute = (Switch) findViewById(R.id.switchVolMute);
        radio_spkSelection= (RadioGroup) findViewById(R.id.radioGroupSPK);

        textViewState = (TextView)findViewById(R.id.state);
        button_volumeUP.setOnClickListener(button_volumeUPOnClickListener);
        button_volumeDown.setOnClickListener(button_volumeDownOnClickListener);
        switch_volumeMute.setOnClickListener(switch_volumeMuteOnClickListener);
        button_power.setOnClickListener(switch_powerOnClickListener);


        udpClientHandler = new UdpClientHandler(this);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    View.OnClickListener button_volumeUPOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    udpClientThread = new UdpClientThread(IPAddr,Port,"VOL__UP",udpClientHandler);
                    udpClientThread.start();
                    //vibrator.vibrate(100);

        //            button_volumeUP.setEnabled(false);
                }
            };

    View.OnClickListener button_volumeDownOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    udpClientThread = new UdpClientThread(IPAddr,Port,"VOL_DWN",udpClientHandler);
                    udpClientThread.start();
                   //vibrator.vibrate(100);
                    //            button_volumeUP.setEnabled(false);
                }
            };

    View.OnClickListener switch_powerOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    udpClientThread = new UdpClientThread(IPAddr,Port,"AMP_PWR",udpClientHandler);
                    udpClientThread.start();
                    //vibrator.vibrate(100);
                    //            button_volumeUP.setEnabled(false);
                }
            };

    View.OnClickListener switch_volumeMuteOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    udpClientThread = new UdpClientThread(IPAddr,Port,"VOL_MUT",udpClientHandler);
                    udpClientThread.start();
                    //            button_volumeUP.setEnabled(false);
                    if (switch_volumeMute.isChecked()) {
                        switch_volumeMute.setChecked(true);
                    } else {
                        switch_volumeMute.setChecked(false);
                    }



                }
            };
    public void OnSpeakerSelection (View view) {

        String spkSel = "";
        switch(radio_spkSelection.getCheckedRadioButtonId()){
            case R.id.radioBothSpk:
                spkSel = "SPK_BTH";
                break;
            case R.id.radioExtSpk:
                spkSel = "SPK_OUT";
                break;
            case R.id.radioIntSpk:
                spkSel= "SPK__IN";
                break;
       }
        udpClientThread = new UdpClientThread(IPAddr,Port,spkSel,udpClientHandler);
        udpClientThread.start();
        //vibrator.vibrate(100);
        //            button_volumeUP.setEnabled(false);
    };


    private void updateState(String state){
        if (DEBUG) {
            textViewState.append(state);
        }
    }

    private void updateRxMsg(String rxmsg){
       // textViewRx.append(rxmsg + "\n");
    }

    private void clientEnd(){
        udpClientThread = null;
        if (DEBUG) {
            textViewState.append("   clientEnd()   ");
        }
      //  editTextGetPass.setText("");
    //    button_volumeUP.setEnabled(true);

    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }
}
