package com.example.nasapictureoftheday

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nasapictureoftheday.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToHome.setOnClickListener {
            finish()
        }

        binding.txtGithub.setOnClickListener {
            val url = findViewById<TextView>(R.id.txtGitLink).text.toString()
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
            startActivity(urlIntent)
        }
    }
}