package com.example.nasapictureoftheday

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nasapictureoftheday.databinding.ActivityViewFavsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewFavsActivity : AppCompatActivity() {
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityViewFavsBinding
    val db = Firebase.firestore
    private lateinit var myFavPhotos: MutableList<APIFormat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_favs)

        binding.btnHome.setOnClickListener{
            finish()
        }

        recyclerViewManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = recyclerViewManager
        binding.recyclerView.setHasFixedSize(true)

        // get the favourites
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("ViewFavsActivity", "pulling favourite photos")
            val request = getFavourites()
            if (request != null) {
                myFavPhotos = request
                Log.d("ViewFavsActivity", "updating fragment list")
                updateUI(request)
            }
        }
    }

    private suspend fun getFavourites(): MutableList<APIFormat> {
        var tempList = mutableListOf<APIFormat>(APIFormat("", "", "","",""))
        tempList.clear()
        val defer = CoroutineScope(Dispatchers.IO).async {
            val queryReturn = db.collection("favouritePhotos")
                .get().await()

            for (document in queryReturn.documents) {
                val newDay:APIFormat = APIFormat(
                    document.get("copyright")!!.toString(),
                    document.get("date")!!.toString(),
                    document.get("title")!!.toString(),
                    document.get("url")!!.toString(),
                    document.get("explanation")!!.toString(),
                )
                Log.d("Firestore", document.toString())
                tempList.add(newDay)
            }
            return@async tempList
        }
        return defer.await()
    }

    private fun updateUI(request:MutableList<APIFormat>) {
        runOnUiThread {
            kotlin.run {
                Log.w("UI THREAD", "Updating the recycler view")
                binding.recyclerView.adapter = RecyclerAdapter(request)
            }
        }
    }
}