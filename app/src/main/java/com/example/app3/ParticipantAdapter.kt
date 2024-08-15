package com.example.app3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParticipantAdapter(private val participantList: List<ParticipantActivity.User>) :
    RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val currentItem = participantList[position]
        holder.textViewEmail.text = currentItem.email
        holder.textViewId.text = currentItem.id
        holder.textViewStatus.text = currentItem.status
    }

    override fun getItemCount() = participantList.size

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewEmail: TextView = itemView.findViewById(R.id.textViewParticipantEmail)
        val textViewId: TextView = itemView.findViewById(R.id.textViewParticipantId)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewParticipantStatus)
    }
}
