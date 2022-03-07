package com.example.writereadlocalfileapp

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*

class MainActivity : AppCompatActivity() {
    var mEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mEditText = findViewById(R.id.edit_text)
    }

    fun save(v: View?) {
        val text = mEditText!!.text.toString()
        var fos: FileOutputStream? = null
        try {
            ////////////////////////////////////////////////
            // Para escribir un archivo en carpeta Descargas (CUIDADO: No todos los dispositivos tienen
            // permisos para hacerlo o bien tampoco dan la posibilidad de descargar en esta carpeta (Ej: Dispositivo Zebra))
//            var baseFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//
//            val string = "Contenido de archivo en carpeta Descargas"
//            val file = File(baseFolder + File.separator.toString() + "archivo_descargas.txt")
//            file.parentFile.mkdirs()
//
//            var fos2 = FileOutputStream(file)
//            fos2.write(string.toByteArray())
//            fos2.flush()
//            fos2.close()
            //////////////////////////////////////////////

            fos = openFileOutput(FILE_NAME, MODE_PRIVATE)
            fos.write(text.toByteArray())
            mEditText!!.text.clear()
            Toast.makeText(this, "Saved to " + filesDir + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show()
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

    companion object {
        private const val FILE_NAME = "example.txt"
    }
}