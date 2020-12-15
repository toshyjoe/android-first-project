package ch.hearc.masrad.swisssearch

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.identity.intents.Address
import kotlinx.android.synthetic.main.activity_detail.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint

class DetailActivity : AppCompatActivity() {
    private val TAG = DetailActivity::class.java.simpleName

    //private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    lateinit var map: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Log.i("TAG", "DetailActivity")

        val intent = intent
        val inputAddress = intent.getParcelableExtra<ch.hearc.masrad.swisssearch.Address>("extra_address")



        if (inputAddress != null) {
            activity_detail_name_txt.text = inputAddress.name
        }


        var ctx = applicationContext

        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        val controller = map.controller

        val mapPoint = GeoPoint(46.99783873042332, 6.938756785005269)

        controller.setZoom(9.5)
        controller.setCenter(mapPoint)



    }
}

