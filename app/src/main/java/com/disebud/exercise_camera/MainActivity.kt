package com.disebud.exercise_camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val OPEN_CAMERA_REQUEST_CODE = 99
    val SELECT_FILE_FROM_STORAGE = 66 // OPEN_STORAGE
    val REQUEST_READ_STORAGE_PERMISSION = 201 // READ

    lateinit var currentPhotoPath: String
    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkStoragePermission()
    }

    fun openCamera(view: View) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.resolveActivity(packageManager)

        photoFile = createImageFile()
        val photoURI =
            FileProvider.getUriForFile(this,"com.disebud.exercise_camera.FileProvider", photoFile)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        startActivityForResult(cameraIntent, OPEN_CAMERA_REQUEST_CODE) // membuka activity camera
    }

    fun browseFile(view: View) {
        val selectFileIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(selectFileIntent, SELECT_FILE_FROM_STORAGE)
    }

    // startActivityForResult  akan bermuara onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == OPEN_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(imageBitmap)

        } else if(requestCode == SELECT_FILE_FROM_STORAGE && resultCode == Activity.RESULT_OK){
            println("DARI STORAGE....")
            println(data?.data.toString())
            println(data?.data?.path)


//            imageView.setImageURI(uriFile)
//            photoFile = uriFile?.toFile()!!
            val uriFile = data?.data
            val originalPath = getOriginalPathFromUri(uriFile!!)
            println(originalPath)

            //ini file nya bisa diupload
            val imageFile = File(originalPath)

            val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(imageBitmap)

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            println(currentPhotoPath)
        }
    }

    fun getOriginalPathFromUri(contentUri: Uri): String?{
        var originalPath: String? = null
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor?.moveToFirst()!!) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            originalPath = cursor.getString(columnIndex)
        }
        return originalPath
    }

   fun checkStoragePermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_READ_STORAGE_PERMISSION
            )
        }
    }

}


