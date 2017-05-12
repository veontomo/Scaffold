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

class Controller : Initializable {
    private val EMPTY = ""
    private val model = Model()
    @FXML private var start: Button? = null
    @FXML private var clear: Button? = null
    @FXML private var selectedFiles: TextField? = null
    @FXML private var folderName: TextField? = null
    @FXML private var pattern: TextField? = null
    @FXML private var placeholderField: TextField? = null
    @FXML private var startField: TextField? = null
    @FXML private var endField: TextField? = null
    @FXML private var paddingField: TextField? = null
    @FXML private var stepField: TextField? = null
    @FXML private var selectFileBtn: Button? = null
    @FXML private var selectFolder: Button? = null
    @FXML private var message: Text? = null


    private val fileChooser = FileChooser()
    private val direcoryChooser = DirectoryChooser()

    /**
     * String used to separate different file names
     */
    private val separator = ","

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        start?.onAction = EventHandler {

            val names = selectedFiles?.text?.split(separator)?.map { it.trim() }?.toTypedArray()

            model.start(fileNames = names ?: arrayOf<String>(),
                    folderName = folderName?.text ?: EMPTY,
                    pattern = pattern?.text ?: EMPTY,
                    marker = placeholderField?.text ?: EMPTY,
                    start = startField?.text ?: EMPTY,
                    end = endField?.text ?: EMPTY,
                    padding = paddingField?.text ?: EMPTY,
                    step = stepField?.text ?: EMPTY)

                    .subscribe(
                            { setMessage("Done") },
                            { e -> setMessage(e.message ?: "An error occurred") })
        }
        clear?.onAction = EventHandler { clearInputFields(setOf(selectedFiles, pattern, folderName)) }
        selectFileBtn?.setOnAction { onSelectFiles() }
        selectFolder?.setOnAction { onFolderSelected() }

    }

    private fun onFolderSelected() {
        val folder = direcoryChooser.showDialog(selectFolder!!.scene.window)
        folder?.let {
            setFolder(it.absolutePath)
        }
    }

    private fun setFolder(path: String){
        folderName?.text = path
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
}
