package mn.turbo.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import mn.turbo.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private val permissionsToCheck: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonRequestReadWriteFiles.setOnClickListener(this@MainActivity)
            buttonRequestCameraPermission.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v) {
                buttonRequestCameraPermission -> {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        return
                    }

                    getCameraPermission.launch(Manifest.permission.CAMERA)
                }
                buttonRequestReadWriteFiles -> {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                        getReadWritePermission.launch(permissionsToCheck)
                        return
                    }

                    showMessage("File permissions not needed on API30 and newer for App Owned Files")
                }
            }
        }
    }

    //Permissions Launchers-------------------------------------------------------------------------
    private val getCameraPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission ->
            if (!hasPermission) {
                showMessage("Camera Permission has been denied.")
                return@registerForActivityResult
            }
            showMessage("Camera Permission has been given.")
        }

    private val getReadWritePermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
            permissionsMap.entries.forEach { mutableEntry ->
                showMessage(mutableEntry.key + ": hasPermission = " + mutableEntry.value)
            }
        }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}