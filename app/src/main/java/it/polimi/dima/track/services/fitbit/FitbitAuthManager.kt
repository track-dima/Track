package it.polimi.dima.track.services.fitbit

import android.net.Uri
import android.util.Base64
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import java.security.SecureRandom

class FitbitAuthManager(private val config: FitbitConfig) {
    private val codeVerifier = generateCodeVerifier()
    private val codeChallenge = generateCodeChallenge(codeVerifier)
    private val httpClient = OkHttpClient()

    fun createAuthorizationUrl(): String {
        return Uri.parse(config.authorizationUri)
            .buildUpon()
            .appendQueryParameter("client_id", config.clientId)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", config.redirectUri)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("scope", config.scope)
            .build()
            .toString()
    }

    fun exchangeCodeForToken(code: String): FitbitOAuthToken {
        val requestBody = FormBody.Builder()
            .add("client_id", config.clientId)
            .add("grant_type", config.grantType)
            .add("code", code)
            .add("redirect_uri", config.redirectUri)
            .add("code_verifier", codeVerifier)
            .build()
        val request = Request.Builder()
            .url(config.tokenUri)
            .header("Authorization", "Basic ${createAuthorizationHeader()}")
            .post(requestBody)
            .build()
        val response = httpClient.newCall(request).execute().body()!!.string()
        return ObjectMapper().readValue(response, FitbitOAuthToken::class.java)
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
        val sha256bytes = MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray())
        return Base64.encodeToString(
            sha256bytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    private fun createAuthorizationHeader(): String {
        val clientIdAndSecret = "${config.clientId}:${config.clientSecret}"
        return Base64.encodeToString(clientIdAndSecret.toByteArray(), Base64.NO_WRAP)
    }

    companion object {
        private const val FITBIT_OAUTH_REQUEST_CODE = 1001
    }
}
