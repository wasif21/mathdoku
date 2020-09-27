import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Cage 
{
	Integer goal;
	int gridSize;
	String operation;
	ArrayList<Cell> cells = new ArrayList<Cell>();
	
	public Cage(int goal, String operation)
	{
		this.goal = goal;
		this.operation = operation;
	}
	
	public void addCell(Cell cell)
	{
		cells.add(cell);
	}
	
	public void drawBorder(int gridSize)
	{
		boolean left2 = false;
		
		for (Cell cell : cells)
		{
			Integer id = Integer.parseInt(cell.getId());
			Integer leftCell = id - 1;
			Integer rightCell = id + 1;
			Integer aboveCell = id - gridSize;
			Integer belowCell = id + gridSize;
			
			boolean left = true;
			boolean right = true;
			boolean top = true;
			boolean bottom = true;
			
			for (Cell cell2 : cells)
			{
				if (cell2.getId().equals(belowCell.toString()))
					bottom = false;
				
				if (cell2.getId().equals(aboveCell.toString()))
					top = false;
					
				if (cell2.getId().equals(leftCell.toString()))
				{
					if (!left2)
						left = false;
					else
					{
						left = true;
						left2 = false;
					}
				}
							
				if (cell2.getId().equals(rightCell.toString())) 
					right = false;
				
				if (cell2.getId().equals(rightCell.toString()) && belowCell == rightCell + 1)
				{
					for (Cell cell3 : cells)
					{
						if (cell3.getId().equals(belowCell.toString()))
						{
							right = true;
							left2 = true;
						}
							
					}
				}
					
			}

			cell.setCage(left, right, top, bottom);	
		}
	}
	
	public void addText()
	{
		Text text = new Text(this.getText());
		text.setFont(Font.font("SanSerif", FontWeight.BOLD, 13));
		cells.get(0).getLabel().getChildren().add(text);
		cells.get(0).getChildren().add(cells.get(0).getLabel());
		cells.get(0).getText().toFront();

	}
	
	public ArrayList<Cell> getCells()
	{
		return cells;
	}
	
	public Boolean goalCheck()
	{
		int value = 0;
		
		switch (operation)
		{
			case "+":
				for (Cell cell : cells)
					if (!cell.getText().getText().isEmpty())
						value += Integer.parseInt(cell.getText().getText());
				if (value == goal)
					return true;
				else 
					return false;
			
			case "x":
				value = 1;
				for (Cell cell : cells)
				{
					if (!cell.getText().getText().isEmpty())
						value *= Integer.parseInt(cell.getText().getText());
				}
				if (value == goal)
					return true;
				else 
					return false;
			
			case "-":
				ArrayList<Integer> nums = new ArrayList<Integer>();
				
				for (Cell cell : cells)
				{
					if (!(cell.getText().getText().isEmpty()))
						nums.add(Integer.parseInt(cell.getText().getText()));
				}
				
				if (nums.size() == 1)
					return false;
				if (nums.size() == 0)
					return true;
				
				for (int i = 0; i < nums.size(); i++)
				{
					for (int j = 0; j < nums.size() - 1; j++)
					{
						if (nums.get(j) < nums.get(j+ 1))
						{
							int temp = nums.get(j);
							nums.set(j, nums.get(j+ 1));
							nums.set(j + 1, temp);
						}
					}
				}
				
				int result = nums.get(0);
				
				for (int i = 1; i < nums.size(); i++)
					result -= nums.get(i);
				
				if (result == goal)
					return true;
				else 
					return false;
				
				
			case "÷":
				ArrayList<Integer> nums2 = new ArrayList<Integer>();
				
				for (Cell cell : cells)
				{
					if (!cell.getText().getText().isEmpty())
						nums2.add(Integer.parseInt(cell.getText().getText()));
				}
				
				if (nums2.size() == 1)
					return false;
				if (nums2.size() == 0)
					return true;
				
				for (int i = 0; i < nums2.size(); i++)
				{
					for (int j = 0; j < nums2.size() - 1; j++)
					{
						if (nums2.get(j) < nums2.get(j+ 1))
						{
							int temp = nums2.get(j);
							nums2.set(j, nums2.get(j+ 1));
							nums2.set(j + 1, temp);
						}
					}
				}
				
				int result2 = nums2.get(0);
				
				for (int i = 1; i < nums2.size(); i++)
					result2 = result2 / nums2.get(i);

				System.out.println(result2);
				
				if (result2 == goal)
					return true;
				else 
					return false;
				
			case " ":
				if (cells.get(0).getText().getText().equals(goal.toString()))
						return true;
				else 
					return false;
				
			default:
				return false;
		}
	}
	
	public void highlight()
	{
		for (Cell cell : cells)
		{
			if (cell.getText().getText().isEmpty())
				break;
			cell.getRect().setFill(Color.YELLOW);
		}
	}
	
	public String getText()
	{
		return String.valueOf(goal) + operation;
	}
	
	public void changeFont(String size)
	{
		Text text = (Text)cells.get(0).getLabel().getChildren().get(0);
		
		switch (size)
		{
		case "Small":
			text.setFont(Font.font("SanSerif", FontWeight.BOLD, 13));
			break;
		
		case "Medium":
			text.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
			break;
			
		case "Large":
			text.setFont(Font.font("SansSerif", FontWeight.BOLD, 17));
			break;
			
		}
	}
	
	public boolean isEmpty()
	{
		int check = 0; 
		
		for (Cell cell : cells)
		{
			if (!cell.getText().getText().isBlank())
				check++;
		}
		
		if (check == cells.size())
			return false;
		
		return true;
	}
	
	
}
