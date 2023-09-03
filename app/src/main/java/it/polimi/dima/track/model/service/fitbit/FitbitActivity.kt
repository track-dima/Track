package it.polimi.dima.track.model.service.fitbit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.Date

@JsonIgnoreProperties(ignoreUnknown = true)
data class FitbitActivity(
  val startTime: Date,
  val duration: Int,
  val activityName: String,
  var calories: Int,
  var steps: Int,
  var distance: Int,
  var elevationGain: Int,
)
