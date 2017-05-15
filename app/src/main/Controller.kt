import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences

class Controller : Initializable {
    private val EMPTY = ""
    private val model = Model()
    @FXML private var startBtn: Button? = null
    @FXML private var clearBtn: Button? = null
    @FXML private var selectedFiles: TextField? = null
    @FXML private var folderNameField: TextField? = null
    @FXML private var patternField: TextField? = null
    @FXML private var placeholderField: TextField? = null
    @FXML private var startField: TextField? = null
    @FXML private var endField: TextField? = null
    @FXML private var paddingField: TextField? = null
    @FXML private var stepField: TextField? = null
    @FXML private var selectFileBtn: Button? = null
    @FXML private var selectFolderBtn: Button? = null
    @FXML private var message: Text? = null


    private val fileChooser = FileChooser()
    private val directoryChooser = DirectoryChooser()

    /**
     * Set of text fields which values are to be filled in by previosly used values
     * when the app starts.
     */
    lateinit var indexedFields: Set<TextField?>

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
        restoreParams(preferences, indexedFields)
        startBtn?.onAction = EventHandler {
            val names = selectedFiles?.text?.split(separator)?.map { it.trim() }?.toTypedArray() ?: arrayOf<String>()
            val folder = folderNameField?.text ?: EMPTY
            val pattern = patternField?.text ?: EMPTY
            val marker = placeholderField?.text ?: EMPTY
            val start = startField?.text ?: EMPTY
            val end = endField?.text ?: EMPTY
            val padding = paddingField?.text ?: EMPTY
            val step = stepField?.text ?: EMPTY

            storeParams(preferences, indexedFields)
            model.start(fileNames = names,
                    folderName = folder,
                    pattern = pattern,
                    marker = marker,
                    start = start,
                    end = end,
                    padding = padding,
                    step = step)
                    .subscribe(
                            { setMessage("Done") },
                            { e -> setMessage(e.message ?: "Unknown error occurred") })
        }
        clearBtn?.onAction = EventHandler { clearInputFields(setOf(selectedFiles, patternField, folderNameField, placeholderField, startField, endField, stepField, paddingField)) }
        selectFileBtn?.setOnAction { showSelectFileDialog() }
        selectFolderBtn?.setOnAction { showSelectFolderDialog() }

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
        folderNameField?.text = path
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
        selectedFiles?.text = filePaths
    }

    fun setMessage(txt: String) {
        message?.text = txt
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

}

