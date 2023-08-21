package com.example.firebasetrain

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.permission_denied_alert.view.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class shareActivity : AppCompatActivity() {

    var chosenImage: Uri? = null
    var chosenBitmap : Bitmap? = null

    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database :FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()



        sharePhoto.setOnClickListener {
            sharePhotoAction()
        }
        sharePost.setOnClickListener {
            sharePostAction()
        }
    }

    private fun sharePostAction() {
        //UUID ->Universal unique id
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference =storage.reference

        val imageReference = reference.child("images").child(imageName)

        if (chosenImage !=null){
            imageReference.putFile(chosenImage!!).addOnSuccessListener { taskSnapshot ->
                val uploadImageReference =storage.reference.child("images").child(imageName)
                uploadImageReference.downloadUrl.addOnSuccessListener {uri->

                    val imageUri=uri.toString()
                    val currUserEmail=auth.currentUser!!.email.toString()
                    val shareComment = shareComment.text.toString()
                    val date = Timestamp.now()



                    val postHashMap= hashMapOf<String,Any>()
                    postHashMap.put("image_url",imageUri)
                    postHashMap.put("user_mail",currUserEmail)
                    postHashMap.put("post_comment",shareComment)
                    postHashMap.put("post_date",date)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Toast.makeText(this,"Photo shared successfully.",Toast.LENGTH_SHORT).show()
                            val intent =Intent(this,feed::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }.addOnFailureListener{ exception->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }



                }.addOnFailureListener{exception->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }




        }

    }

    private fun sharePhotoAction() {
        askPermission()

    }

    private fun askPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),1)

        }
        else{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

            startActivityForResult(chooserIntent, 2)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

                startActivityForResult(chooserIntent, 2)
            }
            else{
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
                val view = inflater.inflate(R.layout.permission_denied_alert, null)
                builder.setView(view)
                val alert = builder.create()
                alert.show()
                view.ok.setOnClickListener {
                    alert.dismiss()
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.data != null) {
                    // Photo selected from the gallery
                    chosenImage = data.data
                    try {
                        chosenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, chosenImage)
                        sharePhoto.setImageBitmap(chosenBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // Photo captured from the camera
                    val thumbnailBitmap = data.extras?.get("data") as? Bitmap
                    if (thumbnailBitmap != null) {
                        chosenBitmap = thumbnailBitmap
                        sharePhoto.setImageBitmap(chosenBitmap)

                        // Save the camera image to a file
                        chosenImage = saveFullSizedCameraImage(thumbnailBitmap)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveFullSizedCameraImage(bitmap: Bitmap): Uri? {
        val imageFile = File(externalCacheDir, "temp_image.jpg")
        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1000, outputStream)
            outputStream.flush()
            outputStream.close()
            return Uri.fromFile(imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}