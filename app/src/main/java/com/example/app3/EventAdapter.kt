package com.example.app3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class EventAdapter(private val context: Context, private val eventsList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.textViewEventName.text = event.name
        holder.textViewEventDate.text = event.date
        holder.textViewLocalName.text = event.localName

        Glide.with(context)
            .load(event.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageViewEvent)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EventDetail::class.java).apply {
                putExtra("EVENT_ID", event.id.toString())
                putExtra("EVENT_NAME", event.name)
                putExtra("EVENT_DESCRIPTION", event.description)
                putExtra("EVENT_DATE", event.date)
                putExtra("EVENT_HOUR", event.hour)
                putExtra("EVENT_LOCAL_NAME", event.localName)
                putExtra("EVENT_STREET_NUMBER", event.streetNumber)
                putExtra("EVENT_CITY_STATE", event.cityState)
                putExtra("EVENT_IMAGE_URL", event.imageUrl)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewEvent: ImageView = itemView.findViewById(R.id.imageViewEvent)
        val textViewEventName: TextView = itemView.findViewById(R.id.textViewEventName)
        val textViewLocalName: TextView = itemView.findViewById(R.id.textViewLocalName)
        val textViewEventDate: TextView = itemView.findViewById(R.id.textViewEventDate)

    }
}
