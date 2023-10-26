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




class AvailLightClientLib(config: String, appId: Int) {
    interface CallbackFunction {
        fun invoke(value: ByteArray)
    }

    private var config: ByteArray
    private var appId: Int
    private var lcRunning: Boolean = false;
    private var gson: Gson;

    init {
        this.config = config.toByteArray()
        this.appId = appId
        System.loadLibrary("avail_light");
        gson = Gson();
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun startLightClient(onStop: () -> Unit) {
        lcRunning = true;
        val thread = GlobalScope.launch {

            val lightNodeCoroutine = launch {

                // Your code to run in the background thread
                startLightNode(config)
            }
        }
        thread.start();
        thread.invokeOnCompletion {
            onStop();
            lcRunning = false;
        }

    }

    fun getLatestBlock(): LatestBlock? {
        return try {
            val response = latestBlock(config).toString();
            gson.fromJson(response, LatestBlock::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null;
        }
    }

    fun geStatus(): StatusV1? {
        return try {
            val response = status(appId, config).toString();
            gson.fromJson(response, StatusV1::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null
        }
    }

    fun getConfidence(block: Int): BlockConfidence? {
        return try {
            val response = confidence(block, config).toString();
            gson.fromJson(response, BlockConfidence::class.java)
        } catch (e: Exception) {
            Log.e("Latest Block.kt Error", e.toString());
            return null;
        }
    }

    fun getStatusV2(block: Int): StatusV2? {
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
                config,
                appId,
                transaction.toByteArray(),
                privateKey.toByteArray()
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

    private external fun startLightNode(cfg: ByteArray)
    private external fun latestBlock(cfg: ByteArray): ByteArray
    private external fun status(appId: Int, cfg: ByteArray): ByteArray
    private external fun confidence(block: Int, cfg: ByteArray): ByteArray
    private external fun getStatusV2(cfg: ByteArray): ByteArray
    private external fun submitTransaction(
        cfg: ByteArray,
        appId: Int,
        transaction: ByteArray,
        privateKey: ByteArray
    ): ByteArray

    private external fun startLightNodeWithCallback(
        cfg: ByteArray,
        callback: CallbackFunction
    ): ByteArray

}