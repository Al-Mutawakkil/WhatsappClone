package com.tugas.whatsappclone.activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugas.whatsappclone.MainActivity
import com.tugas.whatsappclone.R
import com.tugas.whatsappclone.adapter.ContactsAdapter
import com.tugas.whatsappclone.listener.ContactsClickListener
import com.tugas.whatsappclone.util.Contacts
import com.tugas.whatsappclone.util.progressDrawable
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity(), ContactsClickListener {
    private val contactsList = ArrayList<Contacts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        getContacts()
        setupList()
    }

    private fun getContacts() {
        progress_layout.visibility = View.VISIBLE
        contactsList.clear()

        val newList = ArrayList<Contacts>()
        val phone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        while (phone!!.moveToNext()) {
            val name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            newList.add(Contacts(name, phoneNumber))
        }
        contactsList.addAll(newList)
        phone.close()
    }

    private fun setupList() {
        progress_layout.visibility = View.GONE
        val contactsAdapter = ContactsAdapter(contactsList) // memasukkan data ke dalam adapter
        contactsAdapter.setOnItemClickListener(this) // memberikan aksi ketika item kontak diklik
        rv_contacts.apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        adapter = contactsAdapter
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
    }

    override fun onContactClicked(name: String?, phone: String?) {
        val intent = Intent() // selain hanya berpindah activity intent bisa juga berpindah activity
        intent.putExtra(MainActivity.PARAM_NAME, name) // dengan membawa data
        intent.putExtra(MainActivity.PARAM_PHONE, phone) // seperti ini
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}