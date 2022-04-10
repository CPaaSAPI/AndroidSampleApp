package com.example.cpaas_android_sample_app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), InfoFragment.GoToCallViewListener {

    private val MY_PERMISSIONS_RECORD_AUDIO = 1
    private lateinit var mainModel: MainViewModel
    private val CALL_FRAGMENT_TAG = "CALL_FRAGMENT"
    private val INFO_FRAGMENT_TAG = "INFO_FRAGMENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestAudioPermissions() // Audio permission REQUIRED for call functionality

        registerViewModel()
        goToInfoFragment()
    }

    private fun registerViewModel() {
        // main model sends message and we show it on screen
        mainModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainModel.message.observe(this) {
            runOnUiThread {
                Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun goToCallView() {
        var fragment = CallFragment.newInstance()

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.root, fragment, CALL_FRAGMENT_TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun goToInfoFragment() {
        // show call UI fragment
        var fragment = InfoFragment.newInstance()

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.root, fragment, INFO_FRAGMENT_TAG)
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
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG)
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