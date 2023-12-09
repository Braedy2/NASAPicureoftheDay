package com.example.nasapictureoftheday

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val dataSet: MutableList<APIFormat>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewDate: TextView
        var textViewExplanation: TextView
        var textViewAuthor: TextView
        var imageViewPicture: ImageView
        init {
            textViewDate = view.findViewById(R.id.textViewDate)
            textViewExplanation = view.findViewById(R.id.textViewDescription)
            textViewAuthor = view.findViewById(R.id.textViewAuthor)
            imageViewPicture = view.findViewById(R.id.imageViewPicture)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Fragment", "Created viewHolder")
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_photo, viewGroup, false)
        val lp = view.layoutParams
        lp.height = 512
        view.layoutParams = lp

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d("Fragment", dataSet[position].date)
        viewHolder.textViewDate.text = dataSet[position].date
        viewHolder.textViewExplanation.text = dataSet[position].explanation
        viewHolder.textViewAuthor.text = dataSet[position].copyright
        Picasso.get().load(dataSet[position].url).into(viewHolder.imageViewPicture)
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount():Int {
        return dataSet.size
    }

}
