package com.example.nasapictureoftheday

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nasapictureoftheday.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

//private var urlString: String = "https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT&date=$pictureDate"
//private var urlString: String = "https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT"

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = recyclerViewManager
        binding.recyclerView.setHasFixedSize(true)

        binding.buttonLogout.setOnClickListener {
            // logs out of Firebase
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,R.string.logged_out, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        //api call when app is started
        callAPI("initial");
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    private fun callAPI(pictureDate: String) {
        var date: String = pictureDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd");
        //val currDate = dateFormat.format(Date());
        if (pictureDate == "initial") {
            date = dateFormat.format(Date())
        }
        CoroutineScope(Dispatchers.IO/* + coroutineExceptionHandler*/).launch {
            //get current day to load todays picture

            val request = getPictureData(date)

            if (request != null) {
                updateUI(request)
            }
            else {
                binding.textViewError.text = getString(R.string.reqFail)
            }
        }
    }

    private suspend fun getPictureData(pictureDate:String):APIFormat? {
        val defer = CoroutineScope(Dispatchers.IO).async {
            val url = URL("https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT&date=$pictureDate")
            println(url.toString())
            val connection = url.openConnection() as HttpURLConnection
            println(connection)
            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, APIFormat::class.java)

                println("Request")
                println(request.toString())

                inputStreamReader.close()
                inputSystem.close()
                return@async request
            }
            else {
                return@async null
            }
        }
        return defer.await()
    }

    private fun updateUI(request:APIFormat) {
        runOnUiThread {
            kotlin.run {
                binding.textViewError.text = getString(R.string.success)
                binding.recyclerView.adapter = RecyclerAdapter(request!!)
            }
        }
    }

    fun singleDayPicture(view: View) {

    }
}