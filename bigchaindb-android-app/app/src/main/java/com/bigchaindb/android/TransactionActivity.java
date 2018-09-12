package com.bigchaindb.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sourcey.android.R;

import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";
    private static final int REQUEST_SIGNUP = 0;
    BigchainDB bigchainDBApi = new BigchainDB(handleServerResponse());
    int SUCCESS_CODE = 1;
    @BindView(R.id.input_tx) EditText _txText;
    @BindView(R.id.btn_login) Button _txButton;
    @BindView(R.id.tx_response) TextView _txRespText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);
        
        _txButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                send();
            }
        });

    }

    public void send() {
        Log.d(TAG, "Sending Transaction");

        if (!validate()) {
            onSendFailed();
            return;
        }

        _txButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(TransactionActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("sending tx to BigchainDB...");
        progressDialog.show();

        String txText = _txText.getText().toString();
        Transaction sentTx = null;
        try{
            sentTx = bigchainDBApi.sendTransaction(txText);
        } catch (ConnectException ex){
            //set error code
            SUCCESS_CODE = -2;
        } catch (Exception e){
            //set error code
            SUCCESS_CODE = -3;
        }

        Log.d(TAG, sentTx.toString());
        final Transaction tx = sentTx;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.d(TAG, "Success code - " + SUCCESS_CODE);
                        while(SUCCESS_CODE == 1){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Still waiting with code - " + SUCCESS_CODE);
                        }
                        if(SUCCESS_CODE == 0){
                            onSendSuccess(tx);
                        }
                        else {
                            onSendFailed();
                        }

                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onSendSuccess(Transaction successfulTx) {
        _txButton.setEnabled(true);

        Toast.makeText(getBaseContext(), "Transaction successful", Toast.LENGTH_LONG).show();
        JsonObject jsonObject = new JsonParser().parse(successfulTx.toString()).getAsJsonObject();
        _txRespText.setText(toPrettyFormat(jsonObject.toString()));
        //set success code to intial state
        SUCCESS_CODE = 1;
    }

    public void onSendFailed() {
        Log.d(TAG, "Transaction failed. Success code - " + SUCCESS_CODE);
        String msg = "";
        if(SUCCESS_CODE == -2){
            msg = "Couldn't connect to BigchainDB";
            SUCCESS_CODE = 1;
        }
        else if(SUCCESS_CODE == -3){
            msg = "Some error occurred. Exiting";
        }
        else {
            msg ="Transaction Failed";
            SUCCESS_CODE = 1;
        }

        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        if(SUCCESS_CODE == -3){
            finishAffinity();
            System.exit(0);
        }

        _txButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String tx = _txText.getText().toString();

        if (tx.isEmpty()) {
            _txText.setError("type a message to send");
            valid = false;
        } else {
            _txText.setError(null);
        }

        return valid;
    }

    private GenericCallback handleServerResponse() {
        //define callback methods to verify response from BigchainDBServer
        GenericCallback callback = new GenericCallback() {

            @Override
            public void transactionMalformed(Response response) {
                Log.d(TAG, "malformed " + response.message());
                onFailure();
            }

            @Override
            public void pushedSuccessfully(Response response) {
                Log.d(TAG, "pushedSuccessfully");
                onSuccess(response);
            }

            @Override
            public void otherError(Response response) {
                Log.d(TAG, "otherError" + response.message());
                onFailure();
            }
        };

        return callback;
    }

    private void onSuccess(Response response) {
        SUCCESS_CODE = 0;
        Log.d(TAG, "(*) Transaction successfully committed..");
        Log.d(TAG, response.toString());
    }

    private void onFailure() {
        SUCCESS_CODE = -1;
        Log.d(TAG, "Transaction failed");
        Log.d(TAG, "Transaction failed");
    }

    /**
     * Convert a JSON string to pretty print version
     * @param jsonString
     * @return
     */
    public static String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

}
