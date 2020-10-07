package application.chandra.covidtracker.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import application.chandra.covidtracker.DataClass.DataDistrict
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Utilities.ConnectionManager
import com.andexert.library.RippleView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import org.json.JSONException
import java.text.DecimalFormat

class DistrictAdapter(
    val context: Context,
    val district: ArrayList<DataDistrict>,
    val state: String
):RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    class DistrictViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDistrict: TextView = view.findViewById(R.id.textdistrict)
        val txtDistrictData:TextView = view.findViewById(R.id.textdistrictData)
        val districtCard : CardView = view.findViewById(R.id.districtCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(
           R.layout.single_row_district,
           parent,
           false
       )
        return DistrictViewHolder(view)
    }

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        val formatter = DecimalFormat("##,##,###")
        val districtData = district[position]
        holder.txtDistrict.text = districtData.district
        holder.txtDistrictData.text = formatter.format(districtData.confirmed).toString()

        if(position%2 == 0) {
            holder.districtCard.animation = AnimationUtils.loadAnimation(context, R.anim.slide_item_1)
        }
        else{
            holder.districtCard.animation = AnimationUtils.loadAnimation(context, R.anim.slide_item)
        }

        holder.districtCard.setOnClickListener {

            val customDialog : Dialog = Dialog(context)
            customDialog.setContentView(R.layout.dialog_district)
            val stateName :TextView = customDialog.findViewById(R.id.textdistrict)
            val districtConfirmed : TextView = customDialog.findViewById(R.id.districtConfirmed)
            val districtActive : TextView = customDialog.findViewById(R.id.districtActive)
            val districtRecovered : TextView = customDialog.findViewById(R.id.districtRecovered)
            val districtDeath : TextView = customDialog. findViewById(R.id.districtDeath)
            val districtConfirmedToday : TextView = customDialog.findViewById(R.id.districtConfirmedToday)
            val districtRecoveredToday : TextView = customDialog.findViewById(R.id.districtRecoveredToday)
            val districtDeathToday : TextView = customDialog. findViewById(R.id.districtDeathToday)
            val buttonCancel : ImageView = customDialog.findViewById(R.id.buttonCancel)
            val dialogLayout : RelativeLayout = customDialog.findViewById(R.id.dialogLayout)
            val dialogProgress : ProgressBar = customDialog.findViewById(R.id.dialogProgress)
            val doubleBounce: Sprite = Wave()
            dialogProgress.setIndeterminateDrawable(doubleBounce)
            dialogProgress.visibility = View.VISIBLE
            dialogLayout.visibility = View.VISIBLE
            val formatter = DecimalFormat("##,##,###")

            val queue = Volley.newRequestQueue(context)
            val url = "https://api.covid19india.org/state_district_wise.json"

            if (ConnectionManager().checkConnectivity(context)) {
                stateName.text = districtData.district
                districtConfirmed.text = formatter.format(districtData.confirmed).toString()
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                        try {

                            dialogProgress.visibility = View.GONE
                            dialogLayout.visibility = View.GONE

                            val state = it.getJSONObject(state)
                            val dataOfDistrict = state.getJSONObject("districtData")
                            val district = dataOfDistrict.getJSONObject(districtData.district)
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
                                "Data Not Available",
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
                    context.startActivity(settingsIntent)
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

    override fun getItemCount(): Int {
        return district.size
    }

}
