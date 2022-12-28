package it.polimi.dima.track.model.service

interface LogService {
  fun logNonFatalCrash(throwable: Throwable)
}
