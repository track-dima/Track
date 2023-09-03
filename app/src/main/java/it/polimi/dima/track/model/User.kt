package it.polimi.dima.track.model

import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val name: String = "",
    val specialty: String = "",
    val fitbitToken: FitbitOAuthToken? = null,
)