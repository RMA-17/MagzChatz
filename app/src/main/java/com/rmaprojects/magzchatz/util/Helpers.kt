package com.rmaprojects.magzchatz.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackBar(bindingRoot: View, message: String, duration: Int) {
    Snackbar.make(
        bindingRoot,
        message,
        duration
    ).show()
}