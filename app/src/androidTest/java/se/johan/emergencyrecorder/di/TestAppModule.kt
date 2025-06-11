package se.johan.emergencyrecorder.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.johan.emergencyrecorder.locator.GetContinuousLocation
import se.johan.emergencyrecorder.locator.GetLocation
import se.johan.emergencyrecorder.locator.LocatorRepository
import se.johan.emergencyrecorder.locator.LocatorUseCases
import se.johan.emergencyrecorder.service.googledrive.CreateMediaFile
import se.johan.emergencyrecorder.service.googledrive.CreateTextFile
import se.johan.emergencyrecorder.service.googledrive.DeleteAllFiles
import se.johan.emergencyrecorder.service.googledrive.DeleteFile
import se.johan.emergencyrecorder.service.googledrive.GetDriveService
import se.johan.emergencyrecorder.service.googledrive.GoogleDriveServiceRepository
import se.johan.emergencyrecorder.service.googledrive.ListFiles
import se.johan.emergencyrecorder.service.googledrive.ServiceUseCases
import se.johan.emergencyrecorder.service.googledrive.ShareFileWithUser
import se.johan.emergencyrecorder.videorecorder.OpenCamera
import se.johan.emergencyrecorder.videorecorder.PrepareRecording
import se.johan.emergencyrecorder.videorecorder.Release
import se.johan.emergencyrecorder.videorecorder.StartRecordingSession
import se.johan.emergencyrecorder.videorecorder.StopRecording
import se.johan.emergencyrecorder.videorecorder.VideoRecorderRepository
import se.johan.emergencyrecorder.videorecorder.VideoRecorderUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideVideoRecorderRepository(): VideoRecorderRepository {
        return VideoRecorderRepository()
    }

    @Provides
    @Singleton
    fun provideVideoRecorderUseCases(repository: VideoRecorderRepository): VideoRecorderUseCases {
        return VideoRecorderUseCases(
            openCamera = OpenCamera(repository),
            prepareRecording = PrepareRecording(repository),
            startRecordingSession = StartRecordingSession(repository),
            stopRecording = StopRecording(repository),
            release = Release(repository)
        )
    }

    @Provides
    @Singleton
    fun provideLocatorRepository(context: Application): LocatorRepository {
        return LocatorRepository(context)
    }

    @Provides
    @Singleton
    fun provideLocatorUseCases(repository: LocatorRepository): LocatorUseCases {
        return LocatorUseCases(
            getLocation = GetLocation(repository),
            getContinuousLocation = GetContinuousLocation(repository)
        )
    }

    @Provides
    @Singleton
    fun provideGoogleDriveServiceRepository(): GoogleDriveServiceRepository {
        return GoogleDriveServiceRepository()
    }

    @Provides
    @Singleton
    fun provideServiceUseCases(repository: GoogleDriveServiceRepository): ServiceUseCases {
        return ServiceUseCases(
            getDriveService = GetDriveService(repository),
            createMediaFile = CreateMediaFile(repository),
            createTextFile = CreateTextFile(repository),
            shareFileWithUser = ShareFileWithUser(repository),
            listFiles = ListFiles(repository),
            deleteFile = DeleteFile(repository),
            deleteAllFiles = DeleteAllFiles(repository)
        )
    }
}