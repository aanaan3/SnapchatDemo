package com.aanaan3.snapchatdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import com.aanaan3.snapchatdemo.databinding.ActivitySnapBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapActivity : AppCompatActivity()  {

    lateinit var mbinding:ActivitySnapBinding
    private lateinit var mAuth: FirebaseAuth
    var emails: ArrayList<String> = ArrayList()
    var snaps:ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivitySnapBinding.inflate(layoutInflater)
        setContentView(mbinding.root)

        mAuth = FirebaseAuth.getInstance()


        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        mbinding.snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid).child("snaps")
            .addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emails.add(snapshot.child("from").value as String)
                snaps.add(snapshot)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                var index = 0
                for (snap: DataSnapshot in snaps) {
                    if (snap.key == snapshot.key) {
                        snaps.removeAt(index)
                        emails.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        mbinding.snapsListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps.get(i)

            val intent = Intent(this,SnapViewActivity::class.java)
            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.createSnap){
            val intent = Intent(this,CreateSnapActivity::class.java)
            startActivity(intent)
        }else if (item.itemId == R.id.logout){
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}