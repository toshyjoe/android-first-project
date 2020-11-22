 package ch.hearc.masrad.swisssearch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.util.Log
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

import org.osmdroid.views.MapView


 class MapActivity : AppCompatActivity() {
     private val TAG = MapActivity::class.java.simpleName


     private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
     lateinit var map: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var ctx = applicationContext

        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)



    }
}