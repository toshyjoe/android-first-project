@file:Suppress("DEPRECATION")

package ch.hearc.masrad.swisssearch

import android.content.Intent
import android.os.AsyncTask

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
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
    data class Entry(val title: String?)
    inner class Download : AsyncTask<String, Void, String>() {



        @Throws(XmlPullParserException::class, IOException::class)
        fun skip(parser: XmlPullParser) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                throw IllegalStateException()
            }
            var depth = 1
            while (depth != 0) {
                when (parser.next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }

        @Throws(IOException::class, XmlPullParserException::class)
        fun readText(parser: XmlPullParser): String {
            var result = ""
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.text
                parser.nextTag()
            }
            return result
        }

        @Throws(IOException::class, XmlPullParserException::class)
        fun readTitle(parser: XmlPullParser): String {
            parser.require(XmlPullParser.START_TAG, null, "title")
            val title = readText(parser)
            parser.require(XmlPullParser.END_TAG, null, "title")
            return title
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun readEntry(parser: XmlPullParser): Entry {
            parser.require(XmlPullParser.START_TAG, null, "entry")
            var title: String? = null
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                when (parser.name) {
                    "title" -> title = readTitle(parser)
                    else -> skip(parser)
                }
            }
            return Entry(title)
        }


        @Throws(XmlPullParserException::class, IOException::class)
        fun readFeed(parser: XmlPullParser): List<Entry> {
            val entries = mutableListOf<Entry>()

            parser.require(XmlPullParser.START_TAG, null, "feed")
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                // Starts by looking for the entry tag
                if (parser.name == "entry") {
                    entries.add(readEntry(parser))
                } else {
                    skip(parser)
                }
            }
            return entries
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun parse(inputStream: InputStream): List<*> {
            inputStream.use { inputStream ->
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputStream, null)
                parser.nextTag()
                Log.i("TAG", "MainActivity::doInBackground => pars ")
                return readFeed(parser)
            }
        }


        override fun doInBackground(vararg p0: String?): String {


            var url: URL
            val httpURLConnection: HttpURLConnection





            url = URL(p0[0])
            Log.i("TAG", "MainActivity::doInBackground => url " + url)
            httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream


            parse(inputStream)


            return "hello"



        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {

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
}


