package sicAssembler;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class Interface extends Application {

	Stage stage = null;

	@Override
	public void start(final Stage stage) {
		this.stage = stage;
		stage.setTitle("SIC Assembler");
		stage.setResizable(false);
		setDimentions();

		final FileChooser fileChooser = new FileChooser();

		final Button openButton = new Button("Choose file");

		openButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				List<File> list = fileChooser.showOpenMultipleDialog(stage);
				if (list != null) {
					for (File file : list) {
						String folder = file.getParentFile().getAbsolutePath();
						String justName = file.getName();
						int pos = justName.lastIndexOf(".");
						justName = pos > 0 ? justName.substring(0, pos) : justName;
						Controller c = new Controller(folder, file.getAbsolutePath(), justName);
						try {
							c.Assemble();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		final GridPane inputGridPane = new GridPane();

		GridPane.setConstraints(openButton, 0, 0);
		inputGridPane.setHgap(6);
		inputGridPane.setVgap(6);
		inputGridPane.getChildren().add(openButton);
		inputGridPane.setPadding(new Insets(180, 0, 0, 180));
		openButton.setStyle("-fx-background-color: #8B008B;-fx-text-fill: white;");

		final Pane rootGroup = new VBox(12);
		rootGroup.getChildren().addAll(inputGridPane);
		rootGroup.setPadding(new Insets(25, 25, 25, 25));
		rootGroup.setStyle("-fx-background-color: #3b3a30;");

		stage.setScene(new Scene(rootGroup));
		stage.show();
	}

	private void setDimentions() {
		stage.setWidth(500);
		stage.setHeight(500);

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
