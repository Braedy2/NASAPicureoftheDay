package com.example.nasapictureoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //api call when app is started
        CoroutineScope(Dispatchers.Main).launch {
            val request = getPictureData()
        }
    }

    private suspend fun getPictureData():APIFormat? {
        val defer = CoroutineScope(Dispatchers.IO).async {
            val url = URL("https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT&start_date=2023-10-31&end_date=2023-11-27")
            println(url.toString())
            val connection = url.openConnection() as HttpsURLConnection
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
}