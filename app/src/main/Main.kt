import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

import java.net.URL


fun main(args: Array<String>) {
    Main.main(args)
}
class Main : Application() {

    override fun start(primaryStage: Stage) {
        val resource = javaClass.getResource("mainview.fxml")
        if (resource != null) {
            val root = FXMLLoader.load<Parent>(resource)
            primaryStage.title = "Hello World"
            primaryStage.scene = Scene(root, 550.0, 320.0)
            primaryStage.show()
        } else {
            print("resource not found")
        }
    }

    companion object {


        @JvmStatic fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}
