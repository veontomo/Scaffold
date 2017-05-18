import io.reactivex.Single
import io.reactivex.functions.Function8
import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences

class Controller : Initializable {
    private val model = Model()
    @FXML private var startBtn: Button? = null
    @FXML private var clearBtn: Button? = null
    @FXML private var filesField: TextField? = null
    @FXML private var targetFolderField: TextField? = null
    @FXML private var patternField: TextField? = null
    @FXML private var placeholderField: TextField? = null
    @FXML private var startField: TextField? = null
    @FXML private var endField: TextField? = null
    @FXML private var paddingField: TextField? = null
    @FXML private var stepField: TextField? = null
    @FXML private var selectFileBtn: Button? = null
    @FXML private var selectFolderBtn: Button? = null
    @FXML private var messageField: Text? = null


    private val fileChooser = FileChooser()

    private val directoryChooser = DirectoryChooser()

    /**
     * Set of text fields which values are to be filled in by previosly used values
     * when the app starts.
     */
    lateinit var indexedFields: Set<TextField?>
    /**
     * Set of text fields that are highlighted in case their content is no valid
     */
    lateinit var warningFields: Set<TextField?>

    /**
     * String used to separate different file names
     */
    private val separator = ","

    /**
     * Preferences related to this application (or this class) in which some values are to be stored.
     */
    private val preferences = Preferences.userNodeForPackage(this::class.java)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        indexedFields = setOf(patternField, stepField, startField, endField, paddingField, placeholderField)
        warningFields = indexedFields + filesField + targetFolderField

        restoreParams(preferences, indexedFields)

        startBtn?.onAction = EventHandler {
            val names = filesField?.text?.split(separator)?.map { it.trim() }?.toTypedArray() ?: arrayOf<String>()
            val target = targetFolderField?.text
            val pattern = patternField?.text
            val placeholder = placeholderField?.text
            val start = startField?.text
            val end = endField?.text
            val padding = paddingField?.text
            val step = stepField?.text

            storeParams(preferences, indexedFields)
            clearScene()

            val s1 = model.elaborateFileNames(names = names).doOnError { _ -> highlightField(filesField) }
            val s2 = model.elaborateFolder(folderName = target).doOnError { _ -> highlightField(targetFolderField) }
            val s3 = model.elaborateInteger(value = start).doOnError { _ -> highlightField(startField) }
            val s4 = model.elaborateInteger(value = end).doOnError { _ -> highlightField(endField) }
            val s5 = model.elaborateInteger(value = step).doOnError { _ -> highlightField(stepField) }
            val s6 = model.elaborateString(value = padding).doOnError { _ -> highlightField(patternField) }
            val s7 = model.elaborateString(value = placeholder).doOnError { _ -> highlightField(placeholderField) }
            val s8 = model.elaborateString(value = pattern).doOnError { _ -> highlightField(patternField) }

            Single.zip(s1, s2, s3, s4, s5, s6, s7, s8, Function8<Array<String>, String, Int, Int, Int, String, String, String, Unit> {
                names, target, start, end, step, padding, placeholder, pattern ->
                model.start(fileNames = names, placeholder = placeholder, target = target, start = start, end = end, step = step, padding = padding, pattern = pattern)
            }).subscribe({
                setOKMessage("Done")
            }, { e -> setErrorMessage(e.message ?: "Unknown error while scaffolding...") })


        }



        clearBtn?.onAction = EventHandler { clearInputFields(setOf(filesField, patternField, targetFolderField, placeholderField, startField, endField, stepField, paddingField)) }
        selectFileBtn?.setOnAction { showSelectFileDialog() }
        selectFolderBtn?.setOnAction { showSelectFolderDialog() }

    }

    /**
     * Clear the scene from messages and warning generated during elaboration of user input.
     */
    private fun clearScene() {
        warningFields.forEach { it -> enableWarning(it, false) }
        setMessage("")
    }

    private fun setOKMessage(s: String) {
        messageField?.text = s
        messageField?.fill = Color.GREEN
    }

    private fun setErrorMessage(msg: String) {
        messageField?.text = msg
        messageField?.fill = Color.RED
    }

    private fun highlightField(field: TextField?) {
        enableWarning(field, true)
    }

    /**
     * Show a dialog window to select a folder.
     * If the preferences contain a last used directory, then the directory chooser is to be set that directory as
     * the initial one.
     */
    private fun showSelectFolderDialog() {
        selectFolderBtn?.let { btn ->
            val dir = preferences.get(btn.id, null)
            dir?.let {
                directoryChooser.initialDirectory = File(it)
            }
            directoryChooser.showDialog(btn.scene.window)?.let {
                folder ->
                folder.let {
                    setFolder(it.absolutePath)
                    preferences.put(btn.id, folder.absolutePath)
                }
            }
        }
    }

    private fun setFolder(path: String) {
        targetFolderField?.text = path
    }

    /**
     * Clear the content of each text field.
     * @param views set of text fields. Null elements are ignored.
     */
    private fun clearInputFields(views: Set<TextField?>) {
        views.filterNotNull().forEach { it.text = null }
    }

    /**
     * Show a dialog to select multiple files
     * If the preferences contain a directory a first file has been selected form,
     * then the file chooser is to be set that directory as the initial one.
     */
    fun showSelectFileDialog() {
        setMessage("")
        selectFileBtn?.let { btn ->
            preferences.get(btn.id, null)?.let {
                lastDir ->
                run {
                    val dir = File(lastDir)
                    if (dir.exists()) {
                        fileChooser.initialDirectory = dir
                    }
                }
            }
            fileChooser.showOpenMultipleDialog(btn.scene.window)?.let {
                files ->
                run {
                    setSelectedFiles(files.joinToString(separator) { it.absolutePath })
                    preferences.put(btn.id, files.firstOrNull()?.parent)
                }
            }
        }
    }


    fun setSelectedFiles(filePaths: String) {
        filesField?.text = filePaths
    }

    fun setMessage(txt: String) {
        messageField?.text = txt
    }

    /**
     * Read the string content of given text field elements and save them in the preferences.
     * @params prefs location in which the parameters are to be saved
     * @param fields set of text fields
     */
    fun storeParams(prefs: Preferences, fields: Set<TextField?>) {
        fields.forEach {
            field ->
            field?.let { prefs.put(it.id, it.text) }
        }
        prefs.flush()
    }

    /**
     * Fill in the given text fields by corresponding strings found in the preferences associated with those fields.
     * @params prefs location of previously saved parameters
     * @param fields set of text fields
     */
    fun restoreParams(prefs: Preferences, fields: Set<TextField?>) {
        fields.forEach { field ->
            field?.let { it.text = prefs.get(field.id, null) }
        }
    }


    private fun enableWarning(field: TextField?, status: Boolean = true) {
        field?.let {
            if (status) {
                it.stylesheets?.add("warning.css")
            } else {
                it.stylesheets?.remove("warning.css")
            }
        }
    }

}

