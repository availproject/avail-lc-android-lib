package com.example.availlibrary

import android.util.Log
import com.example.availlibrary.models.BlockConfidence
import com.example.availlibrary.models.LatestBlock
import com.example.availlibrary.models.StatusV1
import com.example.availlibrary.models.StatusV2
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ForkJoinPool

class AvailLightClientLib(config: String, appId: Int) {
    interface CallbackFunction {
        fun invoke(value: String)
    }

    private var config: String
    private var appId: Int
    private var lcRunning: Boolean = false;
    private var gson: Gson = Gson();
    public var callbacks = Callbacks()

    init {
        this.appId = appId
        this.config = config
        System.loadLibrary("avail_light");
        startLightClient({})
        val resp = getStatusV2()
        Log.e("RESP", resp.toString())
        val data = "{\"data\": \"VGVzdCBkYXRhYWE=\"}"

        val privateKey =
            "pact source double stadium tourist lake skill ginger scatter age strike purpose"
        val resp2 = submitTransaction(data, privateKey)
        Log.e("RESP2", resp2.toString())

    }

    fun startLightClient(onStop: () -> Unit) {
        ForkJoinPool.commonPool().submit {
            val resp = startNode(config)
        }
    }

    fun getLatestBlock(): LatestBlock? {
        return try {
            val response = latestBlock(config).toString();
            Log.e("Resp entry:", response)
            gson.fromJson(response, LatestBlock::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null;
        }
    }

    fun geStatus(): StatusV1? {
        return try {
            val response = status(config, appId).toString();
            gson.fromJson(response, StatusV1::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null
        }
    }

    fun getConfidence(block: Int): BlockConfidence? {
        return try {
            val response = confidence(config, block).toString();
            gson.fromJson(response, BlockConfidence::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null;
        }
    }

    fun getStatusV2(): StatusV2? {
        return try {
            val response = getStatusV2(config).toString();
            gson.fromJson(response, StatusV2::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null;
        }
    }

    fun submitTransaction(transaction: String, privateKey: String): String {
        return try {
            val response = submitTransaction(
                config, appId, transaction.toString(), privateKey.toString()
            );
            response.toString()
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            ""
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun startLightClientWithCallback(callback: CallbackFunction, onStop: () -> Void) {
        lcRunning = true;
        val thread = GlobalScope.launch {

            val lightNodeCoroutine = launch {
                // Your code to run in the background thread
                startLightNodeWithCallback(config, callback)
            }
        }
        thread.start();
        thread.invokeOnCompletion {
            onStop();
            lcRunning = false;
        }

    }

    fun onStop() {}

    private external fun getStatusV2(cfg: String): String
    private external fun submitTransaction(
        cfg: String, appId: Int, transaction: String, privateKey: String
    ): String

    private external fun startLightNodeWithCallback(
        cfg: String, callback: CallbackFunction
    ): String

    // v1 Calls
    private external fun startNode(cfg: String): String
    private external fun confidence(cfg: String, block: Int): String
    private external fun status(cfg: String, appId: Int): String
    private external fun latestBlock(cfg: String): String
    private external fun startNodeWithCallback(cfg: String, callback: Callbacks): String

}