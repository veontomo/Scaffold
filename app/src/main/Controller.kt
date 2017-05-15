import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        indexedFields = setOf(patternField, stepField, startField, endField, paddingField, placeholderField)
        restoreLatestData(indexedFields)
        startBtn?.onAction = EventHandler {
            val names = selectedFiles?.text?.split(separator)?.map { it.trim() }?.toTypedArray() ?: arrayOf<String>()
            val folder = folderNameField?.text ?: EMPTY
            val pattern = patternField?.text ?: EMPTY
            val marker = placeholderField?.text ?: EMPTY
            val start = startField?.text ?: EMPTY
            val end = endField?.text ?: EMPTY
            val padding = paddingField?.text ?: EMPTY
            val step = stepField?.text ?: EMPTY

            saveLatestData(indexedFields)
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
                            { e -> setMessage(e.message ?: "An error occurred") })
        }
        clearBtn?.onAction = EventHandler { clearInputFields(setOf(selectedFiles, patternField, folderNameField)) }
        selectFileBtn?.setOnAction { onSelectFiles() }
        selectFolderBtn?.setOnAction { onFolderSelected() }

    }

    private fun onFolderSelected() {
        val folder = directoryChooser.showDialog(selectFolderBtn!!.scene.window)
        folder?.let {
            setFolder(it.absolutePath)
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

    fun onSelectFiles() {
        setMessage("")
        val file = fileChooser.showOpenMultipleDialog(selectFileBtn!!.scene.window)
        val filePaths = file?.joinToString(separator) { it.absolutePath }
        filePaths?.let { setSelectedFiles(filePaths) }
    }


    fun setSelectedFiles(filePaths: String) {
        selectedFiles?.text = filePaths
    }

    fun setMessage(txt: String) {
        message?.text = txt
    }


    fun saveLatestData(fields: Set<TextField?>) {
        val prefs = Preferences.userNodeForPackage(this::class.java)
        fields.forEach {
            field ->
            field?.let { prefs.put(it.id, it.text) }
        }
        prefs.flush()
    }


    fun restoreLatestData(fields: Set<TextField?>) {
        val prefs = Preferences.userNodeForPackage(this::class.java)
        fields.forEach { field ->
            field?.let { it.text = prefs.get(field.id, null) }
        }
    }

}

