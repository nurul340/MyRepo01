package com.nc.i2gpt.views
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nc.i2gpt.R
import com.nc.i2gpt.api.ApiService
import com.nc.i2gpt.api.RetrofitClient
import com.nc.i2gpt.models.ChatModel
import com.nc.i2gpt.models.RequestBody
import com.nc.i2gpt.utils.afterTextChanged
import com.nc.i2gpt.utils.toHHmma
import com.nc.i2gpt.views.adapters.ChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*



class MainActivity : AppCompatActivity() {

    private var isMeRandom = false // IsMe flag random

    private var chatMessages: ArrayList<Any> = ArrayList()  // Chat Array
    private var chatAdapter: ChatAdapter? = null            // Chat Adapter

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerView) }
    private val tvMessage by lazy { findViewById<EditText>(R.id.tvMessage) }
    private val btnSend by lazy { findViewById<ImageView>(R.id.btnSend) }

    private var enableTextEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create and assign adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatAdapter = ChatAdapter(this@MainActivity, chatMessages)
        recyclerView.adapter = chatAdapter

//        tvMessage.afterTextChanged {
//            btnSend.visibility = if (tvMessage.text.trim().isNotEmpty()) View.VISIBLE else View.INVISIBLE
//        }

        btnSend.setOnClickListener {
            isMeRandom = !isMeRandom
            sendMessage(ChatModel(tvMessage.text.trim().toString(), Calendar.getInstance().toHHmma(), isMeRandom, true))
            tvMessage.text = null
        }

        findViewById<View>(R.id.btn_image).setOnClickListener {
            textResult.launch(Intent(this, ImageCropActivity::class.java))
        }
    }

    private fun sendMessage(chatMessage: Any) {
        chatMessages.add(chatMessage)
        chatAdapter!!.notifyItemInserted(chatMessages.size - 1)
        Handler(Looper.getMainLooper()).postDelayed({ recyclerView.scrollToPosition(chatMessages.size - 1) }, 100)
        sendMessage()
    }


    private var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private fun sendMessage(){
        isLoading.postValue(true)

        val retrofit = RetrofitClient.getClient()
        val apiInterface = retrofit.create(ApiService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = RequestBody("text-davinci-003",tvMessage.text.toString(),100,
                    0f, 1f,0, 0, "\n")
                val generatedAnswer = apiInterface.getPrompt(requestBody)
                Log.d("TAG", "response: "+ Gson().toJson(generatedAnswer))
                sendMessage(ChatModel(tvMessage.text.trim().toString(), Calendar.getInstance().toHHmma(), isMeRandom, true))
            }catch (e: Exception){
                Log.d("TAG", "sendMessage: "+ e.message)
                e.printStackTrace()
            }
        }

    }

    private val textResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val resultText = it.data?.getStringExtra("TEXT")!!
        Log.d("i2d", resultText)
        if(enableTextEdit){
            tvMessage.setText(resultText)
        }else {
            sendMessage(
                ChatModel(
                    resultText,
                    Calendar.getInstance().toHHmma(),
                    isMeRandom,
                    true
                )
            )
        }
        }



}