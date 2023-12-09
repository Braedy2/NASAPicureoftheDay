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
            val request = getFavourites()
            if (request != null) {
                updateUI(request)
                myFavPhotos = request
            }
        }
    }

    private suspend fun getFavourites(): MutableList<APIFormat> {
        var tempList = mutableListOf<APIFormat>(APIFormat("", "", "","",""))
        tempList.clear()

        val defer = CoroutineScope(Dispatchers.IO).async {
            var gotData = false
            db.collection("favouritePhotos")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val newDay:APIFormat = APIFormat(
                            document.data.get("copyright")!!.toString(),
                            document.data.get("date")!!.toString(),
                            document.data.get("title")!!.toString(),
                            document.data.get("url")!!.toString(),
                            document.data.get("explanation")!!.toString(),
                        )
                        tempList.add(newDay)

                        Log.d("Firestore", document.id)
                        Log.d("Firestore", document.data.get("date")!!.toString())
                        Log.d("Firestore", document.data.get("title")!!.toString())
                        Log.d("Firestore", document.data.get("url")!!.toString())
                    }
                    Log.w("Firestore", "Got Data")
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents.", exception)
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