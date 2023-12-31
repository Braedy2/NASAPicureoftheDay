package com.example.nasapictureoftheday

import android.app.WallpaperManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.nasapictureoftheday.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date


//private var urlString: String = "https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT&date=$pictureDate"
//private var urlString: String = "https://api.nasa.gov/planetary/apod?api_key=etdCoqn82TVIIBC9kcnhJZoJjALrw9ZbwfMegtbT"

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel:MyViewModel
    private lateinit var binding: ActivityMainBinding

    val db = Firebase.firestore
    var currentDay:APIFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // viewmodel
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        var apiObserver = Observer<APIFormat> {
            newValue ->
            currentDay = newValue
            val photoFrag: PhotoFragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as PhotoFragment?
            photoFrag?.change(newValue)
        }

        viewModel.currentDay.observe(this, apiObserver)

        binding.buttonLogout.setOnClickListener {
            // logs out of Firebase
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,R.string.logged_out, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.btnInfo.setOnClickListener {
            startActivity(Intent(this,InfoActivity::class.java))
        }

        binding.btnFav.setOnClickListener{
            startActivity(Intent(this,ViewFavsActivity::class.java))
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
        CoroutineScope(Dispatchers.Main/* + coroutineExceptionHandler*/).launch {
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
                binding.textViewError.text = ""
                viewModel.updateDay(request)
            }
        }
    }

    private suspend fun getStreamFromURL(url:String): InputStream {
        val defer = CoroutineScope(Dispatchers.IO).async {
            return@async URL(url).openStream()
        }
        return defer.await()
    }

    fun setWallpaper(pic: String) {
        if(pic != null) {
            var wallpaperManager = WallpaperManager.getInstance(applicationContext)

            CoroutineScope(Dispatchers.IO/* + coroutineExceptionHandler*/).launch {
                val request = getStreamFromURL(pic)
                if (request != null) {
                    try {
                        wallpaperManager.setStream(request)
                        //Toast.makeText(applicationContext,"Successfully set wallpaper", Toast.LENGTH_SHORT).show()
                    } catch (e:Exception) {
                        //Toast.makeText(applicationContext,"Error setting wallpaper" + e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun addToFaves(newDay: APIFormat?) {
        if (newDay != null) {
            var dayToAdd:APIFormat = newDay!!
            db.collection("favouritePhotos").document(dayToAdd.date)
                .set(dayToAdd, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    Toast.makeText(this,"Successfully added photo to favourites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    e -> Log.w(TAG, "Error writing document", e)
                    Toast.makeText(this,"Error adding to favourites" + e, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val decoded: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            if (decoded != null && decoded.isNotEmpty()) {
                bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                println(bitmap)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
    fun onReturn(view: View) {
        finish()
    }
}