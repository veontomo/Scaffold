import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

class Controller : Initializable {
    @FXML private var start: Button? = null
    @FXML private var clear: Button? = null
    @FXML private var selectedFiles: TextField? = null
    @FXML private var folderName: TextField? = null
    @FXML private var pattern: TextField? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        start?.onAction = EventHandler {
            println("ciao")
        }
        clear?.onAction = EventHandler { clearInputField() }


    }

    private fun clearInputField() {
        selectedFiles?.let {
            it.text = null
        }
        pattern?.let {
            it.text = null
        }
        folderName?.let {
            it.text = null
        }
    }
}
