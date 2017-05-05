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
        val ranges = range.split(regex = Regex(".."), limit = 0)
        if (ranges.count() != 2) return Completable.error(Throwable("Wrong range format"))
        val start = try {
            ranges[0].toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong range initial value: ${ranges[0]}"))
        }
        val end = try {
            ranges[1].toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong range final value: ${ranges[1]}"))
        }

        val stepInt = try {
            step.toInt()
        } catch (e: NumberFormatException){
            return Completable.error(Throwable("Wrong step value: $step"))
        }
        val paddingSize = padding.count()
        val paddingChar = if (paddingSize == 0) null else padding[0]
        val placeholder = Placeholder(placeholder = placeholder, start = start, end = end, paddingSize = paddingSize, paddingChar = paddingChar, step = stepInt)
        val names = placeholder.expand(pattern)


        return Completable.complete()

    }
}
