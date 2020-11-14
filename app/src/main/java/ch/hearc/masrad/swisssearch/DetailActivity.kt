package ch.hearc.masrad.swisssearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        val intent = intent
        val addressName = intent.getStringExtra("name")
        activity_detail_name_txt.text = addressName

    }
}