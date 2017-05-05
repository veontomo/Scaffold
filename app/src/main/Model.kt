import io.reactivex.Completable
import io.reactivex.functions.Action
import java.io.File

/**
 *  Model for the main screen.
 */
class Model {
    fun start(fileName: String, folderName: String, pattern: String, placeholder: String, range: String, step: String, padding: String): Completable {
        val file = File(fileName)
        if (!file.exists()) {
            return Completable.error(Throwable("File $fileName not found."))
        }

        val folder = File(folderName)
        if (!folder.exists()) {
            return Completable.error(Throwable("Folder $folderName not found."))
        }

        return Completable.complete()

    }
}
