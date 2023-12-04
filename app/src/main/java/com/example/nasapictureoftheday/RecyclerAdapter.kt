package com.example.nasapictureoftheday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class RecyclerAdapter(private val dataSet: APIFormat) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var textViewDate: TextView
        lateinit var textViewExplanation: TextView
        lateinit var textViewAuthor: TextView
        lateinit var imageViewPicture: ImageView
        init {
            textViewDate = view.findViewById(R.id.textViewDate)
            textViewExplanation = view.findViewById(R.id.textViewDescription)
            textViewAuthor = view.findViewById(R.id.textViewAuthor)
            imageViewPicture = view.findViewById(R.id.imageViewPicture)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.picture_display, viewGroup, false)

        val lp = view.layoutParams
        lp.height = 512
        view.layoutParams = lp

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewDate.text = dataSet.date.toString()
        viewHolder.textViewExplanation.text = dataSet.explanation.toString()
        viewHolder.textViewAuthor.text = dataSet.copyright.toString()
        Picasso.get().load(dataSet.url.toString()).into(viewHolder.imageViewPicture)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount():Int {
        return 1
    }

}