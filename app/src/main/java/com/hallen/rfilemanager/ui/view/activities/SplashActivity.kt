package com.hallen.rfilemanager.ui.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.BuildConfig
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.iconFolderName
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var prefs: Prefs

    // Launcher for the result of the activity started to request manage all files access
    private val requestManageAllFilesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // All files access granted, proceed to MainActivity
                    navigateToMainActivity()
                } else {
                    // All files access denied, handle accordingly
                    Toast.makeText(this, "All files access is required.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Launcher for the result of requesting storage permissions
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Handle permission grant results
            val grantedPermissions = permissions.entries.filter { it.value }.map { it.key }
            if (grantedPermissions.containsAll(
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            ) {
                // All required permissions granted, proceed to MainActivity
                navigateToMainActivity()
            } else {
                // Some or all permissions denied, handle accordingly
                Toast.makeText(this, "Storage permissions are required.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            checkDefaultIcon()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
        // Check permissions and launch the appropriate request flows
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                requestManageAllFilesLauncher.launch(intent)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            // Permissions already granted, proceed to MainActivity
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the current activity to prevent going back
    }

    private fun checkDefaultIcon() {
        val usedIconPack = prefs.getUsedIconPack()
        if (usedIconPack != null) return
        val iconFolder = File(filesDir, iconFolderName)
        iconFolder.mkdirs()
        try {
            assets.open("WhiteSur-green.zip").use { inputStream ->
                unzip(inputStream, iconFolder)
            }
        } catch (e: Exception) {
            prefs.setUsedIconPack("WhiteSur-green")
        }
    }

    private fun unzip(inputStream: InputStream, destinationDir: File) {
        if (!destinationDir.isDirectory) {
            Logger.i("Destination is not a directory.")
            return
        }
        if (destinationDir.exists() && destinationDir.listFiles()?.isNotEmpty() == true) {
            Logger.i("Destination is not a directory.")
            return
        }
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry?
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val targetFile = File(destinationDir, zipEntry!!.name)
                if (zipEntry!!.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    targetFile.parentFile?.mkdirs()
                    FileOutputStream(targetFile).use { outputStream ->
                        zipInputStream.copyTo(outputStream)
                    }
                }
            }
        }
    }
}