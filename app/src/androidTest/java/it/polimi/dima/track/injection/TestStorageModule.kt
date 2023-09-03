package it.polimi.dima.track.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import it.polimi.dima.track.injection.service.MockPersonalBestStorageServiceImpl
import it.polimi.dima.track.injection.service.MockTrainingStorageServiceImpl
import it.polimi.dima.track.model.service.impl.storage.UserStorageServiceImpl
import it.polimi.dima.track.model.service.module.StorageModule
import it.polimi.dima.track.model.service.storage.PersonalBestStorageService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import it.polimi.dima.track.model.service.storage.UserStorageService
import javax.inject.Singleton

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [StorageModule::class]
)
@Suppress("unused")
abstract class TestStorageModule {
  @Binds
  @Singleton
  abstract fun provideMockTrainingStorageService(impl: MockTrainingStorageServiceImpl): TrainingStorageService

  @Binds
  @Singleton
  abstract fun provideMockPersonalBestStorageService(impl: MockPersonalBestStorageServiceImpl): PersonalBestStorageService

  @Binds
  @Singleton
  abstract fun provideMockUserStorageService(impl: UserStorageServiceImpl): UserStorageService
}