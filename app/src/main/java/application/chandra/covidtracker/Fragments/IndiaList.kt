package application.chandra.covidtracker.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import application.chandra.covidtracker.Adapter.StateAdapter
import application.chandra.covidtracker.DataClass.HelplinesData
import application.chandra.covidtracker.DataClass.LiveData
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Utilities.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class IndiaList : Fragment() {

    lateinit var recyclerState: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: StateAdapter
    lateinit var progress1 : ProgressBar
    lateinit var relativeLayout1 : RelativeLayout
    lateinit var textView1 : TextView

    var liveData = ArrayList<LiveData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_india_list, container, false)

        recyclerState = view.findViewById(R.id.recyclerState)
        layoutManager = LinearLayoutManager(activity)
        progress1 = view.findViewById(R.id.progressBar1)
        relativeLayout1 = view.findViewById(R.id.relativeLayout1)
        textView1 = view.findViewById(R.id.textFetching1)
        val doubleBounce: Sprite = Wave()
        progress1.setIndeterminateDrawable(doubleBounce)

        indiaListData()

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            indiaListData()
            Handler().postDelayed( {
                Toast.makeText(context, "Refreshed!", Toast.LENGTH_SHORT).show()
                pullToRefresh.isRefreshing = false
            }, 2000)
        }

        return view
    }

    private fun indiaListData(){

        progress1.visibility = View.VISIBLE
        relativeLayout1.visibility = View.VISIBLE
        textView1.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "https://api.covid19india.org/data.json"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progress1.visibility = View.GONE
                        relativeLayout1.visibility = View.GONE
                        textView1.visibility = View.GONE

                        val statewise = it.getJSONArray("statewise")
                        for (i in 1 until statewise.length()){
                            val state = statewise.getJSONObject(i)
                            val details = LiveData(
                                state.getString("state"),
                                state.getString("confirmed"),
                                state.getString("active"),
                                state.getString("recovered"),
                                state.getString("deaths"),
                                state.getString("deltaconfirmed"),
                                state.getString("deltarecovered"),
                                state.getString("deltadeaths")
                            )
                            if (details.state != "State Unassigned") {
                                liveData.add(details)
                            }
                        }

                        val nameComparator = Comparator<LiveData> { res1, res2 ->
                            res1.state.compareTo(res2.state, true)
                        }
                        Collections.sort(liveData, nameComparator)

                        adapter = StateAdapter(activity as Context,liveData)

                        recyclerState.adapter = adapter

                        recyclerState.layoutManager = layoutManager

                        layoutAnimation(recyclerState)

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

        liveData.removeAll(liveData)
    }

    fun layoutAnimation(recyclerView: RecyclerView){
        val context : Context = recyclerView.context
        val layoutAnimationController : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.slide_layout)

        recyclerView.layoutAnimation = layoutAnimationController
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

}

