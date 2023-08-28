package it.polimi.dima.track.model.service.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.polimi.dima.track.model.service.fitbit.FitbitAuthManager
import it.polimi.dima.track.model.service.fitbit.FitbitService
import it.polimi.dima.track.model.service.impl.fitbit.FitbitAuthManagerImpl
import it.polimi.dima.track.model.service.impl.fitbit.FitbitServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FitbitModule {
    @Binds
    @Singleton
    fun bindFitbitAuthManager(impl: FitbitAuthManagerImpl): FitbitAuthManager

    @Binds
    @Singleton
    fun bindFitbitService(impl: FitbitServiceImpl): FitbitService
}