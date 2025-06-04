import java.util.Random;

/**
 * Abstract base class for all cell types.
 * Defines the contract for symbol display and update behavior per time step.
 */
abstract class Cell {
    public abstract char getSymbol();
    public abstract Cell update(Cell[][] grid, int x, int y);
}

/**
 * Represents healthy tissue that may be invaded by nearby cancer cells.
 */
class TissueCell extends Cell {
    public char getSymbol() {
        return 'T';
    }

    public Cell update(Cell[][] grid, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx, ny = y + dy;
                if ((dx != 0 || dy != 0) && inBounds(grid, nx, ny)) {
                    if (grid[nx][ny] instanceof CancerCell && Math.random() < 0.2) {
                        return new CancerCell(); // Infected
                    }
                }
            }
        }
        return this;
    }

    private boolean inBounds(Cell[][] grid, int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
    }
}

/**
 * Represents a cancerous cell that can die of age or be killed by white blood cells.
 */
class CancerCell extends Cell {
    private int age = 0;

    public char getSymbol() {
        return 'C';
    }

    public Cell update(Cell[][] grid, int x, int y) {
        age++;
        if (age > 10) return new DeadCell(); // Natural death

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx, ny = y + dy;
                if ((dx != 0 || dy != 0) && inBounds(grid, nx, ny)) {
                    if (grid[nx][ny] instanceof WBC && Math.random() < 0.5) {
                        return new DeadCell(); // Killed by WBC
                    }
                }
            }
        }
        return this;
    }

    private boolean inBounds(Cell[][] grid, int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
    }
}

/**
 * Represents a white blood cell (WBC) that can eliminate cancer cells nearby.
 */
class WBC extends Cell {
    public char getSymbol() {
        return 'W';
    }

    public Cell update(Cell[][] grid, int x, int y) {
        return this; // Static WBC for now; no movement
    }
}

/**
 * Represents a dead cell that remains static in the simulation.
 */
class DeadCell extends Cell {
    public char getSymbol() {
        return 'X';
    }

    public Cell update(Cell[][] grid, int x, int y) {
        return this;
    }
}

/**
 * Main class to run the cancer dynamics simulation using cellular automata.
 */
public class CancerSim {
    private static final int SIZE = 20;          // Grid size
    private static final int ITERATIONS = 50;    // Number of simulation steps
    private static Cell[][] grid = new Cell[SIZE][SIZE];

    public static void main(String[] args) throws InterruptedException {
        initializeGrid();
        for (int i = 0; i < ITERATIONS; i++) {
            printGrid();
            updateGrid();
            Thread.sleep(300); // Pause for visualization
        }
    }

    /**
     * Randomly initializes the grid with a mix of tissue, cancer, WBC, and dead cells.
     */
    private static void initializeGrid() {
        Random rand = new Random();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                double r = rand.nextDouble();
                if (r < 0.7) grid[i][j] = new TissueCell();
                else if (r < 0.75) grid[i][j] = new CancerCell();
                else if (r < 0.8) grid[i][j] = new WBC();
                else grid[i][j] = new DeadCell();
            }
        }
    }

    /**
     * Updates the state of the entire grid based on individual cell rules.
     */
    private static void updateGrid() {
        Cell[][] newGrid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                newGrid[i][j] = grid[i][j].update(grid, i, j);
            }
        }
        grid = newGrid;
    }

    /**
     * Prints the current state of the grid to the console.
     */
    private static void printGrid() {
        System.out.println("==== Cancer Simulation ====");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}