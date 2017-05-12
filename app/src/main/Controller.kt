import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.text.Text
import javafx.stage.FileChooser
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
    @FXML private var message: Text? = null


    private val fileChooser = FileChooser()

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
                            { showMessage("Done") },
                            { e -> showMessage(e.message ?: "An error occurred") })
        }
        clear?.onAction = EventHandler { clearInputFields(setOf(selectedFiles, pattern, folderName)) }
        selectFileBtn?.setOnAction { onSelectFiles() }

    }

    /**
     * Clear the content of each text field.
     * @param views set of text fields. Null elements are ignored.
     */
    private fun clearInputFields(views: Set<TextField?>) {
        views.filterNotNull().forEach { it.text = null }
    }

    fun onSelectFiles() {
        showMessage("")
        val file = fileChooser.showOpenMultipleDialog(selectFileBtn!!.scene.window)
        val filePaths = file.joinToString(separator) { it.absolutePath }
        setSelectedFiles(filePaths)
    }


    fun setSelectedFiles(filePaths: String) {
        selectedFiles?.text = filePaths
    }

    fun showMessage(txt: String) {
        message?.text = txt
    }
}
