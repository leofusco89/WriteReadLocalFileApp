package com.example.writereadlocalfileapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*

/**
 * Los primeros 2 metodos comentados son la explicación rápida de todos los casos
 * que abarcan el ejercicio, y en la activity en sí tenemos el código propio de estos métodos:
 *
 * Case 1: User doesn't have permission
 * Case 2: User has permission
 *
 * Case 3: User has never seen the permission Dialog
 * Case 4: User has denied permission once but he din't clicked on "Never Show again" check box
 * Case 5: User denied the permission and also clicked on the "Never Show again" check box.
 * Case 6: User has allowed the permission
 *
 */
//fun handlePermission() {
//    if (ContextCompat.checkSelfPermission(this@MainActivity,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        != PackageManager.PERMISSION_GRANTED) {
//        // This is Case 1. Now we need to check further if permission was shown before or not
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            // This is Case 4.
//        } else {
//            // This is Case 3. Request for permission here
//        }
//    } else {
//        // This is Case 2. You have permission now you can do anything related to it
//    }
//}
//
//fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>?, grantResults: IntArray) {
//    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        // This is Case 2 (Permission is now granted)
//    } else {
//        // This is Case 1 again as Permission is not granted by user
//
//        //Now further we check if used denied permanently or not
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            // case 4 User has denied permission but not permanently
//        } else {
//            // case 5. Permission denied permanently.
//            // You can open Permission setting's page from here now.
//        }
//    }
//}
class MainActivity : AppCompatActivity() {
    var mEditText: EditText? = null

    companion object {
        private const val FILE_NAME = "example.txt"
        private var permissionGranted = false
        const val PERMISSIONS = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mEditText = findViewById(R.id.edit_text)
    }

    override fun onResume() {
        super.onResume()
        //Get read and write permissions for external storage
        getPermissions()
    }

    private fun getPermissions() {
        if (!permissionGranted) {
            //Request permissions.The result of the permission requests are handled by a callback, onRequestPermissionsResult
            if ((ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
              || ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                //Ask user to grant permissions. Result is handled in onRequestPermissionsResult()
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                        PERMISSIONS)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true
                } else {
                    //Permission is not granted by user

                    //Now further we check if used denied permanently or not
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "Reiniciar aplicación y permitir permisos", Toast.LENGTH_LONG).show()

                    } else {
                        //Permission denied permanently.
                        Toast.makeText(this, "Error: Modificar permisos desde la configuración de la app", Toast.LENGTH_LONG).show()
                        Thread.sleep(3000)
                    }

                }
            }
        }
    }

    fun save(v: View?) {
        val text = mEditText!!.text.toString()
        var fos: FileOutputStream? = null
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE)
            fos.write(text.toByteArray())
            mEditText!!.text.clear()
            Toast.makeText(this, "Archivo guardado en " + filesDir + "/" + FILE_NAME, Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun load(v: View?) {
        var fis: FileInputStream? = null
        try {
            fis = openFileInput(FILE_NAME)
            val isr = InputStreamReader(fis)
            val br = BufferedReader(isr)
            val sb = StringBuilder()
            var text: String?
            while (br.readLine().also { text = it } != null) {
                sb.append(text).append("\n")
            }
            mEditText!!.setText(sb.toString())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun download(v: View?) {
        val text = mEditText!!.text.toString()
        var fos2: FileOutputStream? = null
        try {
            // Para escribir un archivo en carpeta Descargas (IMPORTANTE: En el emulador funciona
            // bien, pero para dispositivos conectados USB, hay que pedir los permisos en versión 29 o más)

            // Obtenemos path+file en carpeta Descargas
            var baseFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val file = File(baseFolder + File.separator.toString() + "archivo_descargas.txt")
            file.parentFile.mkdirs()

            fos2= FileOutputStream(file)
            fos2.write(text.toByteArray())
            fos2.flush()
            fos2.close()
            Toast.makeText(this, "Archivo creado/modificado: $file", Toast.LENGTH_LONG).show()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos2 != null) {
                try {
                    fos2.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun upload(v: View?) {
        var fis2: FileInputStream? = null
        try {
            // Obtenemos path+file en carpeta Descargas
            var baseFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val fileString = baseFolder + File.separator.toString() + "archivo_descargas.txt"
            fis2 = FileInputStream(fileString)
            val isr = InputStreamReader(fis2)
            val bufferedReader = BufferedReader(isr)
            val sb = java.lang.StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            mEditText!!.setText(sb.toString())
            bufferedReader.close()
            Toast.makeText(this, "Archivo cargado: $fileString", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fis2 != null) {
                try {
                    fis2.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}