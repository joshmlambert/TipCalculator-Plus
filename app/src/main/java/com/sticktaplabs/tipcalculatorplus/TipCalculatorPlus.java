package com.sticktaplabs.tipcalculatorplus;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class TipCalculatorPlus extends Activity {

    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String TIP_AMOUNT = "TIP_AMOUNT";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private double billBeforeTip;
    private double tipPercent;
    private double tipAmount;
    private double finalBill;

    EditText billBeforeTipET;
    EditText tipPercentET;
    EditText tipAmountET;
    EditText finalBillET;


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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    tipPercent = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    tipPercent = 0.0;
                }

                updateTipAndFinalBill();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void updateTipAndFinalBill() {
        double finalBill = billBeforeTip + (billBeforeTip * tipPercent);
        finalBillET.setText(String.format("%.02f", finalBill));

        double tipAmount = billBeforeTip * tipPercent;
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
