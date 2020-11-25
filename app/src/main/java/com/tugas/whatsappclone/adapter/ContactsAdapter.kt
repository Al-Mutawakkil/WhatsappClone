package com.tugas.whatsappclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugas.whatsappclone.R
import com.tugas.whatsappclone.listener.ContactsClickListener
import com.tugas.whatsappclone.util.Contacts
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_contacts.*

class ContactsAdapter(val contacts: ArrayList<Contacts>) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private var clickListener: ContactsClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contacts,
        parent, false))

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bindItem(contacts[position], clickListener)
    }
    class ContactsViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        // memasangkan data pada view dalam layout_item
        fun bindItem(contact: Contacts, listener: ContactsClickListener?) {
            txt_contact_name.text = contact.name
            txt_contact_number.text = contact.phone
            itemView.setOnClickListener { // memberikan aksi ketika item kontak diklik
                listener?.onContactClicked(contact.name, contact.phone) }
        } }
    fun setOnItemClickListener(listener: ContactsClickListener) {
        clickListener = listener
        notifyDataSetChanged()
    }
}


