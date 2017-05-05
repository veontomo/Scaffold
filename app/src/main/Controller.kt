import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
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
    @FXML private var placeholder: TextField? = null
    @FXML private var range: TextField? = null
    @FXML private var padding: TextField? = null
    @FXML private var step: TextField? = null


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        start?.onAction = EventHandler {

            model.start(fileName = selectedFiles?.text ?: EMPTY,
                    folderName = folderName?.text ?: EMPTY,
                    pattern = pattern?.text ?: EMPTY,
                    marker = placeholder?.text ?: EMPTY,
                    range = range?.text ?: EMPTY,
                    padding = padding?.text ?: EMPTY,
                    step = step?.text ?: EMPTY)

                    .subscribe(
                            { print("Done") },
                            { e -> print(e.message) })
        }
        clear?.onAction = EventHandler { clearInputFields(setOf(selectedFiles, pattern, folderName)) }


    }

    /**
     * Clear the content of each text field.
     * @param views set of text fields. Null elements are ignored.
     */
    private fun clearInputFields(views: Set<TextField?>) {
        views.filterNotNull().forEach { it.text = null }
    }
}
