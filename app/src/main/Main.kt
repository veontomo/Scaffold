import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage


fun main(args: Array<String>) {
    Main.main(args)
}

class Main : Application() {

    /**
     * Name of the layout file corresponding to this view. It is supposed to be in the resource folder.
     */
    private val LAYOUT = "mainview.fxml"

    /**
     * Path to the app logo
     */
    private val ICON_PATH ="/logo.png"

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Scaffold"

        val grid = FXMLLoader.load<GridPane>(javaClass.getResource(LAYOUT))
        primaryStage.icons.add(Image(ICON_PATH))
        primaryStage.scene = Scene(grid, primaryStage.width - grid.padding.left - grid.padding.right, 500.0)
        primaryStage.show()
    }

    companion object {


        @JvmStatic fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}
