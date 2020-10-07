package application.chandra.covidtracker.Adapter;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import application.chandra.covidtracker.Activity.DistrictData
import application.chandra.covidtracker.DataClass.LiveData
import application.chandra.covidtracker.R
import com.andexert.library.RippleView
import java.text.DecimalFormat

class StateAdapter(val context: Context, val state: ArrayList<LiveData>) :
    RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    class StateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val state: TextView = view.findViewById(R.id.textState)
        val totalCases: TextView = view.findViewById(R.id.textTC)
        val active : TextView = view.findViewById(R.id.textActive)
        val recovered: TextView = view.findViewById(R.id.textRecovered)
        val death: TextView = view.findViewById(R.id.textDeaths)
        val relative: CardView = view.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_row, parent, false)
        return StateViewHolder(view)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        val formatter = DecimalFormat("##,##,###")
        val stateData = state[position]
        holder.state.text = stateData.state
        holder.totalCases.text = formatter.format(stateData.totalCases.toInt()).toString()
        holder.active.text = formatter.format(stateData.active.toInt()).toString()
        holder.recovered.text = formatter.format(stateData.recovered.toInt()).toString()
        holder.death.text = formatter.format(stateData.death.toInt()).toString()

        holder.relative.animation = AnimationUtils.loadAnimation(context, R.anim.slide)


        holder.relative.setOnClickListener {

            if (stateData.state == "Dadra and Nagar Haveli and Daman and Diu" || stateData.state == "Lakshadweep") {
                Toast.makeText(context, "Detailed Data Not Available", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(context, DistrictData::class.java)
                intent.putExtra("state", stateData.state)
                intent.putExtra("totalCases", stateData.totalCases)
                intent.putExtra("active",stateData.active)
                intent.putExtra("recovered", stateData.recovered)
                intent.putExtra("death", stateData.death)
                intent.putExtra("todayTotal",stateData.todayComfirmed)
                intent.putExtra("todayRecovered",stateData.todayRecovered)
                intent.putExtra("todayDeaths",stateData.todayDeaths)
                context.startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.zoom,R.anim.static_animation)
            }
        }
    }

    override fun getItemCount(): Int {
        return state.size
    }
}
