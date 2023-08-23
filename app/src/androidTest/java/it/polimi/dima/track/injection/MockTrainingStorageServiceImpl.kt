package it.polimi.dima.track.injection

import com.google.firebase.firestore.FirebaseFirestore
import it.polimi.dima.track.data.mockedTrainings
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.AccountService
import it.polimi.dima.track.model.service.storage.TrainingStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MockTrainingStorageServiceImpl
@Inject
@Suppress("unused")
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  TrainingStorageService {

  override val trainings: Flow<List<Training>>
    get() = flowOf(mockedTrainings)

  override suspend fun searchTrainings(query: String): List<Training> =
    listOf()

  override suspend fun getTraining(trainingId: String): Training? =
    mockedTrainings.find { it.id == trainingId }

  override suspend fun saveTraining(training: Training): String = ""

  override suspend fun updateTraining(training: Training): Unit = Unit

  override suspend fun updatePersonalBestFlag(trainingId: String, flag: Boolean): Unit = Unit

  override suspend fun duplicateTraining(training: Training): String = ""

  override suspend fun deleteTraining(trainingId: String): Unit = Unit

  override suspend fun deleteAllForUser(userId: String) = Unit
}
