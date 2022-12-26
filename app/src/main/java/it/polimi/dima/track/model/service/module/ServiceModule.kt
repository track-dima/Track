package it.polimi.dima.track.model.service.module

import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.ConfigurationService
import it.polimi.dima.track.model.service.impl.AccountServiceImpl
import it.polimi.dima.track.model.service.impl.ConfigurationServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.impl.LogServiceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

  @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService
  @Binds
  abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService
}
