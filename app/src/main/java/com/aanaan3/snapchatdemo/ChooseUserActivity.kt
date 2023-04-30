package com.aanaan3.snapchatdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aanaan3.snapchatdemo.databinding.ActivityChooseUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityChooseUserBinding

    var emails:ArrayList<String> = ArrayList()
    var keys:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChooseUserBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        mBinding.chooseUserListView.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emails.add(email)
                keys.add(snapshot.key.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        mBinding.chooseUserListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val from = FirebaseAuth.getInstance().currentUser!!.email.toString()
            val snapMap : Map<String,String> = mapOf("from" to from,
                "imageName" to intent.getStringExtra("imageName").toString(),
                "imageURL" to intent.getStringExtra("imageURL").toString(),
                "massage" to intent.getStringExtra("massage").toString())

            FirebaseDatabase.getInstance().getReference().child(keys.get(i)).child("snaps").push().setValue(snapMap)

            val intent = Intent(this,SnapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }
}