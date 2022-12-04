package com.example.crystalfiles.testing

import android.content.Context
import android.widget.Toast
import androidx.annotation.NonNull

fun toast(@NonNull context: Context,
          @NonNull text: String = "HALLEN",
          duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(context, text, duration).show()
}
