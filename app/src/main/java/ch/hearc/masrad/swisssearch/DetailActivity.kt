package ch.hearc.masrad.swisssearch

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {
    private val TAG = DetailActivity::class.java.simpleName

    lateinit var map: MapView

    lateinit var dButtonCall: Button

    lateinit var inputAddress: ch.hearc.masrad.swisssearch.Address


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val inputAddress = intent.getParcelableExtra<ch.hearc.masrad.swisssearch.Address>("extra_address")

        dButtonCall =  findViewById<Button>(R.id.activity_detail_call_btn)

        dButtonCall.setOnClickListener {

                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + inputAddress?.phoneNumber.toString())
                startActivity(intent)

        }

        search()

        Log.i("TAG", "DetailActivity")

        val intent = intent
        Log.i("TAG", "DetailActivity::inputAddress " + inputAddress?.street)
        if (inputAddress != null) {
          activity_detail_name_txt.text = inputAddress.name
            activity_detail_street_txt.text = inputAddress.street + " " + inputAddress.streetNo
            activity_detail_city_txt.text = inputAddress.zip + " " + inputAddress.city
            activity_detail_phoneNumber_txt.text = inputAddress.phoneNumber
        }

    }



    fun search (){
        Log.i("TAG", "DetailActivity::search")
        val downloadData = Download()
        try {
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

                println("jArray" + jArray)

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


            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }




}

