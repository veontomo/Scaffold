import io.reactivex.Completable
import java.io.File
import java.io.FileNotFoundException

/**
 *  Model for the main screen.
 */
class Model {
    fun start(fileNames: Array<String>, folderName: String, pattern: String, marker: String, start: String, end: String, step: String, padding: String): Completable {
        val startInt = try {
            start.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong range initial value: $start"))
        }
        val endInt = try {
            end.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong range final value: $end"))
        }

        val stepInt = try {
            step.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong step value: $step"))
        }
        val paddingSize = padding.count()
        val paddingChar = if (paddingSize == 0) null else padding[0]
        val placeholder2 = Placeholder(placeholder = marker, start = startInt, end = endInt, paddingSize = paddingSize, paddingChar = paddingChar, step = stepInt)
        val names = placeholder2.expand(pattern)
        try {
            fileNames.forEach { it -> copyFile(it, folderName, names) }
        } catch (e: FileNotFoundException) {
            return Completable.error(Throwable(e.message))
        } catch (e: FileAlreadyExistsException) {
            return Completable.error(Throwable(e.message))
        }
        return Completable.complete()

    }


    private fun copyFile(fileName: String, folderName: String, dirNames: Set<String>) {
        val file = File(fileName)
        if (!file.exists()) {
            throw FileNotFoundException("File $fileName not found.")
        }
        val folder = File(folderName)
        if (!folder.exists()) {
            throw FileNotFoundException("Folder $folderName not found.")
        }
        if (!(folder.isDirectory)) {
            throw FileNotFoundException("$folderName must be a folder.")
        }
        val separ = System.getProperty("file.separator")
        val normalizedFolder = if (folderName.endsWith(separ)) folderName else folderName + separ
        val normalizedNames = dirNames.map { normalizedFolder + it + separ + file.name }
        normalizedNames.forEach { file.copyTo(target = File(it), overwrite = false) }
    }
}
