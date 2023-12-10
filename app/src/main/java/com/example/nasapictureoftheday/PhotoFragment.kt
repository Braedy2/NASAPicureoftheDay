package com.example.nasapictureoftheday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var title:TextView? = null
    var photo:ImageView? = null
    //var dateLabel:TextView? = null
    var date:TextView? = null
    var author:TextView? = null
    var desc:TextView? = null
    var btn: Button? = null

    var btnFav:Button? = null

    var day:APIFormat = APIFormat("", "", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_photo, container, false)

        title = view.findViewById(R.id.ImageTitleText)
        photo = view.findViewById(R.id.imageViewPicture)
        date = view.findViewById(R.id.textViewDate)
        author = view.findViewById(R.id.textViewAuthor)
        desc = view.findViewById(R.id.textViewDescription)

        btn = view.findViewById(R.id.button)
        btnFav = view.findViewById(R.id.btnAddToFav)

        btn!!.setOnClickListener {
            callSetWallpaper()
        }
        btnFav!!.setOnClickListener {
            (activity as MainActivity).addToFaves(day)
        }
        return view
    }

    fun callSetWallpaper() {
        (activity as MainActivity).setWallpaper(day.url)
    }

    fun change(newDay:APIFormat) {
        if (newDay.url != "") {
            Picasso.get().load(newDay.url).into(photo!!)
            day = newDay
        }
        title!!.text = newDay.title
        //dateLabel!!.text = newDay.date
        date!!.text = newDay.date
        author!!.text = newDay.copyright
        if (newDay.explanation.length > 450) {
            desc!!.text = newDay.explanation.substring(0, 450) + ". . ."
        }
        else {
            desc!!.text = newDay.explanation
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}