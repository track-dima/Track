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
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import it.polimi.dima.track.model.service.impl.LogServiceImpl
import it.polimi.dima.track.model.service.impl.storage.PersonalBestStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.TrainingStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
  @Binds
  @Singleton
  abstract fun provideTrainingStorageService(impl: TrainingStorageServiceImpl): TrainingStorageService
  @Binds
  @Singleton
  abstract fun providePersonalBestStorageService(impl: PersonalBestStorageServiceImpl): PersonalBestStorageService
  @Binds
  @Singleton
  abstract fun provideUserStorageService(impl: UserStorageServiceImpl): UserStorageService
}
