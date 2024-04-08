package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide

data class Weather(
    val date: String,
    val temperature: Double,
    val description: String,
    val iconUrl: String
)

class WeatherAdapter(private val weatherList: ArrayList<Weather>) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.weather_item, parent,
                false
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.dateTextView.text = weather.date
        holder.temperatureTextView.text = "${weather.temperature}°C"
        holder.descriptionTextView.text = weather.description

        Glide.with(holder.itemView.context)
            .load(weather.iconUrl)
            .override(200, 200)
            .centerCrop()
            .into(holder.iconImageView)
    }

    override fun getItemCount() = weatherList.size
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val temperatureTextView: TextView =
            itemView.findViewById(R.id.temperatureTextView)
        val descriptionTextView: TextView =
            itemView.findViewById(R.id.descriptionTextView)
        val iconImageView: ImageView =
            itemView.findViewById(R.id.iconImageView)
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val city = "Lisbon"
        val apiKey = "ab9aa73f8b34f99fee0b87363778d1ca"
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&units=metric&appid=$apiKey"
        val weatherList = ArrayList<Weather>()
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val jsonArray = response.getJSONArray("list")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val date = jsonObject.getString("dt_txt")
                    val temperature =
                        jsonObject.getJSONObject("main").getDouble("temp")
                    val description =
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                    val icon =
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon")
                    val iconUrl =
                        "https://openweathermap.org/img/w/$icon.png"
                    val weather = Weather(
                        date, temperature, description,
                        iconUrl
                    )
                    weatherList.add(weather)
                }
                val recyclerView =
                    findViewById<RecyclerView>(R.id.weather_list)
                val weatherAdapter = WeatherAdapter(weatherList)
                recyclerView.adapter = weatherAdapter
            },
            { error ->
                Toast.makeText(
                    this, "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        queue.add(request)
    }
}
