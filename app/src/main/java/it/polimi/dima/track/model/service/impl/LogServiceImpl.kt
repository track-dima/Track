package it.polimi.dima.track.model.service.impl

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import it.polimi.dima.track.model.service.LogService
import javax.inject.Inject

class LogServiceImpl @Inject constructor() : LogService {
  override fun logNonFatalCrash(throwable: Throwable) =
    Firebase.crashlytics.recordException(throwable)
}
