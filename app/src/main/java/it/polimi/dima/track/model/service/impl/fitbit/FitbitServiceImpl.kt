package it.polimi.dima.track.model.service.impl.fitbit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.fitbit.FitbitActivity
import it.polimi.dima.track.model.service.fitbit.FitbitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class FitbitServiceImpl @Inject constructor() : FitbitService {
  private val httpClient = OkHttpClient()
  private val jacksonObjectMapper = jacksonObjectMapper()
  var token: String? = null

  override suspend fun getActivitiesByTraining(training: Training): List<FitbitActivity> {
    val responseBody = makeAuthenticatedRequest(
      HttpUrl.Builder()
        .scheme("https")
        .host("api.fitbit.com")
        .addPathSegment("1")
        .addPathSegment("user")
        .addPathSegment("-")
        .addPathSegment("activities")
        .addPathSegment("list.json")
        .addQueryParameter("afterDate", "TODO")
        .addQueryParameter("sort", "asc")
        .addQueryParameter("limit", 1.toString())
        .build()
    )

    return jacksonObjectMapper.readValue(responseBody)
  }

  private suspend fun makeAuthenticatedRequest(url: HttpUrl): String {
    if (token == null) {
      throw IOException()
    }

    val call = httpClient.newCall(
      Request.Builder()
        .get()
        .url(url)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", "Bearer $token")
        .build()
    )

    return withContext(Dispatchers.IO) {
      val response = call.execute()
      response.body().toString()
    }
  }
}
