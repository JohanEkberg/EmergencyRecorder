package se.johan.emergencyrecorder.service.googledrive

data class ServiceUseCases(
    val getDriveService: GetDriveService,
    val createMediaFile: CreateMediaFile,
    val createTextFile: CreateTextFile,
    val shareFileWithUser: ShareFileWithUser,
    val listFiles: ListFiles,
    val deleteFile: DeleteFile,
    val deleteAllFiles: DeleteAllFiles
)


class ServiceException(
    message: String = "",
    val errorCode: Int = 0
) : Exception(message)