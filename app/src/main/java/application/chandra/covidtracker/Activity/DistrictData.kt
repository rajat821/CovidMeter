package application.chandra.covidtracker.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import application.chandra.covidtracker.Adapter.DistrictAdapter
import application.chandra.covidtracker.DataClass.DataDistrict
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Utilities.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.github.ybq.android.spinkit.style.Wave
import org.json.JSONException
import java.text.DecimalFormat


class DistrictData : AppCompatActivity() {

    lateinit var backButton: ImageView
    lateinit var txtStateName : TextView
    lateinit var txtTotalCases : TextView
    lateinit var txtActiveCases : TextView
    lateinit var txtRecovered : TextView
    lateinit var txtDeaths : TextView
    lateinit var txtTodayTotalCases : TextView
    lateinit var txtTodayRecovered : TextView
    lateinit var txtTodayDeaths : TextView
    lateinit var recyclerDistrict: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: DistrictAdapter
    var state : String = ""
    lateinit var progress2 : ProgressBar
    lateinit var relativeLayout2 : RelativeLayout
    lateinit var textView2 : TextView

    var dataDistrict = ArrayList<DataDistrict>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_district_data)

        recyclerDistrict = findViewById(R.id.recyclerDistricts)
        layoutManager = GridLayoutManager(this@DistrictData, 2)
        backButton = findViewById(R.id.backButton)
        txtStateName = findViewById(R.id.txtStateName)
        txtTotalCases = findViewById(R.id.textTC1)
        txtActiveCases = findViewById(R.id.textActive1)
        txtRecovered = findViewById(R.id.textRecovered1)
        txtDeaths = findViewById(R.id.textDeaths1)
        txtTodayTotalCases = findViewById(R.id.textTTC1)
        txtTodayRecovered = findViewById(R.id.textTodayRecovered1)
        txtTodayDeaths = findViewById(R.id.textTodayDeaths1)
        progress2 = findViewById(R.id.progressBar2)
        relativeLayout2 = findViewById(R.id.relativeLayout2)
        textView2 = findViewById(R.id.textFetching2)
        val doubleBounce: Sprite = Wave()
        progress2.setIndeterminateDrawable(doubleBounce)
        progress2.visibility = View.VISIBLE
        relativeLayout2.visibility = View.VISIBLE
        textView2.visibility = View.VISIBLE
        val formatter = DecimalFormat("##,##,###")

        if (intent!=null){
            state = intent.getStringExtra("state")
            txtTotalCases.text = formatter.format(intent.getStringExtra("totalCases").toInt())
            txtActiveCases.text = formatter.format(intent.getStringExtra("active").toInt())
            txtRecovered.text = formatter.format(intent.getStringExtra("recovered").toInt())
            txtDeaths.text = formatter.format(intent.getStringExtra("death").toInt())
            txtTodayTotalCases.text = formatter.format(intent.getStringExtra("todayTotal").toInt()).toString()
            txtTodayRecovered.text = formatter.format(intent.getStringExtra("todayRecovered").toInt()).toString()
            txtTodayDeaths.text = formatter.format(intent.getStringExtra("todayDeaths").toInt()).toString()
        }
        txtStateName.text = state


        backButton.setOnClickListener {
            onBackPressed()
        }

        getDistricts()

    }

    private fun getDistricts(){
        val queue = Volley.newRequestQueue(this@DistrictData)
        val url = "https://api.covidindiatracker.com/state_data.json"

        if (ConnectionManager().checkConnectivity(this@DistrictData)) {
            val jsonRequest =
                object : JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progress2.visibility = View.GONE
                        relativeLayout2.visibility = View.GONE
                        textView2.visibility = View.GONE

                        for (i in 0 until it.length()) {
                            val states = it.getJSONObject(i)
                            val st = states.getString("state")
                            if (st == state) {
                                val districtsArray = states.getJSONArray("districtData")
                                for (j in 0 until districtsArray.length()) {
                                    val district = districtsArray.getJSONObject(j)
                                    val districtCases = DataDistrict(
                                        district.getString("name"),
                                        district.getInt("confirmed")
                                    )
                                    dataDistrict.add(districtCases)
                                }
                                adapter = DistrictAdapter(this@DistrictData, dataDistrict,state)

                                recyclerDistrict.adapter = adapter

                                recyclerDistrict.layoutManager = layoutManager
                            }
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DistrictData,
                            "Unable to Fetch Data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@DistrictData, "Unable to Fetch Data", Toast.LENGTH_SHORT)
                        .show()
                }) {

                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@DistrictData)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this@DistrictData)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.static_animation,R.anim.zoom_out)
    }
}