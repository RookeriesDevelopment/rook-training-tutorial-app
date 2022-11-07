package com.rookmotion.rooktraining.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.rookmotion.rooktraining.R

object Dialogs {

    fun showStopTrainingDialog(
        context: Context,
        cancelTraining: () -> Unit,
        finishTraining: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.what_do_you_want_to_do)
            .setMessage(R.string.theres_a_training_running)
            .setNeutralButton(R.string.dismiss) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_training) { dialog, _ ->
                dialog.dismiss()
                cancelTraining()
            }
            .setPositiveButton(R.string.finish_training) { dialog, _ ->
                dialog.dismiss()
                finishTraining()
            }
            .show()
    }
}