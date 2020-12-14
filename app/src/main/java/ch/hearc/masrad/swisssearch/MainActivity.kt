@file:Suppress("DEPRECATION")

package ch.hearc.masrad.swisssearch

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.widget.AdapterView



class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mNameInput: EditText
    private lateinit var mSearchButton: Button




    inner class AddressAdapter : BaseAdapter {

        private var addressList = ArrayList<Address>()
        private var context : Context? = null

        constructor(context: Context, addressList: ArrayList<Address>) : super() {
            this.addressList = addressList
            this.context = context
        }



        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {


            val view: View?
            val vh: ViewHolder

            if (convertView == null ) {

                view = layoutInflater.inflate(R.layout.address, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("TAG", "set tag for viewHolder, position : " + position)

            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.address_name_txt.text = addressList[position].name
            vh.address_city_txt.text = addressList[position].street + " " + addressList[position].streetNo
            vh.address_city_2_txt.text = addressList[position].zip + " " + addressList[position].city
                    return view

        }

        override fun getItem(position: Int): Any {
            return addressList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return addressList.size
        }



    }


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
            //val key = "&key=920b2b17d1aca9724a94b8799a2d8bec"
            val key = "&key=139e1566337f93a78344eec754ce94ca"
            val url = "https://tel.search.ch/api/?was="
            val chosenBase = mNameInput.text.toString()
            downloadData.execute(url+chosenBase+key)
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    //data class Entry(val title: String?, val phoneNumber: String?)
    data class Entry(val title: String?)



    inner class Download : AsyncTask<String, Void, String>() {

        //var listNames = ArrayList<String>()
        var listAddresses = ArrayList<Address>()


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
            //Log.i("TAG", "MainActivity::doInBackground => readText ")

            var result: String = ""
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.text
                //listNames.add(parser.text)
                Log.i("TAG", "MainActivity::doInBackground => readText var " + result)
                parser.nextTag()
            }
            //return result
            //Log.i("TAG", "MainActivity::doInBackground => readText listName " + listNames)
            return result

        }


        @Throws(IOException::class, XmlPullParserException::class)
        fun readTag(parser: XmlPullParser, tagName: String): String {
            parser.require(XmlPullParser.START_TAG, null, tagName)
            val title = readText(parser)
            parser.require(XmlPullParser.END_TAG, null, tagName)
            return title
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun readEntry(parser: XmlPullParser): Entry {
            Log.i("TAG", "MainActivity::doInBackground => readEntry ")
            Log.i("TAG", "MainActivity::doInBackground => readEntry::parse " + parser)

            var newAddress : Address

            parser.require(XmlPullParser.START_TAG, null, "entry")
            var title: String? = null
            var street: String? = null
            var streetNo: String? = null
            var phone: String? = null
            var zip: String? = null
            var city: String? = null
            while (parser.next() != XmlPullParser.END_TAG) {
                Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::while " + parser.name)
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }
                when (parser.name) {
                    "title" -> title = readTag(parser, "title")
                    "tel:street" -> street = readTag(parser, "tel:street")
                    "tel:streetno" -> streetNo = readTag(parser, "tel:streetno" )
                    "tel:phone" -> phone = readTag(parser, "tel:phone")
                    "tel:zip" -> zip = readTag(parser, "tel:zip")
                    "tel:city" -> city = readTag(parser, "tel:city")
                    else -> skip(parser)
                }

                Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::when " + title)
                //Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::when readPhone :::::::: "  + phone)
            }
            //return Entry(title, phone)
            newAddress = Address(1, title, street, streetNo, phone, zip, city)
            listAddresses.add(newAddress)
            Log.i("TAG", "MainActivity::doInBackground => readEntry::parse::when:::List " + listAddresses)
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
            // récupérer l'url dans le tableau de paramètres p0 reçu
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


            Log.i("TAG", "MainActivity::onPostExecute ")


                //val adapter = ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_expandable_list_item_1, listNames)

                val addressAdapter = AddressAdapter(this@MainActivity, listAddresses)

                listView.adapter = addressAdapter
                listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                    val intent = Intent(applicationContext, DetailActivity::class.java)
                    startActivity(intent)

                /*

                    listView.adapter = adapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                        Log.i("MainActivity", "index:")
                        val intent = Intent(applicationContext, DetailActivity::class.java)
                        //intent.putExtra("name",imagesNames[position])
                        //intent.putExtra("image",imageIds[position])
                        startActivity(intent)


                 */
                }
        }
    }


    private class  ViewHolder(view : View?) {
        val address_name_txt: TextView
        val address_city_txt: TextView
        val address_city_2_txt: TextView

        init {
            this.address_name_txt = view?.findViewById(R.id.address_name_txt) as TextView
            this.address_city_txt = view?.findViewById(R.id.address_city_txt) as TextView
            this.address_city_2_txt = view?.findViewById(R.id.address_city_2_txt) as TextView
        }
    }
}





