package application.chandra.covidtracker.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import application.chandra.covidtracker.Adapter.HelplineAdapter
import application.chandra.covidtracker.DataClass.HelplinesData
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
import kotlin.collections.ArrayList


class Helplines : AppCompatActivity() {

    lateinit var backButton: ImageView
    lateinit var recyclerHelplines: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var phone: ImageView
    lateinit var twitter: ImageView
    lateinit var facebook: ImageView
    lateinit var email: ImageView
    lateinit var adapter: HelplineAdapter
    lateinit var progress : ProgressBar
    lateinit var relativeLayout : RelativeLayout
    lateinit var textView : TextView
    var number : String? = null
    var emailId : String? = null
    var facebookAcc : String? = null
    var twitterAcc : String? = null

    var helplinesList = ArrayList<HelplinesData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helplines)

        recyclerHelplines = findViewById(R.id.recyclerHelplines)
        layoutManager = LinearLayoutManager(this@Helplines)
        backButton = findViewById(R.id.backButton1)
        phone = findViewById(R.id.phone)
        twitter = findViewById(R.id.twitter)
        email = findViewById(R.id.email)
        facebook = findViewById(R.id.facebook)
        progress = findViewById(R.id.progressBar4)
        relativeLayout = findViewById(R.id.relativeLayout4)
        textView = findViewById(R.id.textFetching4)
        val doubleBounce: Sprite = Wave()
        progress.setIndeterminateDrawable(doubleBounce)
        progress.visibility = View.VISIBLE
        relativeLayout.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE

        backButton.setOnClickListener {
            onBackPressed()
        }

        twitter.animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        phone.animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        email.animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        facebook.animation = AnimationUtils.loadAnimation(this,R.anim.fade_in)

        val queue = Volley.newRequestQueue(this@Helplines)
        val url = "https://api.rootnet.in/covid19-in/contacts"

        if (ConnectionManager().checkConnectivity(this@Helplines)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progress.visibility = View.GONE
                        relativeLayout.visibility = View.GONE
                        textView.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONObject("data")
                            val contacts = data.getJSONObject("contacts")
                            val primary = contacts.getJSONObject("primary")
                            number = primary.getString("number")
                            emailId = primary.getString("email")
                            twitterAcc = primary.getString("twitter")
                            facebookAcc = primary.getString("facebook")
                            val regional = contacts.getJSONArray("regional")
                            for (j in 0 until regional.length()) {
                                val help = regional.getJSONObject(j)
                                val helplinesData = HelplinesData(
                                    help.getString("loc"),
                                    help.getString("number")
                                )
                                helplinesList.add(helplinesData)
                            }
                            val nameComparator = Comparator<HelplinesData> { res1, res2 ->
                                res1.state.compareTo(res2.state, true)
                            }
                            Collections.sort(helplinesList, nameComparator)
                            layoutAnimation(recyclerHelplines)

                            adapter = HelplineAdapter(this@Helplines, helplinesList)

                            recyclerHelplines.adapter = adapter

                            recyclerHelplines.layoutManager = layoutManager

                        } else {
                            Toast.makeText(
                                this@Helplines,
                                "Unable to Fetch Data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        phone.setOnClickListener {
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:" + number)
                            startActivity(dialIntent)
                        }
                        twitter.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(twitterAcc)
                            startActivity(i)
                        }
                        email.setOnClickListener {
                            val emailIntent = Intent(Intent.ACTION_SEND)
                            emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            emailIntent.type = "vnd.android.cursor.item/email"
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailId))
                            startActivity(Intent.createChooser(emailIntent, "Send mail using..."))

                        }
                        facebook.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(facebookAcc)
                            startActivity(i)
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Helplines,
                            "Unable to Fetch Data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@Helplines,
                        "Unable to Fetch Data",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@Helplines)
            dialog.setTitle("Failed")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this@Helplines)
            }
            dialog.create()
            dialog.show()
        }
    }
    fun layoutAnimation(recyclerView: RecyclerView){
        val context : Context = recyclerView.context
        val layoutAnimationController : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.slide_layout)

        recyclerView.layoutAnimation = layoutAnimationController
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.static_animation,R.anim.zoom_out)
    }
}