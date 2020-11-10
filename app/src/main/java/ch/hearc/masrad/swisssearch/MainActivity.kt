@file:Suppress("DEPRECATION")

package ch.hearc.masrad.swisssearch

import android.content.Intent
import android.os.AsyncTask

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL



class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mNameInput: EditText
    private lateinit var mSearchButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("TAG", "MainActivity::onCreate")

        mNameInput = findViewById<EditText>(R.id.activity_main_name_input)
        mSearchButton =  findViewById<Button>(R.id.activity_main_search_btn)


        mNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                mSearchButton.isEnabled = s.toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

    fun search (view: View){
        Log.i("TAG", "MainActivity::search")
        val downloadData = Download()
        try {
            val key = "&key=920b2b17d1aca9724a94b8799a2d8bec"
            val url = "https://tel.search.ch/api/?was="
            val chosenBase = mNameInput.text.toString()
            downloadData.execute(url+chosenBase+key)
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    inner class Download : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""
            var url: URL
            val httpURLConnection: HttpURLConnection

            try {

                url = URL(p0[0])
                httpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream = httpURLConnection.inputStream
                val inputStreamReader = InputStreamReader(inputStream)

                var data = inputStreamReader.read()

                while (data > 0) {
                    val character = data.toChar()
                    result += character
                    data = inputStreamReader.read()

                }

                return result
            } catch (e: Exception) {
                e.printStackTrace()
                return result

            }

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                Log.i("TAG", "MainActivity::onPostExecute")

                val ListActivity = Intent(this@MainActivity, ListActivity::class.java)
                startActivity(ListActivity)

                val jSONObject = JSONObject(result)
                println(jSONObject)
                val base = jSONObject.getString("base")
                println(base)
                val date = jSONObject.getString("date")
                println(date)
                val rates = jSONObject.getString("rates")
                println(rates)

                val newJsonObject = JSONObject(rates)
                val chf = newJsonObject.getString("CHF")
                println(chf)
                val czk = newJsonObject.getString("CZK")
                val tl = newJsonObject.getString("TRY")


            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }


    override fun onStart() {
        super.onStart()
        println("MainActivity::onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("MainActivity::onResume()")
    }

    override fun onPause() {
        super.onPause()
        println("MainActivity::onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("MainActivity::onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MainActivity::onDestroy()")
    }
}