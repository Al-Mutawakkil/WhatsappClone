package com.tugas.whatsappclone.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tugas.whatsappclone.R
import com.tugas.whatsappclone.util.*

import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference // mengakses firebase
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId.isNullOrEmpty()){
            finish()
        }
        progress_layout.setOnTouchListener { v, event -> true }

        btn_apply.setOnClickListener {
            onApply()
        }

        btn_delete_account.setOnClickListener{
            onDelete()
        }

        imbtn_profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        userInfo()
    }

    private fun userInfo() {
        progress_layout.visibility = View.VISIBLE
        firebaseDb.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)

                imageUrl = user?.imageUrl
                edt_name_profile.setText(user?.name, TextView.BufferType.EDITABLE)
                edt_email_profile.setText(user?.email, TextView.BufferType.EDITABLE)
                edt_phone_profile.setText(user?.phone, TextView.BufferType.EDITABLE)
                if (imageUrl != null) {
                    populateImage(this, user?.imageUrl!!, img_profile, R.drawable.ic_user)
                }

                progress_layout.visibility = View.GONE
            }.addOnFailureListener {e ->
                e.printStackTrace()
                finish()
            }

    }

    private fun onApply() {
        progress_layout.visibility = View.VISIBLE
        val name = edt_name_profile.text.toString()
        val email = edt_email_profile.text.toString()
        val phone = edt_phone_profile.text.toString() // data text dalam EditText akan diubah
        val map = HashMap<String, Any>() // menjadi String lalu ditampung di variabel
         map[DATA_USER_NAME] = name // yang nantinya di koleksi oleh HashMap
         map[DATA_USER_EMAIL] = email // untuk kemudian dikirimkan ke table user
         map[DATA_USER_PHONE] = phone // di database Firebase sebagai pembaruan

        firebaseDb.collection(DATA_USERS).document(userId!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                progress_layout.visibility = View.GONE

            }
    }

    private fun onDelete() {
        progress_layout.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This will delete your Profile Information. Are you sure?")
            .setPositiveButton("Yes") {dialog, which ->

                firebaseDb.collection(DATA_USERS).document(userId!!).delete()
                firebaseStorage.child(DATA_IMAGES).child(userId).delete()
                firebaseAuth.currentUser?.delete()// perintah menghapus userId yang sedang digunakan
                    ?.addOnSuccessListener {
                        finish()
                    }
                    ?.addOnFailureListener {
                        finish()
                    }


                progress_layout.visibility = View.GONE
                Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("No"){dialog, which ->
                progress_layout.visibility = View.GONE
            }
            .setCancelable(false)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)        }
    }

    private fun storeImage(data: Uri?) {
        if (data != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            progress_layout.visibility = View.VISIBLE
            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)

            filePath.putFile(data)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {
                        val url = it.toString()
                        firebaseDb.collection(DATA_USERS)
                            .document(userId)
                            .update(DATA_USER_IMAGE_URL, url)
                            .addOnSuccessListener {
                                imageUrl = url
                                populateImage(this, imageUrl!!, img_profile, R.drawable.ic_user)
                            }
                        progress_layout.visibility = View.GONE
                    }.addOnFailureListener {
                            onUploadFailured()
                        }
                }.addOnFailureListener {
                    onUploadFailured()
                }
        }
    }

    private fun onUploadFailured() {
        Toast.makeText(this, "Image upload failed. Please try again later.",
            Toast.LENGTH_SHORT).show()
        progress_layout.visibility = View.GONE
    }
}