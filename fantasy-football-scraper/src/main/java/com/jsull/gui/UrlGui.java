package com.jsull.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jsull.page.Process;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UrlGui extends Application {
	
	public static final String PROMPT = "Enter player name to search for";
	public static final String FIRST_LABEL = "First name: ";
	public static final String LAST_LABEL = "Last name: ";
	public static final String BUTTON_TEXT = "Generate";
	
	private String[] tabArr = {"Player URL", "Player Details", "Fantasy Weeks", "Games", "Fantasy Stats"};
	private ExecutorService threadPool = Executors.newWorkStealingPool();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Fantasy Data Scraper");
		TabPane tabPane = new TabPane();
		BorderPane borderPane = new BorderPane();
		for (String tab : tabArr) {
			Tab t = new Tab(tab);
			t.setClosable(false);;
			tabPane.getTabs().add(t);
		}
		tabPane.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Tab>() {
					@Override
					public void changed(ObservableValue<? extends Tab> ov, Tab oldVal, Tab newVal) {
						System.out.println(String.format("Old value: %s\nNew value: %s\n", oldVal.getText(), newVal.getText()));
						GridPane grid = getGrid(newVal.getText());
						borderPane.setCenter(grid);
					}
				}
		);
		GridPane grid = getGrid(tabArr[0]);
		borderPane.setTop(tabPane);
		borderPane.setCenter(grid);
		StackPane root = new StackPane();
		root.getChildren().add(borderPane);
		primaryStage.setScene(new Scene(root, 600, 500));
		primaryStage.show();
	}
	
	public GridPane getGrid(String value) {
		GridPane grid = new GridPane();
		if (value.equals("Player URL")) {
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			Text text = new Text(PROMPT);
			Label firstLabel = new Label(FIRST_LABEL);
			Label lastLabel = new Label(LAST_LABEL);
			TextField firstTextField = new TextField();
			TextField lastTextField = new TextField();
			TextField urlField = new TextField();
			Button submit = new Button();
			submit.setText(BUTTON_TEXT);
			submit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					try {
						threadPool.execute(new Runnable() {
							@Override
							public void run() {
								urlField.clear();
								if (!firstTextField.getText().equals("") && !lastTextField.getText().equals("")) {
									Process service = new Process();
									String first = firstTextField.getText().trim();
									String last = lastTextField.getText().trim();
									String url = service.getEspnLinkForPlayer(first, last);
									urlField.setText(url);
								}
								// print out details to console
							}
						});
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			grid.add(text, 0, 0, 3, 1);
			grid.add(firstLabel, 0, 1);
			grid.add(firstTextField, 1,  1, 2, 1);
			grid.add(lastLabel, 0, 2);
			grid.add(lastTextField, 1, 2, 2, 1);
			grid.add(submit, 1,  3);
			grid.add(urlField, 0, 4, 3, 1);
		} else if (value.equals("Player Details")) {
			
		} else if (value.equals("Fantasy Weeks")) {
		
		} else if (value.equals("Games")) {
			
		} else if (value.equals("Players")) {
			
		}
		return grid;
	}
}
