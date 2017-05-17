import io.reactivex.Completable
import java.io.File
import java.io.FileNotFoundException

/**
 *  Model for the main screen.
 */
class Model {

    /**
     * list of full path names of the files to be multiplicated (scaffolded)
     */
    private var fileNames: Array<String> = arrayOf()

    /**
     * full path to the folder in which the newly created files are to be copied
     */
    private var folderName: String? = null

    private var startC: Int? = null

    private var endC: Int? = null

    private var stepC: Int? = null

    private var placeholder: String? = null

    private var pattern: String? = null

    private var paddingSize: Int = 0

    private var paddingChar: Char? = null

    fun start(): Completable {
        val pl = placeholder
        val start = startC
        val end = endC
        val step = stepC
        val ptn = pattern
        val folder = folderName

        if (start == null)
            return Completable.error(Throwable("Start value is not set"))
        if (end == null)
            return Completable.error(Throwable("End value is not set"))
        if (pl == null)
            return Completable.error(Throwable("Placeholder is not set"))
        if (step == null)
            return Completable.error(Throwable("Step value is not set"))
        if (ptn == null)
            return Completable.error(Throwable("Pattern is not set"))
        if (folder == null)
            return Completable.error(Throwable("Target folder is not set"))

        val placeholder2 = try {
            Placeholder(placeholder = pl, start = start, end = end, paddingSize = paddingSize, paddingChar = paddingChar, step = step)
        } catch (e: Exception){
            return Completable.error(Throwable(e.message))
        }

        val names = placeholder2.expand(ptn)
        try {
            fileNames.forEach { it -> copyFile(it, folder, names) }
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

    fun fileNames(names: Array<String>): Completable {
        fileNames = names.clone()
        val missing = fileNames.filterNot { name -> File(name).exists() }
        return if (missing.isEmpty()) Completable.complete() else Completable.error(Throwable("File(s) ${missing.joinToString { it }} not found."))
    }

    fun targetFolderName(name: String): Completable {
        folderName = name
        val folder = File(name)
        return if (!folder.exists()) {
            Completable.error(FileNotFoundException("Folder '$name' is not found."))
        } else {
            Completable.complete()
        }
    }

    fun startValue(value: String): Completable {
        startC = try {
            value.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong start value: '$value'"))
        }
        return Completable.complete()
    }

    fun endValue(value: String): Completable {
        endC = try {
            value.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong end value: $value"))
        }
        return Completable.complete()
    }

    fun stepValue(value: String): Completable {
        stepC = try {
            value.toInt()
        } catch (e: NumberFormatException) {
            return Completable.error(Throwable("Wrong step value: $value"))
        }
        return Completable.complete()
    }


    fun paddingValue(value: String?): Completable {
        if (value == null) {
            paddingSize = 0
            paddingChar = null
        } else {
            paddingSize = value.length
            paddingChar = value[0]
        }
        return Completable.complete()
    }

    fun placeholderPatternValue(placeholder: String?, pattern: String?): Completable {
        this.placeholder = placeholder
        this.pattern = pattern
        if (placeholder.isNullOrEmpty()) {
            return Completable.error(Throwable("Placeholder is not set"))
        }
        if (pattern.isNullOrEmpty()) {
            return Completable.error(Throwable("Scaffold pattern is not set"))
        }
        if (!pattern!!.contains(placeholder!!, false)) {
            return Completable.error(Throwable("Pattern does not contain the placeholder."))
        }

        return Completable.complete()
    }

}
