package com.rookmotion.rooktraining

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as RookTrainingApplication)
            .rmServiceLocator
            .rm
            .getUserStatusFromApi("daniel.nolasco@rookmotion.com") { rmResponse, userStatus ->
                Toast.makeText(
                    this,
                    "Success: ${rmResponse.isSuccess} Message: ${rmResponse.message} Code: ${rmResponse.code}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}