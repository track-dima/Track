package it.polimi.dima.track.model.service.impl.fitbit

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import it.polimi.dima.track.model.service.fitbit.FitbitService
import org.json.JSONObject

class FitbitServiceImpl(context: Context): FitbitService {
    private val baseUrl = "https://api.fitbit.com/1/"
    private val queue: RequestQueue = Volley.newRequestQueue(context)

    override fun getUserInfo(
        token: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "${baseUrl}user/-/profile.json"
        val request = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            { response ->
                onSuccess(response)
            },
            { error ->
                onError(error.message ?: "Unknown error occurred")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        queue.add(request)
    }
}
