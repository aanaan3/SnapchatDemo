package com.aanaan3.snapchatdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aanaan3.snapchatdemo.databinding.ActivityCreateSnapBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    lateinit var mbinding:ActivityCreateSnapBinding

    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    fun nextClicked(view: View) {
        // Get the data from an ImageView as bytes
        mbinding.imageView.isDrawingCacheEnabled = true
        mbinding.imageView.buildDrawingCache()
        val bitmap = (mbinding.imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"UploadFailed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            val downloadUrl = FirebaseStorage.getInstance().getReference().child("images").child(imageName).downloadUrl

            val intent = Intent(this,ChooseUserActivity::class.java)
            intent.putExtra("imageURL",downloadUrl.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",mbinding.etMassage.text.toString())
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                mbinding.imageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }
}