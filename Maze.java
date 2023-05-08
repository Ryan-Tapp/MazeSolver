/*Ryan Tapp Maze Project
 * 
 * This program allows the user to create a maze of any dimensions (20 and lower is recommend),
 * build and edit it themselves and then try to solve it.
 * Unfortunately the self solving feature is buggy and unreliable so I wouldnt recommend trying that.
 * (Also small glitch when the empty grid is first generated the size may look a little off, to fix this just ajust the size of the window and it fixes on its own)
 * 
 * */


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


final class Maze extends JFrame implements ActionListener // what needs to be added to create a graphical interface
{
	// variables used to check what the last col and row placed by the user's solution path was
	private int lastCol = 0; 
	private int lastRow = 0;
	
	//variable used to check where the user currently was in their solution path (0 is at start, 1 is in middle, returns to 0 once end is reached)
	private int userPath = 0;
	
	//start, end and save state deterimes state of each button (pressed once or twice)
	private int startState = 0;
	private int endState = 0;
	private int saveState = 0;
	
	//currentState is sate of the current program (is the maze saved or in edit mode)
	private int currentState = -1;
	
	//these 2 array acted as the virtual mazes of the program where 0 meant no walls, 1 meant wall, etc...
	private int[][] wallState;
	private int[][] virtualMaze;
	
	//dimensions of maze
	private int height;
	private int width;
	
	//GUI variables
	private JFrame frame;
	private JPanel mazePanel;
	private JPanel textPanel;
	private JPanel buttonPanel;
	private JButton start;
	private JButton end;
	private JButton save;
	private JButton[][] walls;
	private JTextField msgArea;
	private Font msgFont;
	private Scanner in = new Scanner(System.in);
	
	//colors
	private static final Color black = new Color(0,0,0);
	private static final Color lightBlue = new Color(204,204,255);
	private static final Color purple = new Color(128,0,128);
	
	
	public Maze()
	{
		//ask user for dimesnions of maze
		System.out.println("Enter the height of the maze");
		
		height = in.nextInt();
		
		System.out.println("Enter the width of the maze");
		
		width = in.nextInt();
		
		//intialize graphical and virtual mazes
		walls = new JButton[height][width];
		wallState = new int[height][width];
		virtualMaze = new int[height][width];
		
		//intializing variables
		frame = new JFrame();
		
		mazePanel = new JPanel();
		mazePanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		mazePanel.setLayout(new GridLayout(height,width));
		mazePanel.setPreferredSize(new Dimension(750,750));
		
		textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		
		
		msgArea = new JTextField("Welcome to Maze Creator (Click a panel to add a wall)");
		msgArea.setFont(msgFont);
		msgArea.setBackground(lightBlue);
		msgArea.setMargin(new Insets(10,5,10,5));
		msgArea.setHorizontalAlignment(JTextField.CENTER);
		msgArea.setEditable(false);
		
		textPanel.add(msgArea,BorderLayout.CENTER);
		add(textPanel, BorderLayout.NORTH);
		add(mazePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setResizable(true);
		setTitle("MAZE EDITOR");
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        save = new JButton("Save Maze");
		save.addActionListener(this);
		buttonPanel.add(save, BorderLayout.CENTER);
		
		end = new JButton("Intialize ending of the maze");
		end.addActionListener(this);
		buttonPanel.add(end, BorderLayout.WEST);
		
		start = new JButton("Intialize start of the maze");
		start.addActionListener(this);
		buttonPanel.add(start, BorderLayout.EAST);
				
        
        for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				
				walls[i][j] = new JButton();
				walls[i][j].addActionListener(this);
				mazePanel.add(walls[i][j]);
				walls[i][j].setBackground(Color.WHITE);
			}
		}
		
		 for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				wallState[i][j] = 0;
			}
		}
	}
	
	// what happens when a button is pressed
	public void actionPerformed(ActionEvent e)
	{
		//outcome is determined depending on what button is pressed
		if(e.getSource() == save)
		{
			currentState = 0;
			UIButtonPressed();
		}
		
		else if(e.getSource() == end)
		{
			currentState = 1;
			UIButtonPressed();
		}
		
		else if(e.getSource() == start)
		{
			currentState = 2;
			UIButtonPressed();
		}
		
		//if button is on grid and maze isnt saved
		else if(e.getSource() instanceof JButton && saveState ==0 )
		{
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					if(e.getSource() == walls[i][j]) //search for what is pressed
						wallButtonPressed(i,j);
				}
			}
		}
		
		//if the button pressed is on the grid while maze is saved but end button is also pressed (which in the saved menu is the solve yourself button)
		else if(e.getSource() instanceof JButton && saveState ==1 && endState ==1)
		{
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					if(e.getSource() == walls[i][j]) //search for what is pressed
						userMazeSolver(i,j);
				}
			}
		}
	}
	
	private void UIButtonPressed()
	{
		if(currentState == 0)
		{
			
			
			//depends on whether save button has been pressed before
			if(saveState == 1)
			{
				save.setText("Save Maze");
				end.setText("Intialize ending of the maze");
				start.setText("Intialize start of the maze");
				saveState--;
			}
			
			
			else if(saveState == 0)
			{
				save.setText("Edit Maze");
				end.setText("Solve Maze Yourself");
				start.setText("Show Solution to Maze (Currently not working)");
				saveState++;
			}
		
			
		}
		else if(currentState == 1)
		{
			// depends on whether go back button was pressed before
			if(endState == 1 && saveState == 1)
			{
				save.setVisible(true);
				start.setText("Show Solution to Maze (Currently not working)");
				end.setText("Solve Maze Yourself");
				removeSolutionPath();
				endState--;
			}
			
			else if(endState == 0 && saveState == 1)
			{
				save.setVisible(false);
				start.setText("Reset");
				end.setText("Go Back");
				endState++;
			}
			
			// depends on whether end button was pressed before
			else if(endState == 1)
			{
				end.setText("Intialize ending of the maze");
				endState--;
			}
			
			else if(endState == 0)
			{
				end.setText("Click a panel to set the end for the maze");
				endState++;
			}
		}
		else if(currentState == 2 )
		{
			
			// run if reset button is pressed
			if(startState == 0 && saveState == 1 && endState == 1)
			{
				removeSolutionPath();
			}
			
			// depends on whether show solution button was pressed before
			else if(startState == 1 && saveState == 1)
			{
				save.setVisible(true);
				start.setText("Show Solution to Maze (Currently not Working)");
				end.setVisible(true);
				removeSolutionPath();
				startState--;
			}
			
			else if(startState == 0 && saveState == 1)
			{
				save.setVisible(false);
				end.setVisible(false);
				start.setText("Go Back");
				startState++;
				solveMaze();
			}
			
			// depends on whether start button was pressed before
			else if(startState == 1)
			{
				start.setText("Intialize start of the maze");
				startState--;
			}
			
			else if(startState == 0)
			{
				start.setText("Click a panel to set the start for the maze");
				startState++;
			}
		}
	}
	
	private void wallButtonPressed(int col, int row)
	{
		//if intialize start is pressed then grid is pressed
		if(startState == 1)
		{
			
			//removes previous start of maze
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					if(wallState[i][j] == 3)
					{
						walls[i][j].setBackground(Color.WHITE);
						wallState[i][j] = 0;
						virtualMaze[i][j] = 0;
					}
				}
			}
			
			//creates new start of maze and resets varibles
			start.setText("Intialize start of the maze");
			startState--;
			walls[col][row].setBackground(Color.GREEN);
			wallState[col][row] = 3; // 3 is start of maze, 2 is end, 1 is wall, 0 is empty
			virtualMaze[col][row] = 3;
		}
		
		else if(endState == 1)
		{
			//same kind of thing as above but with end button
			for(int i = 0; i < height; i++)
			{
				for(int j = 0; j < width; j++)
				{
					if(wallState[i][j] == 2)
					{
						walls[i][j].setBackground(Color.WHITE);
						wallState[i][j] = 0;
						virtualMaze[i][j] = 0;
					}
				}
			}
			
			end.setText("Intialize end of the maze");
			endState--;
			walls[col][row].setBackground(Color.RED);
			wallState[col][row] = 2;
			virtualMaze[col][row] = 2;
		}
		
		// checks to see if there is a wall or a empty space and depending on that it will switch
		else if(wallState[col][row] == 0)
		{
			msgArea.setText("Click a empty space to add a wall or click a wall to remove it!");
			walls[col][row].setBackground(Color.BLACK);
			wallState[col][row] = 1;
			virtualMaze[col][row] = 1;
		}
		else if(wallState[col][row] == 1)
		{
			msgArea.setText("Click a empty space to add a wall or click a wall to remove it!");
			walls[col][row].setBackground(Color.WHITE);
			wallState[col][row] = 0;
			virtualMaze[col][row] = 0;
		}
	}
	
	//method for user to make maze solution
	private void userMazeSolver(int col, int row)
	{
		// variables and loops check where start and end of the maze are
		int startRow = 0; 
		int startCol = 0;
		int endRow = 0;
		int endCol = 0;;
		
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				if(wallState[i][j] == 3) // 3 is start
				{
					startRow = j;
					startCol = i;
				}
			}
		}
		
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				if(wallState[i][j] == 2) // 2 is end
				{
					endRow = j;
					endCol = i;
				}
			}
		}
		
		//if the space I click is empty, no other path spaces have been placed and space I click is ajacent to start
		if(wallState[col][row] == 0 && userPath == 0 && ((row == startRow && col == startCol -1) || (row == startRow && col == startCol +1) || (row == startRow -1&& col == startCol) || (row == startRow +1  && col == startCol)))
		{
			walls[col][row].setBackground(Color.YELLOW);
			wallState[col][row] = 4;
			virtualMaze[col][row] = 4;
			lastCol = col; // records the location of last space I placed a path
			lastRow = row;
			userPath++;
		}
		
		//if the space I click is empty, other path spaces have been placed and space I click is ajacent to previous space I clicked
		else if (wallState[col][row] == 0 && userPath == 1 &&((row == lastRow && col == lastCol -1) || (row == lastRow && col == lastCol +1) || (row == lastRow -1&& col == lastCol) || (row == lastRow +1  && col == lastCol)))
		{
			walls[col][row].setBackground(Color.YELLOW);
			wallState[col][row] = 4;
			virtualMaze[col][row] = 4;
			lastCol = col;
			lastRow = row;
		}
		
		//if path is ajacent to end of maze and not a wall
		if(((row == endRow && col == endCol -1) || (row == endRow && col == endCol +1) || (row == endRow -1&& col == endCol) || (row == endRow +1  && col == endCol)) && wallState[col][row] == 4)
			{
				userPath = 0;
				msgArea.setText("You solved the maze!");
			}
	}
	
	// removes the yellow solution path
	private void removeSolutionPath()
	{
		
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				if(wallState[i][j] == 4)
				{
					walls[i][j].setBackground(Color.WHITE);
					wallState[i][j] = 0;
					virtualMaze[i][j] = 0;
					userPath = 0;
				}
			}
		}
		msgArea.setText("Click a empty space to add a wall or click a wall to remove it!");
	}
	
	private void print_2D_array(int[][] array){
		
		for(int row = 0; row < array.length; row++)
		{
			for(int col = 0; col < array[0].length; col++)
			{
				System.out.printf("%d", array[row][col]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	// was meant to solve maze automatically but is glitchy
	private void solveMaze()
	{
		int north = 0;
		int east = 1;
		int south = 2;
		int west = 3; 
		
		int[][] copy = copyPlusOne(wallState); // creates a copy of the virtual maze with borders of walls
		//print_2D_array(copy);
		//find start and end of maze loctations
		int startRow = 0;
		int startCol = 0;
		int endRow = 0;
		int endCol = 0;
		
		
		for(int i = 0; i < height + 2; i++)
		{
			for(int j = 0; j < width + 2; j++)
			{
				if(copy[i][j] == 3)
				{
					startCol = j;
					startRow = i;
				}
			}
		}
		
		System.out.printf("start row %d and start col %d \n", startRow, startCol);
		
		
		for(int i = 0; i < height + 2; i++)
		{
			for(int j = 0; j < width + 2; j++)
			{
				if(copy[i][j] == 2)
				{
					endCol = j;
					endRow = i;
				}
			}
		}
		
		System.out.printf("end row %d and end col %d \n", endRow, endCol);
		
		
		//checks for the do while loops
		boolean finish = false;
		boolean start = false;
		boolean check = false;
		int direction = 0;
		//current location
		int row = startRow;
		int col = startCol;
		
		
		do
		{
			print_2D_array(copy);
			System.out.printf("%d \n", direction);
			System.out.printf("north %d \n", copy[row + 1][col]);
			System.out.printf("south %d \n", copy[row - 1][col]);
			System.out.printf("east %d \n", copy[row][col + 1]);
			System.out.printf("west %d \n", copy[row][col - 1]);
			if(direction == north && copy[row + 1][col] == 0)
			{
				row++;
				check = true;
			}
			else if(direction == east && copy[row][col + 1] == 0)
			{
				col++;
				check = true;
			}
			else if(direction == south && copy[row - 1][col] == 0)
			{
				row--;
				check = true;
			}
			else if(direction == west && copy[row][col - 1] == 0)
			{
				col--;
				check = true;
			}
			
			
			//if there is no where to go / surrounded by walls / dead end
			if(direction == west && !check)
				{	
					copy[row][col] = 1; // Make a fake wall on the copy to show that this way is a dead end
					walls[row - 1][col - 1].setBackground(Color.WHITE);
					wallState[row - 1][col - 1] = 0;
					virtualMaze[row - 1][col - 1] = 0;
					print_2D_array(copy);
					boolean stuck_check = false;
					direction = 0;
					
					//follow back down the path
					do{
						
						// understand that the directions may not be correctly labeled *Will be fixed later since it doesnt effect code
						if(direction == north && copy[row + 1][col] == 4)
						{
							row++;
							stuck_check = true;
						}
						else if(direction == east && copy[row][col + 1] == 4)
						{
							col++;
							stuck_check = true;
						}
						else if(direction == south && copy[row - 1][col] == 4)
						{
							row--;
							stuck_check = true;
						}
						else if(direction == west && copy[row][col - 1] == 4)
						{
							col--;
							stuck_check = true;
						}
						else if (direction > west){
							row = startRow;
							col = startCol;
							stuck_check = true;
							removeSolutionPath();
						}
						direction++;
					System.out.printf("Current Row %d and current col %d \n", row, col);			
					}while(stuck_check == false);
							direction = 0;
				}
			direction++;
			//if direction has been choosen
			if(check == true)
			{
				System.out.printf("Changed \n");
					//create a new path block
					walls[row - 1][col - 1].setBackground(Color.YELLOW);
					wallState[row - 1][col - 1] = 4;
					copy[row][col] = 4;
					virtualMaze[row - 1][col - 1] = 4;
					System.out.printf("Current Row %d and current col %d \n", row, col);
				
				//if current space is next to the finish
				if((row == endRow && col == endCol - 1) || (row == endRow && col == endCol + 1) || (row == endRow - 1 && col == endCol) || (row == endRow + 1  && col == endCol))
				{
					//breaks loop
					msgArea.setText("Solved Version of the maze");
					finish = true;
				}
				
				direction = 0;
			}
			
			
			check = false;
			
		}
		while(finish == false);
		
			
	}
	
	//creates a copy of a array with a border of ones or walls in the case of the maze
	public static int[][] copyPlusOne(int[][] array)
	{
		int[][] copy = new int[array.length +2][array[0].length +2] ;
		for(int row = 0; row < array.length +2; row++)
		{
			for(int col= 0; col < array[0].length +2; col++)
			{
				if(row == 0 || col == 0 || row == copy.length -1|| col == copy[0].length -1)
				copy[row][col] = 1;
				
				else
					copy[row][col] = array[row-1][col-1]; 
			}
		}
		return copy;
	}
	
	//main method just makes a new maze
	public static void main(String[] args)
	{
		new Maze();
	}
	
}
