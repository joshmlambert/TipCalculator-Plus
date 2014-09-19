package com.sticktaplabs.tipcalculatorplus;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;


public class TipCalculatorPlus extends Activity {

    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String TIP_AMOUNT = "TIP_AMOUNT";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private static final int SEEK_BAR_MULTIPLE = 3;

    private double billBeforeTip;
    private double tipPercent;
    private double tipAmount;
    private double finalBill;

    private EditText billBeforeTipET;
    private EditText tipPercentET;
    private EditText tipAmountET;
    private EditText finalBillET;

    private SeekBar tipChangeSeekBar;

    private CheckBox roundTipUpCB;
    private CheckBox roundBillUpCB;

    private Switch flashlightSwitch;
    private static Camera cam = null;

    //private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator_plus);

        //init amounts either from scratch or from a saved state
        if(savedInstanceState == null) {
            billBeforeTip = 0.0;
            tipPercent = .18;
            tipAmount = 0.0;
            finalBill = 0.0;

        } else {
            billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipPercent = savedInstanceState.getDouble(CURRENT_TIP);
            tipAmount = savedInstanceState.getDouble(TIP_AMOUNT);
            finalBill = savedInstanceState.getDouble(TOTAL_BILL);
        }

        //init edit text boxes
        billBeforeTipET = (EditText) findViewById(R.id.billEditText);
        tipPercentET = (EditText) findViewById(R.id.tipEditText);
        tipAmountET = (EditText) findViewById(R.id.tipAmountEditText);
        finalBillET = (EditText) findViewById(R.id.finalBillEditText);

        //addTextChangedListener
        billBeforeTipET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    billBeforeTip = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    billBeforeTip = 0.0;
                }

                updateTipAndFinalBill();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tipPercentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    tipPercent = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    tipPercent = 0.0;
                }

                updateTipAndFinalBill();
            }
        });


        //init seek bar
        tipChangeSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);

        tipChangeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tipPercent = (progress / SEEK_BAR_MULTIPLE)*.01 ;
                tipPercentET.setText(String.format("%.02f", tipPercent));

                updateTipAndFinalBill();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        //init checkboxes
        roundTipUpCB = (CheckBox) findViewById(R.id.roundTipCheckBox);
        roundBillUpCB = (CheckBox) findViewById(R.id.roundBillCheckBox);

        roundTipUpCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //uncheck the other box
                    roundBillUpCB.setChecked(false);
                }
                updateTipAndFinalBill();
            }
        });

        roundBillUpCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //uncheck the other box
                    roundTipUpCB.setChecked(false);
                }
                updateTipAndFinalBill();
            }
        });


        flashlightSwitch = (Switch) findViewById(R.id.flashlightSwitch);
        flashlightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //check if flash is available
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                        //TODO turn on flashlight
                        cam = Camera.open();
                        Camera.Parameters p = cam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                    } else {
                        flashlightSwitch.setChecked(false);
                    }
                } else {
                    //turn off
                    cam.stopPreview();
                    cam.release();
                }
            }
        });

    }

    private void updateTipAndFinalBill() {

        double roundingAmount = 0;

        double finalBill = billBeforeTip + (billBeforeTip * tipPercent);
        double tipAmount = billBeforeTip * tipPercent;

        if (roundBillUpCB.isChecked()) {
            roundingAmount = 1 - (finalBill % 1.0);
        } else if (roundTipUpCB.isChecked()) {
            roundingAmount = 1 - (tipAmount % 1.0);
        }

        finalBill += roundingAmount;
        tipAmount += roundingAmount;

        finalBillET.setText(String.format("%.02f", finalBill));
        tipAmountET.setText(String.format("%.02f", tipAmount));
    }

    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putDouble(TOTAL_BILL, finalBill);
        outState.putDouble(TIP_AMOUNT, tipAmount);
        outState.putDouble(CURRENT_TIP, tipPercent);
        outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tip_calculator_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
