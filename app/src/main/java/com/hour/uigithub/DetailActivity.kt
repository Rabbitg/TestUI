package com.hour.uigithub

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.hour.uigithub.fragment.ListFragment
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.main_rv_item.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val image = intent.getParcelableExtra<Category>(ListFragment.INTENT_PARCELABLE)

        val imgSrc = findViewById<ImageView>(R.id._imageDetail)
        val imgTitle = findViewById<TextView>(R.id._imageTitle)
        val imgDesc = findViewById<TextView>(R.id._imageDesc)

        imgSrc.setImageResource(image.photo)
        imgTitle.text = image.title

        exercise_btn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
