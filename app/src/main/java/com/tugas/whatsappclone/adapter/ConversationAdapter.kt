package com.tugas.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugas.whatsappclone.R
import com.tugas.whatsappclone.util.Message

class ConversationAdapter(private val messages: ArrayList<Message>, val userId: String?) :
    RecyclerView.Adapter<ConversationViewholder>(){

    companion object {
        val MESSAGE_CURRENT_USER = 1 // pesan dari user
        val MESSAGE_OTHER_USER = 2 // pesan dari partner chat user
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewholder {
        if (viewType == MESSAGE_CURRENT_USER) {
            return ConversationViewholder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_current_user_message, parent, false)
            )
        }else {
            return ConversationViewholder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_other_user_message, parent, false)
            )
        }
    }

    override fun getItemCount() = messages.size


    override fun onBindViewHolder(holder: ConversationViewholder, position: Int) {
        holder.bindItem(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].sentBy.equals(userId)) {
            return MESSAGE_CURRENT_USER
        }else {
            return MESSAGE_OTHER_USER
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }


}

class ConversationViewholder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bindItem(message: Message) {
        view.findViewById<TextView>(R.id.txt_message).text = message.message
    }
}
