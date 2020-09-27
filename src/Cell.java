import java.util.Stack;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Cell extends StackPane
{
	TextField text;
	Rectangle r;
	Line top = new Line();
	Line bottom = new Line();
	Line left = new Line();
	Line right = new Line();
	TextFlow label = new TextFlow();
	int width = 3;
	int size;
	Stack<String> sliderStack = new Stack<String>();
	Stack<String> redoStack = new Stack<String>();
	
	public Cell(int size, int i, int j, Integer id) 
	{
		this.size = size;
		
		r = new Rectangle(size, size);
		r.setFill(Color.WHITE);
		r.setStroke(Color.BLACK);

		text = new TextField();
		text.setId(i + " " + j);
		text.setAlignment(Pos.BOTTOM_CENTER);
		text.setShape(r);
		text.setMaxSize(size, size);
		text.setFont(new Font("SansSerif", 17));
		text.setStyle("-fx-text-box-border: transparent; -fx-focus-color: transparent; -fx-background-color: transparent; ");
		
		top.setStartX(1.5);
		top.setEndX(size - 2);
		top.setTranslateY(-(size/2) + 1);
		top.setStrokeWidth(width);
		top.setStroke(Color.TRANSPARENT);
		
		bottom.setStartX(1.5);
		bottom.setEndX(size - 2);
		bottom.setTranslateY(size/2 - 2);
		bottom.setStrokeWidth(width);
		bottom.setStroke(Color.TRANSPARENT);
		
		left.setStartY(1.5);
		left.setEndY(size - 2);
		left.setTranslateX(-(size/2) + 1);
		left.setStrokeWidth(width);
		left.setStroke(Color.TRANSPARENT);
		
		right.setStartY(1.5);
		right.setEndY(size - 2);
		right.setTranslateX(size/2 - 2);
		right.setStrokeWidth(width);
		right.setStroke(Color.TRANSPARENT);
		
		label.setPadding(new Insets(6));
		
		this.getChildren().addAll(r, top, left, right, bottom, text);
		this.setId(id.toString());
		
		redoStack.push("");
	}
	
	public TextField getText()
	{
		return text;
	}
	
	public Rectangle getRect()
	{
		return r;
	}
	
	public void changeFont(String size)
	{
		switch (size)
		{
		case "Small":
			text.setFont(new Font("SansSerif", 17));
			break;
		
		case "Medium":
			text.setFont(new Font("SansSerif", 19));
			break;
			
		case "Large":
			text.setFont(new Font("SansSerif", 21));
			break;
			
		}
	}
	
	public void setCage(boolean left, boolean right, boolean top, boolean bottom)
	{
		if (top)
			this.top.setStroke(Color.BLACK);
		if (left)
			this.left.setStroke(Color.BLACK);
		if (right)
			this.right.setStroke(Color.BLACK);
		if (bottom)
			this.bottom.setStroke(Color.BLACK);
		
	}
	
	public TextFlow getLabel()
	{
		return label;
	}
	
	public void reset()
	{
		if (!label.getChildren().isEmpty())
		{
			Text text = (Text)label.getChildren().get(0);
			text.setText("");
		}
		
		top.setStroke(Color.TRANSPARENT);
		left.setStroke(Color.TRANSPARENT);
		right.setStroke(Color.TRANSPARENT);
		bottom.setStroke(Color.TRANSPARENT);
		
		sliderStack.clear();
	}
	
	public Stack<String> getStack()
	{
		return sliderStack;
	}
	
	public void stackPush(String num)
	{
		sliderStack.push(num);
	}
	
	public void redoPush(String val)
	{
		redoStack.push(val);
	}
	
	public boolean cageCheck()
	{
		if (top.getStroke().equals(Color.BLACK) || bottom.getStroke().equals(Color.BLACK) || 
				left.getStroke().equals(Color.BLACK) || right.getStroke().equals(Color.BLACK))
			return true;
		
		return false;
	}
	
}
