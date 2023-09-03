package it.polimi.dima.track.model.service.fitbit

import io.mockk.mockk
import it.polimi.dima.track.model.service.impl.fitbit.FitbitAuthManagerImpl
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FitbitAuthManagerTest {
  private lateinit var httpClient: OkHttpClient
  private lateinit var fitbitAuthManager: FitbitAuthManager

  @Before
  fun setUp() {
    httpClient = mockk()
    fitbitAuthManager = FitbitAuthManagerImpl(httpClient)
  }

  @Test
  fun createAuthorizationUrl() {
    val expectedUrl = HttpUrl.Builder()
      .scheme("https")
      .host("www.fitbit.com")
      .encodedPath("/oauth2/authorize")
      .addQueryParameter("client_id", fitbitAuthManager.config.clientId)
      .addQueryParameter("response_type", "code")
      .addQueryParameter("code_challenge", fitbitAuthManager.codeChallenge)
      .addQueryParameter("code_challenge_method", "S256")
      .addQueryParameter("scope", fitbitAuthManager.config.scope)
      .build()
      .toString()

    val actualUrl = fitbitAuthManager.createAuthorizationUrl()

    assertEquals(expectedUrl, actualUrl)
  }
}