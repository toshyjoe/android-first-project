 package ch.hearc.masrad.swisssearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView


 class MapActivity : AppCompatActivity() {

     lateinit var mMap: MapView // xml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // mMap = binding.mapView
        mMap.setTileSource(TileSourceFactory.MAPNIK) // map

        Configuration.getInstance().load(applicationContext, getSharedPreferences( "phoneBook app", MODE_PRIVATE))

        val longitude: Double = intent.getDoubleExtra("Longitude", 36.7783)
        val latitude: Double = intent.getDoubleExtra("Latitude", 119.4179)

        val controller: IMapController? = mMap.controller

        val mapPoint = GeoPoint(longitude,latitude)
        controller?.setZoom(9.5)
        controller?.setCenter(mapPoint)

    }
}