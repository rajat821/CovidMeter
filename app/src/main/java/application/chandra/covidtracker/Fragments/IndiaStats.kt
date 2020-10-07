package application.chandra.covidtracker.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import application.chandra.covidtracker.Activity.Helplines
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Utilities.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.android.synthetic.main.fragment_india_stats.*
import org.json.JSONException
import java.text.DecimalFormat
import java.util.*

class IndiaStats : Fragment(), LocationListener {

    lateinit var locationManager: LocationManager
    lateinit var confirmedToday: TextView
    lateinit var confirmed: TextView
    lateinit var active: TextView
    lateinit var recoveredToday: TextView
    lateinit var recovered: TextView
    lateinit var deathToday: TextView
    lateinit var death: TextView
    lateinit var textDate: TextView
    lateinit var firstCard: CardView
    lateinit var secondCard: CardView
    lateinit var thirdCard: CardView
    lateinit var fourthCard: CardView
    lateinit var fetchData: CardView
    lateinit var helplines: CardView
    lateinit var progress2: ProgressBar
    lateinit var relativeLayout2: RelativeLayout
    lateinit var textView2: TextView
    var pincode = "000000"
    var reqstate = ""
    var reqdistrict = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_india_stats, container, false)

        confirmed = view.findViewById(R.id.confirmed)
        confirmedToday = view.findViewById(R.id.confirmedToday)
        active = view.findViewById(R.id.active)
        recovered = view.findViewById(R.id.recovered)
        recoveredToday = view.findViewById(R.id.recoveredToday)
        deathToday = view.findViewById(R.id.deathToday)
        death = view.findViewById(R.id.death)
        textDate = view.findViewById(R.id.textDate)
        firstCard = view.findViewById(R.id.cardFirst)
        secondCard = view.findViewById(R.id.cardSecond)
        thirdCard = view.findViewById(R.id.cardThird)
        fourthCard = view.findViewById(R.id.cardFourth)
        fetchData = view.findViewById(R.id.fetchData)
        helplines = view.findViewById(R.id.helplines)
        progress2 = view.findViewById(R.id.progressBar2)
        relativeLayout2 = view.findViewById(R.id.relativeLayout2)
        textView2 = view.findViewById(R.id.textFetching2)
        val doubleBounce: Sprite = Wave()
        progress2.setIndeterminateDrawable(doubleBounce)
        progress2.visibility = View.VISIBLE
        relativeLayout2.visibility = View.VISIBLE
        textView2.visibility = View.VISIBLE

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            indiaData()
            Handler().postDelayed({
                Toast.makeText(context, "Refreshed!", Toast.LENGTH_SHORT).show()
                pullToRefresh.isRefreshing = false
            }, 2000)
        }

        indiaData()

        helplines.setOnClickListener {
            val intent = Intent(context, Helplines::class.java)
            startActivity(intent)
            activity!!.overridePendingTransition(R.anim.zoom,R.anim.static_animation)

        }

        fetchData.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    context as Context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context as Context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 101
                )
            }

            locationManager =
                activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            progress2.visibility = View.VISIBLE
            relativeLayout2.visibility = View.VISIBLE
            textView2.visibility = View.VISIBLE
            locationEnabled()
            getLocation()

        }


        return view
    }

    private fun getLocation() {
        try {
            locationManager =
                activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                500,
                5f,
                this as LocationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun locationEnabled() {
        val lm = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder(context)
                .setTitle("Enable GPS Service")
                .setMessage("We need your GPS location to show Near Places around you.")
                .setCancelable(false)
                .setPositiveButton(
                    "Enable"

                ) { paramDialogInterface, paramInt ->
                    startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun indiaData() {

        val formatter = DecimalFormat("##,##,###")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "https://api.covid19india.org/data.json"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progress2.visibility = View.GONE
                        relativeLayout2.visibility = View.GONE
                        textView2.visibility = View.GONE
                        val stateWise = it.getJSONArray("statewise")
                        val india = stateWise.getJSONObject(0)
                        confirmed.text = formatter.format(india.getString("confirmed").toInt())
                        confirmedToday.text =
                            formatter.format(india.getString("deltaconfirmed").toInt())
                        death.text = formatter.format(india.getString("deaths").toInt())
                        deathToday.text = formatter.format(india.getString("deltadeaths").toInt())
                        recovered.text = formatter.format(india.getString("recovered").toInt())
                        recoveredToday.text =
                            formatter.format(india.getString("deltarecovered").toInt())
                        active.text = formatter.format(india.getString("active").toInt())
                        textDate.text = "Last Updated on : " + india.getString("lastupdatedtime")

                        firstCard.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        secondCard.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        thirdCard.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        fourthCard.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        helplines.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        textDate.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)
                        fetchData.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in_zoom)


                        confirmed.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        confirmedToday.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        active.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        recovered.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        recoveredToday.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        death.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                        deathToday.animation =
                            AnimationUtils.loadAnimation(activity, R.anim.fade_in)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Unable to Fetch Data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, "Unable to Fetch Data", Toast.LENGTH_SHORT)
                        .show()
                }) {

                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


    }

    override fun onLocationChanged(location: Location?) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(location!!.latitude, location.longitude, 1)
            pincode = addresses[0].postalCode

            //start**************************************
            getDataByPincode()
            //end***************************************


            //start***************************
            independentData()
            //end************************


        } catch (e: java.lang.Exception) {
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }


    private fun getDataByPincode() {

        val queue1 = Volley.newRequestQueue(context as Activity)
        val urls = "https://api.postalpincode.in/pincode/${pincode}"

        val jsonRequest1 =
            object : JsonArrayRequest(Request.Method.GET, urls, null, Response.Listener {

                try {
                    val post = it.getJSONObject(0)
                    val status = post.getString("Status")
                    if (status == "Success") {
                        val postOffice = post.getJSONArray("PostOffice")
                        val requiredPost = postOffice.getJSONObject(0)
                        reqstate = requiredPost.getString("State")
                        reqdistrict = requiredPost.getString("District")

                    } else {
                        Toast.makeText(context, "Unable to detect location", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(context, "Unable to detect location", Toast.LENGTH_SHORT).show()
                }
            },
                Response.ErrorListener {
                    Toast.makeText(context, "Unable to detect location", Toast.LENGTH_SHORT).show()
                }) {

            }
        queue1.add(jsonRequest1)
    }


    private fun independentData() {


        val customDialog: Dialog = Dialog(context as Activity)
        customDialog.setContentView(R.layout.dialog_district)
        val stateName: TextView = customDialog.findViewById(R.id.textdistrict)
        val districtConfirmed: TextView = customDialog.findViewById(R.id.districtConfirmed)
        val districtActive: TextView = customDialog.findViewById(R.id.districtActive)
        val districtRecovered: TextView = customDialog.findViewById(R.id.districtRecovered)
        val districtDeath: TextView = customDialog.findViewById(R.id.districtDeath)
        val districtConfirmedToday: TextView =
            customDialog.findViewById(R.id.districtConfirmedToday)
        val districtRecoveredToday: TextView =
            customDialog.findViewById(R.id.districtRecoveredToday)
        val districtDeathToday: TextView = customDialog.findViewById(R.id.districtDeathToday)
        val buttonCancel: ImageView = customDialog.findViewById(R.id.buttonCancel)
        val dialogLayout: RelativeLayout = customDialog.findViewById(R.id.dialogLayout)
        val dialogProgress: ProgressBar = customDialog.findViewById(R.id.dialogProgress)
        val doubleBounce: Sprite = Wave()
        dialogProgress.setIndeterminateDrawable(doubleBounce)
        dialogProgress.visibility = View.VISIBLE
        dialogLayout.visibility = View.VISIBLE
        val formatter = DecimalFormat("##,##,###")


        val queue = Volley.newRequestQueue(context as Activity)
        val url2 = "https://api.covid19india.org/state_district_wise.json"

        if (ConnectionManager().checkConnectivity(context as Activity)) {
            stateName.text = reqdistrict
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url2, null, Response.Listener {
                    try {
                        progress2.visibility = View.GONE
                        relativeLayout2.visibility = View.GONE
                        textView2.visibility = View.GONE
                        dialogProgress.visibility = View.GONE
                        dialogLayout.visibility = View.GONE
                        val state = it.getJSONObject(reqstate)
                        val dataOfDistrict = state.getJSONObject("districtData")
                        val district = dataOfDistrict.getJSONObject(reqdistrict)
                        districtConfirmed.text =
                            formatter.format(district.getInt("confirmed")).toString()
                        districtActive.text =
                            formatter.format(district.getInt("active")).toString()
                        districtDeath.text =
                            formatter.format(district.getInt("deceased")).toString()
                        districtRecovered.text =
                            formatter.format(district.getInt("recovered")).toString()
                        val delta = district.getJSONObject("delta")
                        districtConfirmedToday.text =
                            formatter.format(delta.getInt("confirmed")).toString()
                        districtRecoveredToday.text =
                            formatter.format(delta.getInt("recovered")).toString()
                        districtDeathToday.text =
                            formatter.format(delta.getInt("deceased")).toString()

                        buttonCancel.setOnClickListener {
                            customDialog.onBackPressed()
                        }
                        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        customDialog.show()

                    } catch (e: JSONException) {
                        Toast.makeText(
                            context,
                            "Failed !! Please Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {
                    Toast.makeText(context, "Unable to Fetch Data", Toast.LENGTH_SHORT)
                        .show()
                }) {

                }
            queue.add(jsonRequest)

        } else {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                (context)?.startActivity(settingsIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }
}