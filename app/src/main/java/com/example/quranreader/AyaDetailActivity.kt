package com.example.quranreader

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.quranreader.data.Ayah
import com.example.quranreader.data.Quran
import com.example.quranreader.data.RetrofitService
import com.example.quranreader.data.RoomService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AyaDetailActivity : AppCompatActivity() {
    private var audio: String? = null
    //private lateinit var numPageView :TextView
    private var page:Int=0
    var ayaid:String? = ""
    private lateinit var translationView :TextView
    private lateinit var tafsirView :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aya_detail)

        var ayahView = findViewById<TextView>(R.id.aya)
        var idAyahView = findViewById<TextView>(R.id.id_ayah)
        var numSouratView = findViewById<TextView>(R.id.num_sourat)
       // var numAyahView = findViewById<TextView>(R.id.num_ayah)
        var readersSpinner = findViewById<Spinner>(R.id.reader_spinner)
        var translationsSpinner = findViewById<Spinner>(R.id.translation_spinner)
        var tafsirsSpinner = findViewById<Spinner>(R.id.tafsir_spinner)
        var nbMotsView = findViewById<TextView>(R.id.nb_mots)
        var button = findViewById<TextView>(R.id.button5)


        var fabPlay = findViewById<FloatingActionButton>(R.id.fabPlay)

        var fabStop = findViewById<FloatingActionButton>(R.id.fabStop)
        fabStop.visibility = View.GONE

        var aya:Quran


        var ch=intent.getStringExtra("idAya")
        ayaid=ch


        aya= RoomService.getDataBase(applicationContext).getQuranDAO().getAyaById(ch)
        ayahView.text=aya.texteAya
        idAyahView.text=ch

        aya.isVisited=1
        RoomService.getDataBase(applicationContext).getQuranDAO().updathistory(aya)


        numSouratView.text=aya.idSourat.toString()
        //numAyahView.text=aya.numAya.toString()
        nbMotsView.text=aya.nbWord.toString()
        getAudioAndPage (ch,"1")
        getTranslation (ch,"20")
        getTafsir (ch,"90")
        button.setOnClickListener {
            val intent = Intent(this, PageActivity::class.java)
            intent.putExtra("page",page)
            startActivity(intent)
        }

        tafsirsSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position==0){

                    getTafsir (ch,"90")
                }else if(position==1){
                    getTafsir(ch,"91")
                }else if(position==2){
                    getTafsir(ch,"92")
                }else if(position==3){
                    getTafsir (ch,"93")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        translationsSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position==0){

                    getTranslation (ch,"131")
                }else if(position==1){
                    getTranslation (ch,"149")
                }else if(position==2){
                    getTranslation (ch,"20")
                }else if(position==3){
                    getTranslation (ch,"167")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }




        readersSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getAudioAndPage (ch,(position+2).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val mediaPlayer = MediaPlayer()
        fabPlay.setOnClickListener {
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setDataSource("https://verses.quran.com/"+audio)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            fabPlay.visibility = View.GONE
            fabStop.visibility = View.VISIBLE
        }

        fabStop.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            fabPlay.visibility = View.VISIBLE
            fabStop.visibility = View.GONE
        }
    }
    fun getAudioAndPage (ayaKey:String?,readerKey:String){
        var call =RetrofitService.endpoint.getDoctor(ayaKey,readerKey)
        call!!.enqueue(object : Callback<Ayah> {
            override fun onResponse(call: Call<Ayah>, response: Response<Ayah>) {
                if (response.isSuccessful) {
                    var ayah= response.body()!!

                    // numPageView = findViewById<TextView>(R.id.num_page)
                    page=ayah.verse.page_number
                    //numPageView.text=ayah.verse.page_number.toString()

                    audio=ayah.verse.audio.url

                }

            }

            override fun onFailure(call: Call<Ayah>, t: Throwable) {

                println(t.message)

            }
        })
    }

    fun getTranslation (ayaKey:String?,translatiobKey:String){
        var call =RetrofitService.endpoint.getTranslation(ayaKey,translatiobKey)
        call!!.enqueue(object : Callback<Ayah> {
            override fun onResponse(call: Call<Ayah>, response: Response<Ayah>) {
                if (response.isSuccessful) {
                    var ayah= response.body()!!

                     translationView = findViewById<TextView>(R.id.translation)

                    translationView.text= Html.fromHtml( ayah.verse.translations.get(0).text)


                }

            }

            override fun onFailure(call: Call<Ayah>, t: Throwable) {

                println(t.message)

            }
        })
    }


    fun getTafsir (ayaKey:String?,tafsirKey:String){
        var call =RetrofitService.endpoint.getTafsir(ayaKey,tafsirKey)
        call!!.enqueue(object : Callback<Ayah> {
            override fun onResponse(call: Call<Ayah>, response: Response<Ayah>) {
                if (response.isSuccessful) {
                    var ayah= response.body()!!

                   tafsirView = findViewById<TextView>(R.id.tafsir)
                    tafsirView.text= Html.fromHtml( ayah.verse.tafsirs.get(0).text)


                }

            }

            override fun onFailure(call: Call<Ayah>, t: Throwable) {

                println(t.message)

            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu_favoris, menu)


        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_favoris -> {


                print("the fav string is  ${ayaid}")
                Log.i("meeeesage debug", "favoris action ${ayaid}")

                showDialog(ayaid)

                Log.i("meeeesage debug", "favoris action finnnn ${ayaid}")
                return true
            }
            R.id.ACTION_SEND -> {

            print("the fav string is  ${ayaid}")
            Log.i("meeeesage debug", "favoris action ${ayaid}")

                showDialogSend(ayaid)

            Log.i("meeeesage debug", "favoris action finnnn ${ayaid}")
            return true
        }
            else ->{ return super.onOptionsItemSelected(item) }
        }

    }

    private fun showDialog(id:String?) {
        var aya:Quran

        print("the fav string is  ${id}")
        aya= RoomService.getDataBase(applicationContext).getQuranDAO().getAyaById(id)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_favoris)
        val body = dialog.findViewById(R.id.ajouter_note) as TextView

        val yesBtn = dialog.findViewById(R.id.enregistrer) as Button

        yesBtn.setOnClickListener {
            aya= RoomService.getDataBase(applicationContext).getQuranDAO().getAyaById(id)


            Log.i("meeeesage debug", "this the fav strin id aya ${aya.idAya}")
            aya.notefav=body.text.toString()
            aya.isfav=1
            RoomService.getDataBase(applicationContext).getQuranDAO().updatfavAyah(aya)
            Log.i("meeeesage debug", "this the fav texte note ${aya.notefav}")
            Log.i("meeeesage debug", "this the fav texte note ${aya.isfav.toString()}")

            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showDialogSend(id: String?) {
        var aya:Quran

        print("the fav string is  ${id}")
        aya= RoomService.getDataBase(applicationContext).getQuranDAO().getAyaById(id)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activitymail)
        val body = dialog.findViewById(R.id.recipientEt) as TextView

        val yesBtn = dialog.findViewById(R.id.sendEmailBtn) as Button

        yesBtn.setOnClickListener {
            aya= RoomService.getDataBase(applicationContext).getQuranDAO().getAyaById(id)

            sendEmail(body.toString(), aya.texteAya.toString())
            Log.i("meeeesage debug", "this the fav strin id aya ${aya.idAya}")
            aya.notefav=body.text.toString()
            aya.isfav=1
            RoomService.getDataBase(applicationContext).getQuranDAO().updatfavAyah(aya)
            Log.i("meeeesage debug", "this the fav texte note ${aya.notefav}")
            Log.i("meeeesage debug", "this the fav texte note ${aya.isfav.toString()}")

            dialog.dismiss()
        }

        dialog.show()}


    private fun sendEmail(recipient: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent

        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message

        }



    }
}