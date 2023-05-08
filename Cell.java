package mazesolver;

class Cell
{

	int x;
	int y;
	int thisRow;
	int thisCol;
	int size = 40;
	
	public Cell(int row, int col)
	{
		x = row * size;
		y = col * size;
		thisRow = row;
		thisCol = col;
	}
	
	public void display()
	{
		line(x,y, x + size, y);
		line(x + size,y, x + size, y + size);
		line(x + size,y + size, x, y + size);
		line(x,y + size, x, y);
	}
}
