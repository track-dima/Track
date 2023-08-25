package it.polimi.dima.track.model.service.fitbit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FitbitActivity(
  val activeDuration: Int,
  val activityName: String,
  val calories: Int,
  val distance: Int,
  val duration: Int,
  val pace: Int,
  val speed: Int,
  val steps: Int,
)
