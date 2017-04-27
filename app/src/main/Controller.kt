import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javafx.event.EventHandler
import javafx.fxml.FXML;
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

class Controller : Initializable {
    private val model = Model()
    @FXML private var start: Button? = null
    @FXML private var clear: Button? = null
    @FXML private var selectedFiles: TextField? = null
    @FXML private var folderName: TextField? = null
    @FXML private var pattern: TextField? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        start?.onAction = EventHandler {
            model.start("a", "b", "c").subscribe({ print("Done") }, {print("Error")} )
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
