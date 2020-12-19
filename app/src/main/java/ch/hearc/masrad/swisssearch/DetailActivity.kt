package ch.hearc.masrad.swisssearch

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.identity.intents.Address
import com.google.android.gms.nearby.messages.internal.ClientAppContext
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {
    private val TAG = DetailActivity::class.java.simpleName

    lateinit var map: MapView

    lateinit var dButtonCall: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        dButtonCall =  findViewById<Button>(R.id.activity_detail_call_btn)

        dButtonCall.setOnClickListener {

                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:1234566")
                startActivity(intent)

        }


        fun onClick(v: View){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:1234566"))
            startActivity(intent)

        }




        search()


        Log.i("TAG", "DetailActivity")

        val intent = intent
        val inputAddress = intent.getParcelableExtra<ch.hearc.masrad.swisssearch.Address>("extra_address")
        Log.i("TAG", "DetailActivity::inputAddress " + inputAddress?.street)
        if (inputAddress != null) {
          activity_detail_name_txt.text = inputAddress.name
        }






    }


    fun search (){
        Log.i("TAG", "DetailActivity::search")
        val downloadData = Download()
        try {
            //val key = "&key=920b2b17d1aca9724a94b8799a2d8bec"
            //val key = "&key=139e1566337f93a78344eec754ce94ca"
            //https://nominatim.openstreetmap.org/search?q=17+Strada+Pictor+Alexandru+Romano%2C+Bukarest&format=geojson
            val Address = intent.getParcelableExtra<ch.hearc.masrad.swisssearch.Address>("extra_address")
            val url = "https://nominatim.openstreetmap.org/search?q="
            var geojson = "&format=geojson"
            val address = Address?.street.toString()
            downloadData.execute(url+address+geojson)
            Log.i("TAG", "DetailActivity::search::address " + url+address+geojson)
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
                println("Download")
                val jSONObject = JSONObject(result)
                println("result " + jSONObject)
                val jString = jSONObject.getString("type")
                println("getString " + jString)
                val jArray =  jSONObject.getJSONArray("features")
                //val features = jSONObject.getString("features")
                println("jArray" + jArray)

                //val values = features.get(0)
                //val values = jSONObject.getJSONArray("features")
                val x = jArray.getJSONObject(0)
                println("jArray" + x)

                val y = x.getJSONObject("geometry")
                println("jArray" + y)

                val z = y.getJSONArray("coordinates")
                println("jArray" + z)

                val long: Double = z.getDouble(0)
                println("jArray" + long)

                val lat: Double = z.getDouble(1)
                println("jArray" + lat)

                var ctx = applicationContext

                Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
                map = findViewById(R.id.map)
                map.setTileSource(TileSourceFactory.MAPNIK)

                val controller = map.controller

                val mapPoint = GeoPoint(lat, long)

                controller.setZoom(18.5)
                controller.setCenter(mapPoint)

                var items = ArrayList<OverlayItem>()

                items.add(OverlayItem("resto", "chez",GeoPoint(lat,long)))

                var mOverlay = ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items,
                    object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                        override fun onItemSingleTapUp(index:Int, item:OverlayItem):Boolean {
                            return false
                        }
                        override fun onItemLongPress(index:Int, item:OverlayItem):Boolean {
                            return false
                        }
                    })

                mOverlay.setFocusItemsOnTap(true)
                map.overlays.add(mOverlay)




                    //val rates = jSONObject.getString("rates")
                //println(rates)
                //data.cars['Nissan'][0].model

                //val newJsonObject = jSONObject["features"][3]
                //println("newJsonObject " + newJsonObject)
                //val geometry = newJsonObject.getString("geometry")
                //println("geometry" + geometry)Curty

                //val czk = newJsonObject.getString("CZK")
                //val tl = newJsonObject.getString("TRY")

                //chfText.text = "CHF: " + chf
                //czkText.text = "CZK: " + czk
                //tryText.text = "TRY: " + tl
            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }




}

