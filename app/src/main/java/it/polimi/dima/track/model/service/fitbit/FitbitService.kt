package it.polimi.dima.track.model.service.fitbit

import org.json.JSONObject

interface FitbitService {
    fun getUserInfo(
        token: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    )
}