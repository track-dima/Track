import android.content.Context
import com.android.volley.toolbox.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class FitbitService(context: Context) {
  private val baseUrl = "https://api.fitbit.com/1/"
  private val queue: RequestQueue = Volley.newRequestQueue(context)

  fun getUserInfo(
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
