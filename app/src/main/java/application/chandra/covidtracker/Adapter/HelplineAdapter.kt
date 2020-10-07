package application.chandra.covidtracker.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import application.chandra.covidtracker.DataClass.HelplinesData
import application.chandra.covidtracker.R
import java.security.AccessController.getContext

class HelplineAdapter(val context: Context, val help: ArrayList<HelplinesData>): RecyclerView.Adapter<HelplineAdapter.HelplinesViewHolder>() {

    class HelplinesViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtState: TextView = view.findViewById(R.id.txtState)
        val txtPhone: TextView = view.findViewById(R.id.txtphone)
        val txtPhone2: TextView = view.findViewById(R.id.txtphone2)
        var card: CardView = view.findViewById(R.id.impoCard)
        var modified : String? = null
        var modified2 : String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelplinesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.single_row_helplines,
            parent,
            false
        )
        return HelplinesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelplinesViewHolder, position: Int) {
        val helpLine = help[position]
        holder.card.layoutParams.height = dpToPx(65)
        holder.txtPhone2.visibility = View.GONE
        holder.txtState.text = helpLine.state
        if (helpLine.number.contains(",")) {
            val i = helpLine.number.indexOf(",")
            holder.modified = helpLine.number.substring(0, i)
            holder.modified2 = helpLine.number.substring(i + 1).replace(",", "")
            holder.txtPhone2.text = holder.modified2
            holder.card.layoutParams.height = dpToPx(85)
            holder.txtPhone2.visibility = View.VISIBLE

            holder.txtPhone2.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + holder.modified2)
                context.startActivity(dialIntent)
            }
        } else {
            holder.modified = helpLine.number
        }

        holder.card.animation = AnimationUtils.loadAnimation(context, R.anim.slide)

        
        holder.txtPhone.text = holder.modified
        holder.txtPhone.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + holder.modified)
            context.startActivity(dialIntent)
        }
    }

    override fun getItemCount(): Int {
        return help.size
    }

    private fun dpToPx(dp: Int): Int {
        val density: Float = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}