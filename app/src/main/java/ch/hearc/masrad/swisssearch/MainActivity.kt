@file:Suppress("DEPRECATION")

package ch.hearc.masrad.swisssearch

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
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

        var listNames = ArrayList<String>()

        @Throws(XmlPullParserException::class, IOException::class)
        fun skip(parser: XmlPullParser) {
            Log.i("TAG", "MainActivity::doInBackground => skip ")
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
            Log.i("TAG", "MainActivity::doInBackground => readText ")

            var result: String = ""

            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.text
                listNames.add(parser.text)
                Log.i("TAG", "MainActivity::doInBackground => readText var " + result)
                parser.nextTag()
            }
            //return result
            Log.i("TAG", "MainActivity::doInBackground => readText listName " + listNames)
            return result

        }

        @Throws(IOException::class, XmlPullParserException::class)
        fun readTitle(parser: XmlPullParser): String {
            Log.i("TAG", "MainActivity::doInBackground => readTitle ")
            parser.require(XmlPullParser.START_TAG, null, "title")
            val title = readText(parser)
            Log.i("TAG", "MainActivity::doInBackground => readTitle val " + title)
            parser.require(XmlPullParser.END_TAG, null, "title")
            return title
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun readEntry(parser: XmlPullParser): Entry {
            Log.i("TAG", "MainActivity::doInBackground => readEntry ")
            Log.i("TAG", "MainActivity::doInBackground => readEntry::parse " + parser)

            parser.require(XmlPullParser.START_TAG, null, "entry")
            var title: String? = null
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::while " + parser.name)
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                when (parser.name) {
                    "title" -> title = readTitle(parser)
                    else -> skip(parser)

                }
                Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::when " + title)
            }
            return Entry(title)

        }


        @Throws(XmlPullParserException::class, IOException::class)
        fun readFeed(parser: XmlPullParser): List<Entry> {
            val entries = mutableListOf<Entry>()

            Log.i("TAG", "MainActivity::doInBackground => readFeed " + entries)
            Log.i("TAG", "MainActivity::doInBackground => readFeed::parser " + parser)
            parser.require(XmlPullParser.START_TAG, null, "feed")
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i("TAG", "MainActivity::doInBackground => readFeed::parser::while " + parser.name)
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                // Starts by looking for the entry tag
                if (parser.name == "entry") {
                    entries.add(readEntry(parser))
                    Log.i("TAG", "MainActivity::doInBackground => readFeed::parser::while => Tag =  " + parser.name)
                    Log.i("TAG", "MainActivity::doInBackground => readFeed::parser::while =>  " + entries)
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
                Log.i("TAG", "MainActivity::doInBackground => parse " + inputStream)
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
            Log.i("TAG", "MainActivity::doInBackground => inputStream " + inputStream)
            parse(inputStream)

            return ""


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)



            Log.i("TAG", "MainActivity::onPostExecute " + listNames)


            try {


                val adapter = ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_expandable_list_item_1, listNames)
                listView.adapter = adapter


            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }
}


