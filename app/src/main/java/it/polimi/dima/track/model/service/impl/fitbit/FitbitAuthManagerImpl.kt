package it.polimi.dima.track.model.service.impl.fitbit

import android.net.Uri
import android.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitConfig
import it.polimi.dima.track.model.service.fitbit.FitbitOAuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject

class FitbitAuthManagerImpl @Inject constructor() : FitbitAuthManager {
  private val config: FitbitConfig = FitbitConfig()
  private val codeVerifier = generateCodeVerifier()
  private val codeChallenge = generateCodeChallenge(codeVerifier)
  private val httpClient = OkHttpClient()
  private val jacksonObjectMapper = jacksonObjectMapper()

  override fun createAuthorizationUrl(): String {
    return Uri.parse(config.authorizationUri)
      .buildUpon()
      .appendQueryParameter("client_id", config.clientId)
      .appendQueryParameter("response_type", "code")
      .appendQueryParameter("code_challenge", codeChallenge)
      .appendQueryParameter("code_challenge_method", "S256")
      .appendQueryParameter("scope", config.scope)
      .build()
      .toString()
  }

  override suspend fun isAccessTokenActive(accessToken: String): Boolean {
    val call = httpClient.newCall(
      Request.Builder()
        .url("https://api.fitbit.com/1.1/oauth2/introspect")
        .header("Authorization", "Bearer $accessToken")
        .post(
          FormBody.Builder()
            .add("token", accessToken)
            .build()
        )
        .build()
    )

    return withContext(Dispatchers.IO) {
      val response = call.execute()
      response.isSuccessful
    }
  }

  override suspend fun exchangeCodeForToken(code: String): FitbitOAuthToken {
    val call = httpClient.newCall(
      Request.Builder()
        .url(config.tokenUri)
        .header("Authorization", "Basic ${createAuthorizationHeader()}")
        .post(
          FormBody.Builder()
            .add("client_id", config.clientId)
            .add("grant_type", config.grantType)
            .add("code", code)
            .add("code_verifier", codeVerifier)
            .build()
        )
        .build()
    )

    return withContext(Dispatchers.IO) {
      val response = call.execute()
      val responseBody = response.body()
      if (response.isSuccessful && responseBody != null) {
        jacksonObjectMapper.readValue(responseBody.string())
      } else {
        throw IOException("Fitbit token retrieval failed")
      }
    }
  }

  override suspend fun refreshToken(refreshToken: String): FitbitOAuthToken {
    val call = httpClient.newCall(
      Request.Builder()
        .url(config.tokenUri)
        .header("Authorization", "Basic ${createAuthorizationHeader()}")
        .post(
          FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .build()
        )
        .build()
    )

    return withContext(Dispatchers.IO) {
      val response = call.execute()
      val responseBody = response.body()
      if (response.isSuccessful && responseBody != null) {
        jacksonObjectMapper.readValue(responseBody.string())
      } else {
        throw IOException("Unable to refresh token")
      }
    }
  }

  private fun generateCodeVerifier(): String {
    val randomBytes = ByteArray(32)
    SecureRandom().nextBytes(randomBytes)
    return Base64.encodeToString(
      randomBytes,
      Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )
  }

  private fun generateCodeChallenge(codeVerifier: String): String {
    val sha256bytes = MessageDigest
      .getInstance("SHA-256")
      .digest(codeVerifier.toByteArray())
    return Base64.encodeToString(
      sha256bytes,
      Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )
  }

  private fun createAuthorizationHeader(): String {
    val clientIdAndSecret = "${config.clientId}:${config.clientSecret}"
    return Base64.encodeToString(clientIdAndSecret.toByteArray(), Base64.NO_WRAP)
  }
}
