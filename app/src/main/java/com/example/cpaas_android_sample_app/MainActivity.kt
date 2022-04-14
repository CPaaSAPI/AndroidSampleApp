package com.example.cpaas_android_sample_app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.cpaasapi.sdk.api.CPaaSAPICb
import com.cpaasapi.sdk.api.ICall

/**
 * Main Activity gave as sample code for using CPaaS API in order to establish a voice call
 * The main activity UI contains a panel that allows the user to make such a call.
 */
class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1
    private lateinit var cPaaSModel: CPaaSViewModel
    private val CALL_FRAGMENT_TAG = "CALL_FRAGMENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermissions() // Audio permission REQUIRED for call functionality

        registerViewModel()
        setView()
    }

    private fun setView() {
        findViewById<Button>(R.id.btn_register).setOnClickListener {
            onRegisterToCpaasPressed()
        }
        findViewById<Button>(R.id.btn_call).setOnClickListener {
            onStartCallPressed()
        }
    }

    private fun onRegisterToCpaasPressed() {
        cPaaSModel.onRegisterToCpaasPressed(object : CPaaSAPICb {
            override fun onIncomingCall(call: ICall) {
                goToCallView()
            }

            override fun onRegistrationComplete(success: Boolean) {
                //Register complete
            }
        })
    }

    private fun onStartCallPressed() {
        cPaaSModel.onStartCallPressed("DESTINATION_ID")
        goToCallView()
    }

    private fun registerViewModel() {
        // main model sends message and we show it on screen
        cPaaSModel = ViewModelProvider(this).get(CPaaSViewModel::class.java)
        cPaaSModel.message.observe(this) {
            runOnUiThread {
                Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goToCallView() {
        var fragment = CallFragment.newInstance()

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.root, fragment, CALL_FRAGMENT_TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                Toast.makeText(this, getString(R.string.permission_msg), Toast.LENGTH_LONG)
                    .show()

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            }
        }
    }

}