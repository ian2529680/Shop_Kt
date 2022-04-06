package com.home.shop

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.jetbrains.anko.*
import java.net.URL

class ParkingActivity : AppCompatActivity(), AnkoLogger {
    val TAG = ParkingActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)
        val parking = "http://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=f4cc0b12-86ac-40f9-8745-885bddc18f79&rid=0daad6e6-0632-44f5-bd25-5e1de1e9146f"

        doAsync {
            val url = URL(parking)
            val json = url.readText()
//            info(json)
            uiThread {
                toast("Got it")
                alert("Got it", "Alert"){
                    okButton {
                        parseGson(json)
                    }
                }.show()
            }
        }

//        ParkingTask().execute(parking)
    }

    private fun parseGson(json: String) {
        val parking = Gson().fromJson<Parking>(json, Parking::class.java)
            parking.parkingLots.forEach {
                info {
                "${it.parkId}, ${it.parkName}, ${it.totalSpace}"
                }
            }
    }

    inner class ParkingTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[0])
            val json = url.readText()
            Log.d(TAG, "doInBackground: $json")
            return json
        }
        //這是做完後會執行的區塊
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}

data class Parking(val parkingLots: List<ParkingLots>)

data class ParkingLots(
    val areaId: String,
    val areaName: String,
    val parkName: String,
    val totalSpace: Int,
    val surplusSpace: String,
    val payGuide: String,
    val introduction: String,
    val address: String,
    val wgsX: Double,
    val wgsY: Double,
    val parkId: String
)

/*

{
        "areaId" : "1",
        "areaName" : "桃園區",
        "parkName" : "府前地下停車場",
        "totalSpace" : 344,
        "surplusSpace" : "260",
        "payGuide" : "停車費率:30 元/小時。停車時數未滿一小時者，以一小時計算。逾一小時者，其超過之不滿一小時部分，如不逾三十分鐘者，以半小時計算；如逾三十分鐘者，仍以一小時計算收費。",
        "introduction" : "桃園市政府管轄之停車場",
        "address" : "桃園區縣府路一號",
        "wgsX" : 121.3011,
        "wgsY" : 24.9934,
        "parkId" : "P-TY-001"
      }

 */
