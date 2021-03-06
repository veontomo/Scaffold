import io.reactivex.Single
import java.io.File
import java.io.FileNotFoundException

/**
 *  Model for the main screen.
 */
class Model {

    fun start(fileNames: Array<String>, placeholder: String, start: Int, end: Int, step: Int, pattern: String, target: String, padding: String) {
        val paddingChar = if (padding.isEmpty()) null else padding[0]
        val paddingSize = padding.length
        val placeholderObj = Placeholder(placeholder = placeholder,
                start = start,
                end = end,
                paddingSize = paddingSize,
                paddingChar = paddingChar,
                step = step)
        val names = placeholderObj.expand(pattern)
        fileNames.forEach { it -> copyFile(it, target, names) }
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

    /**
     * Convert array of strings to a Single instance.
     * Each string is a full path of a file that is supposed to be scaffolded later.
     * Every blank string is to be ignored.
     *
     * @param names file full paths
     */
    fun elaborateFileNames(names: Array<String>): Single<Array<String>> {
        val nonEmptyNames = names.filterNot { it.isBlank() }.toTypedArray()
        return if (nonEmptyNames.isEmpty()) Single.error(Throwable("No valid file names are given")) else Single.just(nonEmptyNames)
    }

    /**
     * Convert a string into a Single instance.
     * If a folder corresponding to the path does not exist, an error is generated.
     * @param folderName full path to a folder in which other folders are to be created.
     */
    fun elaborateFolder(folderName: String?): Single<String> {
        val folder = File(folderName)
        return if (!folder.exists()) {
            Single.error(FileNotFoundException("Folder '$folderName' is not found."))
        } else {
            Single.just(folderName)
        }
    }

    /**
     * Convert a string into an integer. If the given string can not be converted into an integer, an error is created.
     * @param value a string to be cast to an integer
     */
    fun elaborateInteger(value: String?): Single<Int> {
        if (value == null) {
            return Single.error(Throwable("Value is not set."))
        }
        val start = try {
            value.toInt()
        } catch (e: NumberFormatException) {
            return Single.error(Throwable("The parameter must be an integer, instead '$value' is received."))
        }
        return Single.just(start)
    }

    /**
     * Wrap a string into a Single instance.
     * @param value a string.
     */
    fun elaborateString(value: String?): Single<String> {
        val padding = value ?: ""
        return Single.just(padding)
    }


}
