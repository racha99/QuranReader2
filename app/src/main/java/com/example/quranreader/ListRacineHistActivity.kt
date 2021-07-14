package com.example.quranreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quranreader.data.Quran
import com.example.quranreader.data.RoomService
import com.example.quranreader.recyclerListAyatOfRacine.ListAyatRacineAdapter

class ListRacineHistActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var adapter: ListAyatRacineAdapter
    lateinit var quran: MutableList<Quran>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_aya)

        val recyclerView = findViewById(R.id.racine_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        //val ayas2 = mutableListOf<Ayah>()

        quran= RoomService.getDataBase(applicationContext).getQuranDAO().getVisitedRacine().toMutableList()
        println("Favvv ${quran}")

        adapter=ListAyatRacineAdapter(quran, this)
        recyclerView.adapter = adapter






    }





    private fun showAyaDetail(i: String) {
        val intent = Intent(this, AyaDetailActivity::class.java)
        intent.putExtra("idAya",i)

        startActivity(intent)

    }

    override fun onClick(v: View?) {

        if (v != null) {
            if (v.tag != null) {
                println("maranish null")

                showAyaDetail(v.tag as String)
                println("maranish null ou dkhalt l ayah")

            }
        }
        else{
            println("rani null")
        }

    }
}