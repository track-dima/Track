package it.polimi.dima.track.model.service.fitbit

import com.fasterxml.jackson.annotation.JsonProperty

data class FitbitOAuthToken(
    @JsonProperty("access_token") val accessToken: String = "",
    @JsonProperty("expires_in") val expiresIn: Long = 0L,
    @JsonProperty("refresh_token") val refreshToken: String = "",
    @JsonProperty("token_type") val tokenType: String = "",
    @JsonProperty("scope") val scope: String = "",
    @JsonProperty("user_id") val userId: String = "",
)
