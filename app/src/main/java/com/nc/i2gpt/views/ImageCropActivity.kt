package com.nc.i2gpt.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nc.i2gpt.R


class ImageCropActivity : AppCompatActivity() {

    private lateinit var imageBitmap: Bitmap
    private lateinit var imageView: CropImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlight_text)
        imageView = findViewById(R.id.iv_photo)

        findViewById<Button>(R.id.btn_done).setOnClickListener {
            val cropped: Bitmap? = imageView.getCroppedImage()
            val image = InputImage.fromBitmap(cropped!!, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image).addOnSuccessListener {
                val returnedIntent = Intent().putExtra("TEXT", it.text)
                setResult(TEXT_RESULT_CODE, returnedIntent)
                finish()
            }.addOnFailureListener {
                Log.d("TAG", "onCreate: ")
            }
        }
        findViewById<Button>(R.id.btn_recapture).setOnClickListener {
            //todo
        }
        openCamera()
    }


    private fun openCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }
    }

    private val cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
//            val imageBitmap = result.data?.extras?.get("data") as Bitmap
//            imageView.setImageBitmap(imageBitmap)

            imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.text)
            imageView.setImageBitmap(imageBitmap)

        } else {
            Log.d("TAG", "openCamera: else")
        }
    }


    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
        const val TEXT_RESULT_CODE = 1
    }






}