import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import java.util.regex.*;

import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class Mathdoku extends Application
{
	private TextField currentCell = null;
	private String currentVal = null;
	private ArrayList<TextField> cells = new ArrayList<TextField>();
	private Integer num = 8;
	private int size = 60;
	private Stack<TextField> cellStack = new Stack<TextField>();
	private Stack<TextField> redoStack = new Stack<TextField>();
	private ArrayList<Cell> cells2 = new ArrayList<Cell>();
	private ArrayList<Cage> cages = new ArrayList<Cage>();
	private File file;
	private boolean loadCheck = true;
	private BorderPane pane = new BorderPane();
	private LongButton undo = new LongButton("Undo");
	private LongButton redo = new LongButton("Redo");
	private LongButton mistake = new LongButton("Check for mistakes");
	private LongButton clear = new LongButton("Clear board");
	private Slider slider;
	
	public void start(Stage stage)
	{
		HBox hbox = new HBox(10);
		HBox extensions = new HBox(10);
		VBox vbox = new VBox(-10);
		
		//Buttons
		LongButton loadFile = new LongButton("Load from file");
		LongButton loadText = new LongButton("Load from text");
		LongButton random = new LongButton("Generate random game");
		
		Text text1 = new Text("                                                         ");
		Text text2 = new Text("                                                ");
		
		undo.setDisable(true);
		redo.setDisable(true);
		clear.setDisable(true);
		mistake.setDisable(true);
		
		Text prompt = new Text("Please load a Mathdoku game");
		prompt.setFont(Font.font("Arial", FontWeight.BOLD, 30));
		
		ChoiceBox<String> font = new ChoiceBox<String>(FXCollections.observableArrayList("Font size", "Small", "Medium", "Large")); 
		font.setValue("Font size");
		
		hbox.getChildren().addAll(undo, redo, clear, mistake, loadFile, loadText);
		hbox.setPadding(new Insets(20));
		hbox.setAlignment(Pos.CENTER);
		
		extensions.getChildren().addAll(text2, font, random, text1);
		extensions.setAlignment(Pos.CENTER);
		extensions.setPadding(new Insets(0, 20, 20, 20));
		
		HBox.setHgrow(undo, Priority.ALWAYS);
		HBox.setHgrow(redo, Priority.ALWAYS);
		HBox.setHgrow(clear, Priority.ALWAYS);
		HBox.setHgrow(mistake, Priority.ALWAYS);
		HBox.setHgrow(loadFile, Priority.ALWAYS);
		HBox.setHgrow(loadText, Priority.ALWAYS);
		HBox.setHgrow(font, Priority.ALWAYS);
		HBox.setHgrow(random, Priority.ALWAYS);
		
		vbox.getChildren().addAll(hbox, extensions);

		//Slider
		slider = new Slider(0, num, 1);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setBlockIncrement(1);
		slider.setSnapToTicks(true);
		slider.setOrientation(Orientation.VERTICAL);
		slider.setPadding(new Insets(15));
		slider.setLabelFormatter(new StringConverter<Double>() {
            public String toString(Double n) 
            {
                if (n == 0) 
                	return "Clear";
                else if (n == 1)
                	return "1";
                else if (n == 2)
                	return "2";
                else if (n == 3)
                	return "3";
                else if (n == 4)
                	return "4";
                else if (n == 5)
                	return "5";
                else if (n == 6)
                	return "6";
                else if (n == 7)
                	return "7";
                else if (n == 8)
                	return "8";
                else
                	return n.toString();
            }
            
            public Double fromString(String s)
            {
            	if (s == "Clear")
            		return 0d;
            	else
            		return Double.parseDouble(s);
            }
		});
		
		pane.setCenter(prompt);
		pane.setBottom(vbox);
		pane.setPrefSize(800, 700);
		pane.setRight(slider);
		//pane.setTop(prompt);
		
		Scene scene = new Scene(pane);
		stage.setTitle("Mathdoku");
		stage.setScene(scene);
		stage.show();
		
		for (Cell cell : cells2)
		{
			cell.setOnMouseEntered(e -> cell.getRect().setFill(Color.LIGHTBLUE));
			cell.setOnMouseExited(e -> cell.getRect().setFill(Color.WHITE));
		}
		
		//Clicking buttons
		
		//Looks for mistakes
		mistake.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent e)
			{
				int colCheck = 0;
				int rowCheck = 0;
				int cageCheck = 0;
				int numCheck = 0;
				
				for(Cell cell : cells2)
					cell.getRect().setFill(Color.WHITE);
				
				for(int i = 0; i<= num; i++)
				{
					if(findRowDuplicate(i))
					{
						for(Cell cell : cells2)
						{
							if (cell.getText().getId().startsWith(Integer.toString(i)))
								cell.getRect().setFill(Color.YELLOW);
						}
						
						rowCheck++;
					}
				}
				
				for(int j = 0; j<= num; j++)
				{
					if(findColDuplicate(j))
					{
						for(Cell cell : cells2)
						{
							if (cell.getText().getId().endsWith(Integer.toString(j)))
								cell.getRect().setFill(Color.YELLOW);
						}
						
						colCheck++;
					}
				}
				
				for (Cage cage : cages)
				{
					if(!cage.goalCheck())
					{
						cageCheck++;
						
						if (!cage.isEmpty())
							cage.highlight();
					}
				}
				
				for (Cell cell : cells2)
				{
					if (!cell.getText().getText().isEmpty() && Integer.parseInt(cell.getText().getText()) > num)
						cell.getRect().setFill(Color.YELLOW);
					else
						numCheck++;
				}
				
				if (colCheck == 0 && rowCheck == 0 && cageCheck == 0 && numCheck == cells2.size())
				{
					Alert alert = new Alert(AlertType.INFORMATION, "You have successfully solved this Mathdoku puzzle!");
					alert.setTitle("Well done!");
					alert.setHeaderText("Congratulations!");
					alert.showAndWait();
					
					animation();
					undo.setDisable(true);
					redo.setDisable(true);
					clear.setDisable(true);
					mistake.setDisable(true);
				}
					
			}
		});
		
		//Clear board
		clear.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent e) 
			{
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to clear the grid?");
				
				alert.setTitle("Clear board");
				
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.isPresent() && result.get() == ButtonType.OK)
					clearBoard();
	
			}

		});
		
		//Undo
		undo.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent e) 
			{
				if(!cellStack.isEmpty())
				{
					TextField cell = cellStack.pop();
					String val = cell.getText();
					Cell sliderCell = (Cell)cell.getParent();
					
					if(cell.isUndoable()) //&& !cell.getText().isEmpty())
					{
						cell.undo();
						redoStack.push(cell);
						sliderCell.redoPush(val);
						//System.out.println(cellStack);
					}
					
					else
					{
						if (!sliderCell.getStack().isEmpty())
							cell.setText(sliderCell.getStack().pop());
						
						redoStack.push(cell);
						sliderCell.redoPush(val);
					}
					
					//System.out.println(cell);
				}
				
				else if (!cells.get(0).getText().isBlank() && cells.get(0).isUndoable())
				{
					cells.get(0).undo();
					redoStack.push(cells.get(0));
				}
				
				//System.out.println(cellStack);
			}
		});
		
		undo.setOnMouseReleased(e ->
		{
			if (redoStack.isEmpty())
				redo.setDisable(true);
			else
				redo.setDisable(false);
			
			if (cellStack.isEmpty())
				undo.setDisable(true);
			else
				undo.setDisable(false);
		});
		
		undo.setOnMouseMoved(e ->
		{
			if (currentCell != null && !currentCell.getText().isEmpty() && !cellStack.peek().equals(currentCell))
			{
				cellStack.push(currentCell);
				Cell c = (Cell)currentCell.getParent();
				c.stackPush(currentVal);
				currentVal = currentCell.getText();
				//System.out.println(currentCell.getText());
			}
		});
		
		redo.setOnMouseReleased(e ->
		{
			if (redoStack.isEmpty())
				redo.setDisable(true);
			else
				redo.setDisable(false);
			
			if (cellStack.isEmpty())
				undo.setDisable(true);
			else
				undo.setDisable(false);
		});
		
		//Redo
		redo.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent e)
			{
				if(!redoStack.isEmpty())
				{
					TextField cell = redoStack.pop();
					if (cell.isRedoable())
						cell.redo();
					else
					{
						Cell sliderCell = (Cell)cell.getParent();
						if (!sliderCell.redoStack.isEmpty())
							cell.setText(sliderCell.redoStack.pop());
					}
				}
				
			}
		});
		
		//Change font size
		font.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() 
		{
			public void changed(ObservableValue<? extends Number> ov, Number oldSelected, Number newSelected) 
			{
				String size = font.getItems().get(newSelected.intValue());
				changeFont(size);
			}
			
		});
		
		loadFile.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)	
			{
				FileChooser chooser = new FileChooser();
				chooser.getExtensionFilters().add(new ExtensionFilter("Text files", "*.txt"));
				file = chooser.showOpenDialog(stage);
				
				if (!(file== null) && file.exists() && file.canRead())
				{
					try
					{
						reset();
						
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String line;
						
						ArrayList<String> lines = new ArrayList<String>();
						
						while ((line = reader.readLine()) != null)
						{
							lines.add(line);
						}
						
						reader.close();
						
						String[] array = lines.toArray(new String[lines.size()]);
						getGridSize(array);
						loadGrid();
						
						for (String line2 : lines)
							loadPuzzle(line2);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Invalid puzzle");
						alert.setHeaderText("There was an error trying to load this puzzle");
						alert.setContentText("Please try again");
						alert.show();
						
						pane.setCenter(prompt);
					}
				}
				

				if (!loadCheck)
				{
					reset();
					loadCheck = true;
				}
			}
		});
		
		
		loadText.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)	
			{
				String puzzle = null;
				
				Dialog<ButtonType> dialog = new Dialog<>();
				dialog.setTitle("Load Mathdoku");
				dialog.setHeaderText("Please enter a text-based Mathdoku puzzle");
				dialog.setResizable(true);
				
				dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
				
				GridPane input = new GridPane();
				TextArea text = new TextArea();
				input.setPadding(new Insets(20));
				
				input.getChildren().add(text);
				GridPane.setHgrow(text, Priority.ALWAYS);
				GridPane.setVgrow(text, Priority.ALWAYS);
				
				dialog.getDialogPane().setContent(input);
				
				Optional<ButtonType> result = dialog.showAndWait();
				
				if (result.get() == ButtonType.OK && !text.getText().isEmpty())
				{
					puzzle = text.getText();
					
					reset();
					clearBoard();
				}
				
				if (puzzle != null)
				{
					String[] lines = puzzle.split("\\r?\\n");
					
					try
					{
						getGridSize(lines);
						loadGrid();
					
						for (String line : lines)
							loadPuzzle(line);
						
					}
					catch (Exception e)
					{
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Invalid puzzle");
						alert.setHeaderText("There was an error trying to load this puzzle");
						alert.setContentText("Please try again");
						
						alert.show();
						
						//pane.setCenter(prompt);
					}
				}
				
				if (!loadCheck)
				{
					reset();
					loadCheck = true;
				}
			}
		});
		
		random.setOnAction(e ->
		{
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Random game");
			dialog.setContentText("Please enter the grid size");
			dialog.setHeaderText("Creating a random game");
			
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent())
			{
				try
				{
					num = Integer.parseInt(result.get());
					randomGame();
				}
				catch (Exception error)
				{
					error.printStackTrace();
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Invalid puzzle");
					alert.setHeaderText("There was an error trying to load this puzzle");
					alert.setContentText("Please try again");
					alert.show();
					
					pane.setCenter(prompt);
				}
			}
		});
		
	}
	
	public void randomGame()
	{
		Random rand = new Random();
		String[] operations = {"+", "-", "x", "÷"};
		ArrayList<String> lines = new ArrayList<String>();
		
		reset();
		clearBoard();
		loadGrid();
		
		int count = 0; //Counts number of cells
		ArrayList<Integer> last = new ArrayList<Integer>();
		
		while (count < num * num)
		{
			String line = new String();
			line += ((rand.nextInt(50) + 1) + "");
			line += (operations[rand.nextInt(4)] + " ");

			ArrayList<Integer> list = new ArrayList<Integer>(); //Stores cells in cage as they're added
			
			boolean empty = false;
			int finalCount = 0;
			
			for (int j = 0; j <= rand.nextInt(4); j++)
			{
				boolean duplicate = true;
				boolean firstCell = false; 
				int searchCount = 0;
				
				if (count == num * num)
					duplicate = false;
				
				while (duplicate)
				{
					Integer randCell = rand.nextInt(num * num) + 1;
					int check = 0;
					boolean adj = false;
					
					Pattern p1 = Pattern.compile("," + randCell + "\\d");
					Pattern p2 = Pattern.compile(" " + randCell + "\\d");
					
					if (!lines.isEmpty()) //Checks if there's a duplicate from prev lines
					{
						for (String cage : lines)
						{
							Matcher m1;
							if (cage.lastIndexOf(",") != -1)
								m1 = p1.matcher(cage.substring(cage.lastIndexOf(",")));
							else
								m1 = p1.matcher(cage);
							
							Matcher m2; 
							if (cage.indexOf(",") == -1)
								m2 = p2.matcher(cage.substring(cage.indexOf(" ")));
							else
								m2 = p2.matcher(cage.substring(cage.indexOf(" "), cage.indexOf(",")));
						
							
							if ( !cage.contains("," + randCell) && !cage.contains("," + randCell + ",") && !cage.contains(" " + randCell) )
								check++;
							else if (!cage.contains("," + randCell + ",") && (m1.matches() || m2.matches()))
							{
								check++;
							}
						}
						
						if (check == lines.size() && !line.contains("," + randCell + ",") && !line.contains(" " + randCell)) //Also checks current line
						{
							duplicate = false;
							//line += (randCell + ",");
							//count++;
						}
						else if (check == lines.size() && !line.contains("," + randCell + ",") && p2.matcher(line).results().count() > 0)
						{
							duplicate = false;
						}
							
					}
					
					else if ( !line.contains("," + randCell + ",") && !line.contains(" " + randCell) )
					{
						duplicate = false;
						//line += (randCell + ",");
						//count++;
					}
					else if (!line.contains("," + randCell + ",") && p2.matcher(line).matches())
						duplicate = false;
					
					if (last.contains(randCell))
						duplicate = true;
					
					if (j == 0 && !duplicate)
					{
						line += (randCell + ",");
						count++;
						//cellCount++;
						list.add(randCell);
						firstCell = true;
						
						if (randCell <= 6)
							last.add(randCell);
					}
					else if (!duplicate)
					{
						for (Integer cell : list)
						{
							if (randCell == cell - 1 || randCell == cell + 1 || randCell == cell - num || randCell == cell + num)
							{
								adj = true;
								break;
							}
							else
								adj = false;
						}
						
						boolean end = false;
						boolean start = false;
						
						if (randCell % num == 0)
							end = true;
						
						if (randCell % num == 1)
							start = true;
						
						if ( (end && list.contains(randCell + 1)) || (start && list.contains(randCell - 1)) )
							adj = false;
					}
					
					if (!duplicate)
					{
						if (adj && !firstCell)
						{
							line += (randCell + ",");
							count++;
							//cellCount++;
							list.add(randCell);
							
							if (randCell <= 6)
								last.add(randCell);
						}
						else if (!firstCell)
							duplicate = true;
					}
					
					if (duplicate)
					{
						searchCount++;
						
						if (j == 0)
							finalCount++;
					}
					
					if (searchCount >= 900)
					{
						duplicate = false;
						
						if (j == 0)
						{
							empty = true;
							//finalCount++;
						}
					}
					
					
					if (finalCount >= 899 && j == 0)
					{
						int x = 0;
						for (int i = 1; i <= 6; i++)
						{
							if (!last.contains(i))
								x = i;
						}					
						
						duplicate = false;
						empty = false;
						line += x + ",";
						count++;
						list.add(x);
						j = 4;
						last.add(x);
					} 
				}
			}
			
			if (!empty)
			{
				line = line.substring(0, line.length() - 1);
				lines.add(line);	
				//System.out.println(line);
			}
			
		}
		
		//System.out.println();
		for (String line : lines)
		{
			if (!line.contains(","))
				line = line.substring(0, line.indexOf(" ") - 1) + line.substring(line.indexOf(" "));
			
			loadPuzzle(line);
		}
	}
	
	public boolean canExist(int randCell)
	{
		if (randCell <= 0 || randCell > num * num)
			return false;
		
		return true;
	}
	
	public void getGridSize(String[] lines)
	{
		ArrayList<Integer> nums = new ArrayList<Integer>();
		
		for (String line : lines)
		{
			String cells = line.substring(line.indexOf(" ") + 1, line.length());
			ArrayList<String> ids = new ArrayList<String>();
			
			while(cells.contains(","))
			{
				int index = cells.indexOf(",");
				ids.add(cells.substring(0, index));
				cells = cells.substring(index + 1);
			}
			ids.add(cells);
			
			for (String id : ids)
				nums.add(Integer.parseInt(id));
		}
		
		for (int i = 0; i < nums.size(); i++)
		{
			for (int j = 0; j < nums.size() - 1; j++)
			{
				if (nums.get(j) > nums.get(j+ 1))
				{
					int temp = nums.get(j);
					nums.set(j, nums.get(j+ 1));
					nums.set(j + 1, temp);
				}
			}
		}
		
		double prime = Math.sqrt(nums.get(nums.size()- 1));
		if (prime == 2 || prime == 3 || prime == 4 || prime == 5 || prime == 6 || prime == 7 || prime == 8)
			num = (int)prime;
		else
			num = 0; 
			
	}
	
	public class CellHandler implements EventHandler<MouseEvent>
	{
		public void handle(MouseEvent event)
		{
			if (redoStack.isEmpty())
				redo.setDisable(true);
			else
				redo.setDisable(false);
			
			if (cellStack.isEmpty())
				undo.setDisable(true);
			else
				undo.setDisable(false);
			
			//Stack handling
			if (cellStack.isEmpty() && !cells.get(0).getText().isBlank())
				cellStack.push(cells.get(0)); 
			
			//if(currentCell != null)
				//prevCell = currentCell;
			
			if (currentCell != null && !currentCell.getText().isEmpty() && !cellStack.isEmpty() && !cellStack.peek().equals(currentCell))
			{
				cellStack.push(currentCell);
				Cell c = (Cell)currentCell.getParent();
				c.stackPush(currentVal);
				//System.out.println(currentCell.getText());
			}
			
			currentCell = (TextField)event.getSource();
			currentVal = currentCell.getText();
			
			slider.valueProperty().addListener(new ChangeListener<Number>() 
			{ 
	            public void changed(ObservableValue <? extends Number > observable, Number oldValue, Number newValue) 
	            { 
        			//Cell cell = (Cell)currentCell.getParent();
        			
	            	if(currentCell != null)
	            	{
	            		if(newValue.intValue() == 0)
	            			currentCell.clear();
	            		
	            		else
	            			currentCell.setText(Integer.toString(newValue.intValue()));
	            		 
	            	}
	            	
	            } 
	        }); 
			
			slider.setOnMouseReleased(e ->
			{
				Cell cell = (Cell)currentCell.getParent();
				
				if (currentCell != null && !currentCell.getText().equals(currentVal))
				{
					cellStack.push(currentCell);
					cell.stackPush(currentVal);
					//System.out.println(cellStack.peek().getText());
					currentVal = currentCell.getText();
				}
			});
		}
	}

	public void loadGrid()
	{
		cells2.clear();
		cages.clear();
		cells.clear();
		cellStack.clear();
		redoStack.clear();
		currentCell = null;
		currentVal = null;
		
		Integer id = 0;
		GridPane grid = new GridPane();
		
		for(int j = 0; j <= num - 1; j++)
		{
			for (int i = 0; i <= num - 1; i++)
			{
				id++;
				Cell c = new Cell(size, i, j, id);
				
				grid.add(c, i, j);
				cells2.add(c);
				cells.add(c.getText());
				
				if(c.getText().getId().equals("1 1"))
					currentCell = c.getText();
				
				TextField text = c.getText();
				
				text.setOnMouseClicked(new CellHandler()); 
				text.setOnMouseExited(e -> 
				{
					if (redoStack.isEmpty())
						redo.setDisable(true);
					else
						redo.setDisable(false);
					
					if (cellStack.isEmpty())
						undo.setDisable(true);
					else
						undo.setDisable(false);
					
					if (currentCell != null && currentVal != null && text.equals(currentCell) && !text.getText().equals(currentVal) && 
							(!text.getText().isEmpty() || !currentVal.isEmpty()))
					{
						cellStack.push(text);
						c.stackPush(currentVal);
						//System.out.println(cellStack.peek().getText());
						currentVal = text.getText();
					}
				});
				
			}
		}
		
		grid.setAlignment(Pos.CENTER);
		grid.setMaxSize((num + 1.5) * size, (num + 1.5) * size);
		grid.setMinSize((num + 1.5) * size, (num + 1.5) * size);
		grid.setVgap(-1);
		grid.setHgap(-1);
		
		if(num % 2 == 1)
		{
			grid.setVgap(-2);
			grid.setHgap(-2);
		}
		
		GridPane.setHgrow(grid, Priority.ALWAYS);
		
		pane.setCenter(grid);
		//grid.setGridLinesVisible(true);
		slider.setMax(num);
		clear.setDisable(false);
		mistake.setDisable(false);
	}
	
	public void loadPuzzle(String line)
	{
		String label = line.substring(0, line.indexOf(" "));
		String cells = line.substring(line.indexOf(" ") + 1, line.length());
		String operation = label.substring(label.length() - 1);
		String number = label.substring(0, label.length() - 1);
		int errorCount = 0;
		boolean newLine = false;
		
		try
		{
			Integer.parseInt(number);
		}
		catch (Exception e)
		{
			if (label.endsWith("+") || label.endsWith("-") || label.endsWith("x") || label.endsWith("·"))
				number = number.substring(0, number.length() - 1);
			else
				number = label;
		}
		
		if (operation.equals("·")) 
			operation = "÷";
		if (operation.equals(label))
			operation = " ";
		
		Cage cage= new Cage(Integer.parseInt(number), operation);
		cages.add(cage);
		
		ArrayList<String> ids = new ArrayList<String>();
		
		while(cells.contains(","))
		{
			int index = cells.indexOf(",");
			ids.add(cells.substring(0, index));
			cells = cells.substring(index + 1);
		}
		ids.add(cells);
		
		ArrayList<Integer> nums = new ArrayList<Integer>();
		int check = 0;
		boolean exists = false;
		
		for (String id : ids)
			nums.add(Integer.parseInt(id));
		
		if (nums.size() == 1)
			check = 1;
		else
		{
			for (Integer id : nums)  //For each cell in the cage line
			{
				for (int i = 0; i < nums.size(); i++)
				{
					if (id != nums.get(i) && (nums.get(i) == id + 1 || nums.get(i) == id - 1 || nums.get(i) == id + num || nums.get(i) == id - num))
						check++;
				}
			}
		}
		
		for (String id : ids) //Checks if a cell has already been referenced
		{
			for (Cage cage2 : cages)
			{
				for (Cell cell : cage2.getCells())
				{
					if (id.equals(cell.getId()))
						exists = true;
				}
			}
		}
		
		//Checks if a cell directly at the end of the grid doesn't connect with 
		//the first cell of the next line of the grid.
		for (int cell : nums) 
		{
			boolean end = false;
			
			if (cell % num == 0 && num >= 3)
				end = true;
			
			if (end)
			{
				if (nums.contains(cell + 1))
					newLine = true;
			}
		}
		
		
		if (check >= ids.size() && !exists && !newLine)		
			setCells(cage, ids);
		else
			errorCount++;
		
		if (errorCount > 0)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid puzzle");
			alert.setHeaderText("There was an error trying to load this puzzle");
			alert.setContentText("Please try again");
			
			alert.show();
			
			loadCheck = false;
			
		}
	}
	
	public void setCells(Cage cage, ArrayList<String> ids)
	{
		for (Cell cell : cells2)
		{
			if (ids.contains(cell.getId()))
				cage.addCell(cell);
		}
		
		cage.drawBorder(num);
		cage.addText();
		
	}
	
	public void reset()
	{
		if (!cages.isEmpty())
		{
			for (Cage cage : cages)
			{
				for (Cell cell : cage.getCells())
					cell.reset();
				
				cage.getCells().clear();
			}
			
			cages.clear();
		}
	}
	
	public void changeFont(String size)
	{
		for (Cell cell : cells2)
			cell.changeFont(size);
		
		for (Cage cage : cages)
			cage.changeFont(size);
	}
	
	public void clearBoard()
	{
		for(Cell cell : cells2)
		{
			cell.getText().clear();
			cell.getRect().setFill(Color.WHITE);
		}
		
		cellStack.clear();
		redoStack.clear();
		currentVal = null;
		currentCell = null;
	}
	
	
	public Boolean findRowDuplicate(int rowNum)
	{
		ArrayList<TextField> row = new ArrayList<TextField>();
		
		for(TextField cell : cells)
		{
			if (cell.getId().startsWith(Integer.toString(rowNum)) && !cell.getText().isBlank())
				row.add(cell);
		}
		
		if(row.size() >= 2)
		{
			for(TextField value : row) 
			{
				for(int x = 0; x < row.size(); x++)
				{
					if(row.get(x) != value && value.getText().equals(row.get(x).getText()))
						return true;
				}
			}
		}
		
		return false;
		
	}
	
	public Boolean findColDuplicate(int colNum)
	{
		ArrayList<TextField> column = new ArrayList<TextField>();
		
		for(TextField cell : cells)
		{
			if (cell.getId().endsWith(Integer.toString(colNum)) && !cell.getText().isBlank())
				column.add(cell);
		}
		
		if(column.size() >= 2)
		{
			for(TextField value : column) 
			{
				for(int x = 0; x < column.size(); x++)
				{
					if(column.get(x) != value && value.getText().equals(column.get(x).getText()))
						return true;
				}
			}
		}
		
		return false;
		
	}
	
	class LongButton extends Button
	{
		public LongButton(String label)
		{
			super(label);
		    setMaxWidth(Double.MAX_VALUE);
		    setMinWidth(Double.NEGATIVE_INFINITY);
		}
	}
	
	//Animation extension
	public void animation()
	{
		ArrayList<ArrayList<Cell>> grid = new ArrayList<ArrayList<Cell>>();
		
		for (int i = 0; i < num; i++)
			grid.add(new ArrayList<Cell>());
		
		for (Integer rowCol = 0; rowCol < num; rowCol++)
		{
			Integer n = num - 1 - rowCol;
			for (Cell cell : cells2)
			{
				String id = cell.getText().getId();
				if (id.startsWith(rowCol.toString()) || id.endsWith(rowCol.toString()) || id.startsWith(n.toString()) || id.endsWith(n.toString()))
				{
					if (rowCol == 0)
						grid.get(rowCol).add(cell);
					else
					{
						int count = 0;
						for (ArrayList<Cell> list : grid)
						{
							if (!list.isEmpty() && list.contains(cell))
								count++;
						}
						
						if (count == 0)
							grid.get(rowCol).add(cell);
					}
				}
			}
		}
		
		for (Cell cell : grid.get(0))
		{
			animate1(cell);
			cell.getRect().setFill(Color.RED);
		}
		
		for (Cell cell : grid.get(1))
		{
			animate2(cell);
			cell.getRect().setFill(Color.BLUE);
		}
		
		if (grid.size() >= 3)
		{
			for (Cell cell : grid.get(2))
			{
				animate3(cell);
				cell.getRect().setFill(Color.GREEN);
			}
				
		}
		
		if (grid.size() >= 4)
		{
			for (Cell cell : grid.get(3))
			{
				animate4(cell);
				cell.getRect().setFill(Color.ORANGE);
			}
		}
		
		if (grid.size() >= 5)
		{
			for (Cell cell : grid.get(4))
			{
				animate1(cell);
				cell.getRect().setFill(Color.RED);
			}
		}
		
		if (grid.size() >= 6)
		{
			for (Cell cell : grid.get(5))
			{
				animate2(cell);
				cell.getRect().setFill(Color.BLUE);
			}
		}
		
		if (grid.size() >= 7)
		{
			for (Cell cell : grid.get(6))
			{
				animate3(cell);
				cell.getRect().setFill(Color.GREEN);
			}
		}
		
		if (grid.size() >= 8)
		{
			for (Cell cell : grid.get(7))
			{
				animate4(cell);
				cell.getRect().setFill(Color.ORANGE);
			}
		}
		
	}
	
	public void animate1(Cell cell)
	{
		SequentialTransition sequel = new SequentialTransition();
		sequel.setNode(cell.getRect());
		sequel.setCycleCount(FillTransition.INDEFINITE);
		
		FillTransition fill1 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill1.setFromValue(Color.RED);
		fill1.setToValue(Color.BLUE);
		
		FillTransition fill2 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill2.setToValue(Color.GREEN);
		
		FillTransition fill3 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill3.setToValue(Color.ORANGE);
		
		FillTransition fill4 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill4.setToValue(Color.RED);
		
		//FillTransition fill5 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill4.setToValue(Color.RED);
		
		sequel.getChildren().addAll(fill1, fill2, fill3, fill4);
		sequel.play();
	}
	
	public void animate2(Cell cell)
	{
		SequentialTransition sequel = new SequentialTransition();
		sequel.setNode(cell.getRect());
		sequel.setCycleCount(FillTransition.INDEFINITE);
		
		FillTransition fill1 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill1.setToValue(Color.BLUE);
		
		FillTransition fill2 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill2.setFromValue(Color.RED);
		fill2.setToValue(Color.GREEN);
		
		FillTransition fill3 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill3.setToValue(Color.ORANGE);
		
		FillTransition fill4 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill4.setToValue(Color.RED);
		
		//FillTransition fill5 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill4.setToValue(Color.RED);
		
		sequel.getChildren().addAll(fill2, fill3, fill4, fill1);
		sequel.play();
	}
	
	public void animate3(Cell cell)
	{
		SequentialTransition sequel = new SequentialTransition();
		sequel.setNode(cell.getRect());
		sequel.setCycleCount(FillTransition.INDEFINITE);
		
		FillTransition fill1 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill1.setFromValue(Color.RED);
		fill1.setToValue(Color.BLUE);
		
		FillTransition fill2 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill2.setToValue(Color.GREEN);
		
		FillTransition fill3 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill3.setFromValue(Color.RED);
		fill3.setToValue(Color.ORANGE);
		
		FillTransition fill4 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill4.setToValue(Color.RED);
		
		//FillTransition fill5 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill4.setToValue(Color.RED);
		
		sequel.getChildren().addAll(fill3, fill4, fill1, fill2);
		sequel.play();
	}
	
	public void animate4(Cell cell)
	{
		SequentialTransition sequel = new SequentialTransition();
		sequel.setNode(cell.getRect());
		sequel.setCycleCount(FillTransition.INDEFINITE);
		
		FillTransition fill1 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill1.setFromValue(Color.RED);
		fill1.setToValue(Color.BLUE);
		
		FillTransition fill2 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill2.setToValue(Color.GREEN);
		
		FillTransition fill3 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill3.setToValue(Color.ORANGE);
		
		FillTransition fill4 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		fill4.setToValue(Color.RED);
		//fill4.setFromValue(Color.RED);
		
		//FillTransition fill5 = new FillTransition(Duration.seconds(0.25), cell.getRect());
		//fill4.setToValue(Color.RED);
		
		sequel.getChildren().addAll(fill4, fill1, fill2, fill3);
		sequel.play();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
	

}
