//package UI
//
//import scalafx.application.JFXApp3.PrimaryStage
//import scalafx.scene.Scene
//import scalafx.scene.layout.VBox
//import scalafx.stage.{Stage, StageStyle}
//
//
///**
// * As of now this isnt used yet, I need to figure out how to make things work before
// * the thing Im trying to load finishes
// */
//object LoadingScreen {
//
//  val parentPane = new VBox()
//
//  parentPane.fillWidth = true
//  parentPane.setId("loadingScreenVbox")
//  parentPane.setPrefSize(600,300)
//
//  val parentStage = new Stage()
//  parentStage.initStyle(StageStyle.Undecorated)
//  parentStage.setAlwaysOnTop(true)
//
//  val parentScene = new Scene(parentPane)
//
//
//  parentStage.scene = parentScene
//
//
//
//  def beginLoading(): Unit ={
//    parentStage.show()
//  }
//
//
//}
