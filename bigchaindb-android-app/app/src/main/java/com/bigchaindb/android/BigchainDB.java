package com.bigchaindb.android;

import android.util.Log;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.Transaction;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;

import java.security.KeyPair;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * simple usage of BigchainDB Java driver (https://github.com/bigchaindb/java-bigchaindb-driver)
 * to create TXs on BigchainDB network
 * @author innoprenuer
 *
 */
public class BigchainDB {

    private static KeyPairGenerator edDsaKpg = new KeyPairGenerator();
    private static final String TAG = "BigchainDB";
    private static String userId = "";
    private static final KeyPair KEYS = edDsaKpg.generateKeyPair();
    private static final String bigchainDBNodeURL = "http://10.0.2.2:9984" ;
    private GenericCallback callback = null;

    public BigchainDB(GenericCallback callback){
        this.callback = callback;
    }

    /**
     * configures connection url and credentials
     */
    public void setConfig() {
        BigchainDbConfigBuilder
                .baseUrl(bigchainDBNodeURL) //or use http://testnet.bigchaindb.com
                .addToken("app_id", "")
                .addToken("app_key", "").setup();

    }

    public Transaction sendTransaction(String data) throws Exception {

        Log.d(TAG, "Setting configuration..");
        this.setConfig();
        Transaction transaction = null;

        //create asset data
        Map<String, String> assetData = new TreeMap<String, String>();
        assetData.put("data", data);

        //create asset metadata
        Map<String, String> metadata = new TreeMap<String, String>();
        metadata.put("lastModifiedOn", new Date().toString());


        //build and send CREATE transaction
        transaction = BigchainDbTransactionBuilder
                .init()
                .addAssets(assetData, TreeMap.class)
                .addMetaData(metadata)
                .operation(Operations.CREATE)
                .buildAndSign((EdDSAPublicKey) KEYS.getPublic(), (EdDSAPrivateKey) KEYS.getPrivate())
                .sendTransaction(this.callback);

        Log.d(TAG, "(*) Transaction successfully sent.. - " + transaction.getId());


        return transaction;

    }




}