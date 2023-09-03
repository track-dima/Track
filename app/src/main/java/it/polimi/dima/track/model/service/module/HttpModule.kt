package it.polimi.dima.track.model.service.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object HttpModule {
  @Provides
  @Singleton
  @JvmStatic
  fun provideHttpClient(): OkHttpClient = OkHttpClient()
}