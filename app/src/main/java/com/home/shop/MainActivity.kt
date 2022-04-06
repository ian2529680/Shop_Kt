package com.home.shop

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.row_function.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger {
    var cacheService:Intent? = null
    val auth = FirebaseAuth.getInstance()
    private val RC_NICK: Int = 101
    private val RC_SINUP: Int = 100
    var isLoging = false
    var functions = listOf<String>("Camera", "Invite friend", "Parking", "Movies", "Bus", "Download coupons", "News", "Maps" )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth.addAuthStateListener({auth ->
            authChanged(auth)
        })
        /*if (!isLoging) {
            val intent = Intent(this, SingupActivity::class.java)
            startActivityForResult(intent, RC_SINUP)
        }*/
        var colors = arrayOf("Red", "Blue","Green")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("mainActivity", "onItemSelected; ${colors[position]}")
            }

        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.adapter = FunctionAdapter()

    }

    inner class FunctionAdapter() : RecyclerView.Adapter<FunctionHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_function, parent, false)
            val viewHolder = FunctionHolder(view)
            return viewHolder
        }

        override fun getItemCount(): Int {
            return functions.size
        }

        override fun onBindViewHolder(holder: FunctionHolder, position: Int) {
            holder.functionName.text = functions[position]
            holder.itemView.setOnClickListener {
                functionCliked(holder, position)
            }
        }

    }

    private fun functionCliked(holder: FunctionHolder, position: Int) {
        when(position) {
            1 -> startActivity(Intent(this, ContactActivity::class.java))
            2 -> startActivity(Intent(this, ParkingActivity::class.java))
            3 -> startActivity(Intent(this, MovieActivity::class.java))
            4 -> startActivity(Intent(this, BusActivity::class.java))
        }
    }

    class FunctionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val functionName = view.txt_function_name
    }

    override fun onResume() {
        super.onResume()
//        txt_nick.text = getNickname()
        FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser!!.uid)
            .child("nickname")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nick = dataSnapshot.value as String
                    txt_nick.text = nick
                }

            })
    }

    private fun authChanged(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            val intent = Intent(this, SingupActivity::class.java)
            startActivityForResult(intent, RC_SINUP)
        } else {
            Log.d("mainActivity", "authChanged: ${auth.currentUser?.uid}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SINUP) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, NicknameActivity::class.java)
                startActivityForResult(intent, RC_NICK)
            }
        }

        if (requestCode == RC_NICK) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals(CacheService.ACTION_CACHE_DONE)) {
                toast("MainActivity cache informed")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            R.id.action_cache -> {
                doAsync {
                    val json = URL("https://api.myjson.com/bins/m3auc").readText()
                    val movies = Gson().fromJson<List<Movie>>(json, object : TypeToken<List<Movie>>() {}.type)
                    val movie = movies.get(0)
//                cacheService = Intent(this, CacheService::class.java)
                    startService(intentFor<CacheService>(
                        "TITLE" to movie.Title,
                        "URL" to movie.Images[0]
                    ))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(CacheService.ACTION_CACHE_DONE)
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
//        stopService(cacheService)
        unregisterReceiver(broadcastReceiver)
    }
}
