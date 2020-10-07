package application.chandra.covidtracker.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Utilities.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import org.json.JSONException
import java.text.DecimalFormat

class WorldStats : Fragment() {

    lateinit var country : TextView
    lateinit var worldConfirmedToday : TextView
    lateinit var worldConfirmed : TextView
    lateinit var worldActive : TextView
    lateinit var worldRecoveredToday : TextView
    lateinit var worldRecovered : TextView
    lateinit var worldDeathToday : TextView
    lateinit var card : CardView
    lateinit var firstCard : CardView
    lateinit var secondCard : CardView
    lateinit var thirdCard : CardView
    lateinit var fourthCard : CardView
    lateinit var fifthCard : CardView
    lateinit var worldDeath : TextView
    lateinit var worldCritical : TextView
    lateinit var progress : ProgressBar
    lateinit var relativeLayout : RelativeLayout
    lateinit var textView : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_world_stats, container, false)

        country = view.findViewById(R.id.country)
        worldConfirmed = view.findViewById(R.id.worldConfirmed)
        worldConfirmedToday = view.findViewById(R.id.worldConfirmedToday)
        worldActive = view.findViewById(R.id.worldActive)
        worldRecovered = view.findViewById(R.id.worldRecovered)
        worldRecoveredToday = view.findViewById(R.id.worldRecoveredToday)
        worldDeathToday = view.findViewById(R.id.worldDeathToday)
        worldDeath = view.findViewById(R.id.worldDeath)
        worldCritical = view.findViewById(R.id.worldCritical)
        card = view.findViewById(R.id.card)
        firstCard = view.findViewById(R.id.cardFirst)
        secondCard = view.findViewById(R.id.cardSecond)
        thirdCard = view.findViewById(R.id.cardThird)
        fourthCard = view.findViewById(R.id.cardFourth)
        fifthCard = view.findViewById(R.id.cardFifth)
        progress = view.findViewById(R.id.progressBar)
        relativeLayout = view.findViewById(R.id.relativeLayout)
        textView = view.findViewById(R.id.textFetching)
        val doubleBounce: Sprite = Wave()
        progress.indeterminateDrawable = doubleBounce
        progress.visibility = View.VISIBLE
        relativeLayout.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE


        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            data()
            Handler().postDelayed( {
                Toast.makeText(context, "Refreshed!", Toast.LENGTH_SHORT).show()
                pullToRefresh.isRefreshing = false
            }, 2000)
        }

        data()

        return view
    }


    private fun data(){
        val formatter = DecimalFormat("##,##,###")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "https://corona.lmao.ninja/v2/all"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progress.visibility = View.GONE
                        relativeLayout.visibility = View.GONE
                        textView.visibility = View.GONE
                        country.text = it.getString("affectedCountries")
                        worldConfirmed.text = formatter.format(it.getString("cases").toInt())
                        worldConfirmedToday.text = formatter.format(
                            it.getString("todayCases").toInt()
                        )
                        worldDeath.text = formatter.format(it.getString("deaths").toInt())
                        worldDeathToday.text = formatter.format(it.getString("todayDeaths").toInt())
                        worldRecovered.text = formatter.format(it.getString("recovered").toInt())
                        worldRecoveredToday.text = formatter.format(
                            it.getString("todayRecovered").toInt()
                        )
                        worldActive.text = formatter.format(it.getString("active").toInt())
                        worldCritical.text = formatter.format(it.getString("critical").toInt())

                        firstCard.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)
                        secondCard.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)
                        thirdCard.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)
                        fourthCard.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)
                        card.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)
                        fifthCard.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in_zoom)

                        worldConfirmed.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldConfirmedToday.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldActive.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldRecovered.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldRecoveredToday.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldDeath.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                        worldDeathToday.animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)

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
}