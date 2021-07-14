package com.example.quranreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class PageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)
        var myImageView=findViewById<ImageView>(R.id.image)
        Picasso.get().load("https://quran-images-api.herokuapp.com/show/page/${intent.getIntExtra("page",0)}").into(myImageView)
    }
}