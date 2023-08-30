package it.polimi.dima.track.model.service.impl.fitbit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.fitbit.FitbitActivity
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import it.polimi.dima.track.model.service.fitbit.FitbitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject

class FitbitServiceImpl @Inject constructor() : FitbitService {
  override lateinit var token: FitbitOAuthToken
  private val httpClient = OkHttpClient()
  private val jacksonObjectMapper = jacksonObjectMapper()

  override suspend fun getActivitiesByTraining(training: Training): List<FitbitActivity> {
    if (training.dueDate == null || training.dueDatetime == null) {
      throw IOException()
    }

    val call = httpClient.newCall(
      Request.Builder()
        .get()
        .url(
          HttpUrl.Builder()
            .scheme("https")
            .host("api.fitbit.com")
            .addPathSegment("1")
            .addPathSegment("user")
            .addPathSegment("-")
            .addPathSegment("activities")
            .addPathSegment("list.json")
            .addQueryParameter(
              "afterDate",
              SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(training.dueDatetime!!)
            )
            .addQueryParameter("sort", "asc")
            .addQueryParameter("limit", 1.toString())
            .addQueryParameter("offset", 0.toString())
            .build()
        )
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", "Bearer ${token.accessToken}")
        .build()
    )

    val responseContent = withContext(Dispatchers.IO) {
      val response = call.execute()
      val responseBody = response.body()
      if (!response.isSuccessful || responseBody == null) {
        throw IOException()
      }
      responseBody.string()
    }

    val dto: FitbitActivityDto = jacksonObjectMapper.readValue(responseContent)

    return dto.activities.filter {  (it.startTime.toInstant().toEpochMilli() + it.duration) < (training.dueDatetime!!.time + (training.calculateTotalTime() * 1000)) }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private data class FitbitActivityDto(
    val activities: List<FitbitActivity>
  )
}