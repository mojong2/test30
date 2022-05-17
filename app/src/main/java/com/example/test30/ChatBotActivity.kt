package com.example.test30

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibm.cloud.sdk.core.http.Response
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import com.ibm.watson.assistant.v2.Assistant
import com.ibm.watson.assistant.v2.model.CreateSessionOptions
import com.ibm.watson.assistant.v2.model.MessageInput
import com.ibm.watson.assistant.v2.model.MessageOptions
import com.ibm.watson.assistant.v2.model.SessionResponse
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType
import com.ibm.watson.speech_to_text.v1.SpeechToText
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback
import com.ibm.watson.text_to_speech.v1.TextToSpeech
import kotlinx.android.synthetic.main.content_chat_room.*
import java.io.InputStream
import java.util.*


class ChatBotActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var btnRecord: ImageButton? = null
    private var initialRequest = false
    private var permissionToRecordAccepted = false
    private var listening = false
    var inputMessage = ""
    private var capture: MicrophoneInputStream? = null
    private var mContext: Context? = null
    private var microphoneHelper: MicrophoneHelper? = null
    private var watsonAssistant: Assistant? = null
    private var watsonAssistantSession: Response<SessionResponse>? = null
    private var speechService: SpeechToText? = null
    private var textToSpeech: TextToSpeech? = null

    lateinit var mAdapter: ChatAdapter
    var messageArrayList = ArrayList<Message>()

    private fun createServices() {
        watsonAssistant = Assistant(
            "2019-02-28", IamAuthenticator(
                mContext!!.getString(R.string.assistant_apikey)
            )
        )
        watsonAssistant!!.serviceUrl = mContext!!.getString(R.string.assistant_url)
        textToSpeech = TextToSpeech(IamAuthenticator(mContext!!.getString(R.string.TTS_apikey)))
        textToSpeech!!.serviceUrl = mContext!!.getString(R.string.TTS_url)
        speechService = SpeechToText(IamAuthenticator(mContext!!.getString(R.string.STT_apikey)))
        speechService!!.serviceUrl = mContext!!.getString(R.string.STT_url)

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbot_main)
        mContext = applicationContext
        val customFont = "Montserrat-Regular.ttf"
        val typeface = Typeface.createFromAsset(assets, customFont)
        recyclerView = findViewById(R.id.recycler_view)
        messageArrayList = ArrayList<Message>()
        mAdapter = ChatAdapter(messageArrayList)
        microphoneHelper = MicrophoneHelper(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView!!.setLayoutManager(layoutManager)
        recyclerView!!.setItemAnimator(DefaultItemAnimator())
        recyclerView!!.setAdapter(mAdapter)
        initialRequest = true


        createServices()
        sendMessage()
        Log.d("섹스","머신")
        tts = android.speech.tts.TextToSpeech(this) { status ->
            if (status != android.speech.tts.TextToSpeech.ERROR) {
                // 언어를 선택한다.
                tts!!.language = Locale.KOREAN
            }
        }

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        } else {
            Log.i(TAG, "Permission to record was already granted")
        }

        recyclerView!!.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if(child !=null){
                    onClick(child,rv.getChildPosition(child))
                }
                return false
            }
            fun onLongPress(rv: RecyclerView, e: MotionEvent) {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    onLongClick(child, rv.getChildPosition(child))
                }
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
            fun onClick(view: View?, position: Int) {
                val audioMessage = messageArrayList.get(position) as Message
                if (audioMessage != null && !audioMessage.message!!.isEmpty()) {
                    ttsSpeak(audioMessage.message.toString())
                }
            }
            fun onLongClick(view: View?, position: Int) {
                recordMessage()
            }
        })
//        recyclerView!!.addOnItemTouchListener(
//            RecyclerTouchListener(
//                applicationContext,
//                recyclerView!!,
//                object : ClickListener {
//                    override fun onClick(view: View?, position: Int) {
//                        val audioMessage = messageArrayList.get(position) as Message
//                        if (audioMessage != null && !audioMessage.message!!.isEmpty()) {
//                            ChatBotActivity().SayTask().doInBackground(audioMessage.message.toString())
//                        }
//                    }
//
//                    override fun onLongClick(view: View?, position: Int) {
//                        recordMessage()
//                    }
//                })
//        )

        btn_send.setOnClickListener(View.OnClickListener {
            if (checkInternetConnection()) {
                sendMessage()
            }
        })
        btn_record.setOnClickListener(View.OnClickListener { recordMessage() })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                finish()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Speech-to-Text Record Audio permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted =
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            RECORD_REQUEST_CODE -> {
                if (grantResults.size == 0
                    || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
                return
            }
            MicrophoneHelper.REQUEST_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        // if (!permissionToRecordAccepted ) finish();
    }

    protected fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO),
            MicrophoneHelper.REQUEST_PERMISSION
        )
    }

    // Sending a message to Watson Assistant Service
    private fun sendMessage() {
        inputMessage = message.getText().toString()
        var inputmessage = inputMessage.trim { it <= ' ' }
        if (!initialRequest) {
            val inputMessage = Message()
            inputMessage.message = inputmessage
            inputMessage.id = "1"
            messageArrayList!!.add(inputMessage)
        } else {
            val inputMessage = Message()
            inputMessage.message = inputmessage
            inputMessage.id = "100"
            initialRequest = false
            Toast.makeText(applicationContext, "Tap on the message for Voice", Toast.LENGTH_LONG)
                .show()
        }
        mAdapter!!.notifyDataSetChanged()
        val thread = Thread {
            try {
                if (watsonAssistantSession == null) {
                    val call = watsonAssistant!!.createSession(
                        CreateSessionOptions.Builder().assistantId(
                            mContext!!.getString(R.string.assistant_id)
                        ).build()
                    )
                    watsonAssistantSession = call.execute()
                }
                val input = MessageInput.Builder()
                    .text(inputmessage)
                    .build()
                val options = MessageOptions.Builder()
                    .assistantId(mContext!!.getString(R.string.assistant_id))
                    .input(input)
                    .sessionId(watsonAssistantSession!!.result.sessionId)
                    .build()
                val response = watsonAssistant!!.message(options).execute()
                Log.i(TAG, "run: " + response!!.result)
                if (response != null && response.result.output != null &&
                    !response.result.output.generic.isEmpty()
                ) {
                    val responses = response.result.output.generic
                    for (r in responses) {
                        var outMessage: Message
                        when (r.responseType()) {
                            "text" -> {
                                outMessage = Message()
                                outMessage.message = r.text()
                                outMessage.id = "2"
                                messageArrayList!!.add(outMessage)

                                // speak the message
                                ttsSpeak(outMessage.message.toString())
                            }
                            "option" -> {
                                outMessage = Message()
                                val title = r.title()
                                var OptionsOutput = ""
                                var i = 0
                                while (i < r.options().size) {
                                    val option = r.options()[i]
                                    OptionsOutput = """
                                          $OptionsOutput${option.label}
                                          
                                          """.trimIndent()
                                    i++
                                }
                                outMessage.message = """
                                      $title
                                      $OptionsOutput
                                      """.trimIndent()
                                outMessage.id = "2"
                                messageArrayList!!.add(outMessage)

                                // speak the message
                                ttsSpeak(outMessage.message.toString())
                            }
                            "image" -> {
                                outMessage = Message(r)
                                messageArrayList!!.add(outMessage)

                                // speak the description
                                ttsSpeak("You received an image: " + outMessage.title + outMessage.description)
                            }
                            else -> Log.e("Error", "Unhandled message type")
                        }
                    }
                    runOnUiThread {
                        mAdapter!!.notifyDataSetChanged()
                        if (mAdapter!!.itemCount > 1) {
                            recyclerView!!.layoutManager!!
                                .smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    mAdapter!!.itemCount - 1
                                )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        message.setText(null)
        thread.start()
    }

    //Record a message via Watson Speech to Text
    private fun recordMessage() {
        if (listening != true) {
            capture = microphoneHelper!!.getInputStream(true)
            Thread {
                try {
                    speechService!!.recognizeUsingWebSocket(
                        getRecognizeOptions(capture),
                        ChatBotActivity().MicrophoneRecognizeDelegate()
                    )
                } catch (e: Exception) {
                    showError(e)
                }
            }.start()
            listening = true
            Toast.makeText(this@ChatBotActivity, "Listening....Click to Stop", Toast.LENGTH_LONG).show()
        } else {
            try {
                microphoneHelper!!.closeInputStream()
                listening = false
                Toast.makeText(
                    this@ChatBotActivity,
                    "Stopped Listening....Click to Start",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Check Internet Connection
     *
     * @return
     */
    private fun checkInternetConnection(): Boolean {
        // get Connectivity Manager object to check connection
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting

        // Check for network connections
        return if (isConnected) {
            true
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show()
            false
        }
    }
    //Private Methods - Speech to Text
    private fun getRecognizeOptions(audio: InputStream?): RecognizeOptions {
        return RecognizeOptions.Builder()
            .audio(audio)
            .contentType(ContentType.OPUS.toString())
            .model("en-US_BroadbandModel")
            .interimResults(true)
            .inactivityTimeout(2000)
            .build()
    }

    fun showMicText(text: String) {
        runOnUiThread { message.setText(text) }
    }

    fun enableMicButton() {
        runOnUiThread { btnRecord!!.isEnabled = true }
    }

    fun showError(e: Exception) {
        runOnUiThread {
            e.printStackTrace()
        }
    }

    private var tts: android.speech.tts.TextToSpeech? = null
    private fun initTextToSpeech(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Toast.makeText(this,"SDK version is low", Toast.LENGTH_SHORT).show()
            return
        }
        tts = android.speech.tts.TextToSpeech(this) {
            if (it == android.speech.tts.TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "TTS setting successed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "TTS init failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun ttsSpeak(strTTS: String){
        tts?.speak(strTTS, android.speech.tts.TextToSpeech.QUEUE_ADD,null,null)
    }
//    inner class SayTask() {
//        fun doInBackground(vararg params: String): String {
////            Log.d("제발",params)
////            ttsSpeak(params.toString())
////            streamPlayer.playStream(
////                textToSpeech!!.synthesize(
////                    SynthesizeOptions.Builder()
////                        .text(params[0])
////                        .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
////                        .accept(HttpMediaType.AUDIO_WAV)
////                        .build()
////                ).execute().result
////            )
//            return "Did synthesize"
//        }
//    }

    //Watson Speech to Text Methods.
    inner class MicrophoneRecognizeDelegate : BaseRecognizeCallback() {
        override fun onTranscription(speechResults: SpeechRecognitionResults) {
            if (speechResults.results != null && !speechResults.results.isEmpty()) {
                val text = speechResults.results[0].alternatives[0].transcript
                showMicText(text)
            }
        }

        override fun onError(e: Exception) {
            showError(e)
            enableMicButton()
        }

        override fun onDisconnected() {
            enableMicButton()
        }
    }
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val TAG = "MainActivity"
        private const val RECORD_REQUEST_CODE = 101
    }
}
