package com.home.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.row_bus.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class BusActivity : AppCompatActivity() {
    var bus: Bus? = null
    var datas: List<Data>? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
//        .baseUrl("https://api.myjson.com/bins/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)
        doAsync {
            val busServer = retrofit.create(BusServer::class.java)
            bus = busServer.listBus()
                .execute()
                .body()
            datas = bus?.datas
            uiThread {
                recycler.layoutManager = LinearLayoutManager(this@BusActivity)
                recycler.setHasFixedSize(true)
                recycler.adapter = BusAdapter()
            }
        }
    }

    inner class BusAdapter(): RecyclerView.Adapter<BusHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            return BusHolder(view)
        }

        override fun getItemCount(): Int {
            var size = bus?.datas?.size?: 0
            return size
        }

        override fun onBindViewHolder(holder: BusHolder, position: Int) {
            var data = datas!![position]
            holder.bindBus(data)
        }
    }

    inner class BusHolder(view: View) : RecyclerView.ViewHolder(view) {
        val busId = view.txt_busId
        val routeId = view.txt_routeId
        val speed = view.txt_speed
        fun bindBus(data: Data) {
            busId.text = data.BusID
            routeId.text = data.RouteID
            speed.text = data.Speed
        }
    }
}

data class Bus(
    val datas: List<Data>
)

data class Data(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusServer {
//    @GET("kw9gk")
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBus() : Call<Bus>
}

/*
{
  "datas" : [ {
    "BusID" : "716-FD",
    "ProviderID" : "12",
    "DutyStatus" : "90",
    "BusStatus" : "90",
    "RouteID" : "",
    "GoBack" : "",
    "Longitude" : "",
    "Latitude" : "",
    "Speed" : "0",
    "Azimuth" : "0",
    "DataTime" : "",
    "ledstate" : "0",
    "sections" : "1"
  } ]
}
*/