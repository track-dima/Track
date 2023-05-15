package it.polimi.dima.track.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class TrainingStep(
  @DocumentId val id: String = "",
  val type: String = "",
  val durationType: String = "",
  val distance: Int = 0,
  val duration: Int = 0,
  val recover: Boolean = true,
  val recoverType: String = "",
  val recoverDistance: Int = 0,
  val recoverDuration: Int = 0,
)
