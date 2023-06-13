package world;

public class AStarAlgorithm {
	
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.HashSet;
	import java.util.List;
	import java.util.PriorityQueue;
	import java.util.Set;

	class Node implements Comparable<Node> {
	    private int x;
	    private int y;
	    private int gCost;
	    private int hCost;
	    private int fCost;
	    private Node parent;

	    public Node(int x, int y) {
	        this.x = x;
	        this.y = y;
	        this.gCost = 0;
	        this.hCost = 0;
	        this.fCost = 0;
	        this.parent = null;
	    }

	    public int getX() {
	        return x;
	    }

	    public int getY() {
	        return y;
	    }

	    public int getGCost() {
	        return gCost;
	    }

	    public void setGCost(int gCost) {
	        this.gCost = gCost;
	    }

	    public int getHCost() {
	        return hCost;
	    }

	    public void setHCost(int hCost) {
	        this.hCost = hCost;
	    }

	    public int getFCost() {
	        return fCost;
	    }

	    public void setFCost(int fCost) {
	        this.fCost = fCost;
	    }

	    public Node getParent() {
	        return parent;
	    }

	    public void setParent(Node parent) {
	        this.parent = parent;
	    }

	    @Override
	    public int compareTo(Node other) {
	        return Integer.compare(this.getFCost(), other.getFCost());
	    }
	}

	public class AStarAlgorithm {
	    private static final int COST_DIAGONAL = 14;
	    private static final int COST_STRAIGHT = 10;
	    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

	    public static void main(String[] args) {
	        int[][] grid = {
	                {0, 0, 0, 0, 0},
	                {0, 1, 1, 1, 0},
	                {0, 0, 0, 0, 0},
	                {0, 1, 1, 1, 0},
	                {0, 0, 0, 0, 0}
	        };

	        int startX = 0;
	        int startY = 0;
	        int endX = 4;
	        int endY = 4;

	        List<Node> path = findPath(grid, startX, startY, endX, endY);

	        if (path != null) {
	            System.out.println("Caminho encontrado!");
	            for (Node node : path) {
	                System.out.println("(" + node.getX() + ", " + node.getY() + ")");
	            }
	        } else {
	            System.out.println("Caminho não encontrado.");
	        }
	    }

	    public static List<Node> findPath(int[][] grid, int startX, int startY, int endX, int endY) {
	        int numRows = grid.length;
	        int numCols = grid[0].length;

	        if (!isValidCell(grid, startX, startY) || !isValidCell(grid, endX, endY)) {
	            return null;
	        }

	        Node startNode = new Node(startX, startY);
	        Node endNode = new Node(endX, endY);

	        PriorityQueue<Node> openSet = new PriorityQueue<>();
	        Set<Node> closedSet = new HashSet<>();

	        startNode.setGCost(0);
	        startNode.setHCost(calculateHeuristic(startNode, endNode));
	        startNode.setFCost(startNode.getGCost() + startNode.getHCost());

	        openSet.add(startNode);

	        while (!openSet.isEmpty()) {
	            Node currentNode = openSet.poll();

	            if (currentNode.equals(endNode)) {
	                return reconstructPath(currentNode);
	            }

	            closedSet.add(currentNode);

	            for (int[] direction : DIRECTIONS) {
	                int neighborX = currentNode.getX() + direction[0];
	                int neighborY = currentNode.getY() + direction[1];

	                if (!isValidCell(grid, neighborX, neighborY) || closedSet.contains(new Node(neighborX, neighborY))) {
	                    continue;
	                }

	                int tentativeGCost = currentNode.getGCost() + calculateDistance(currentNode.getX(), currentNode.getY(), neighborX, neighborY);

	                Node neighborNode = new Node(neighborX, neighborY);
	                neighborNode.setGCost(tentativeGCost);
	                neighborNode.setHCost(calculateHeuristic(neighborNode, endNode));
	                neighborNode.setFCost(neighborNode.getGCost() + neighborNode.getHCost());
	                neighborNode.setParent(currentNode);

	                if (!openSet.contains(neighborNode)) {
	                    openSet.add(neighborNode);
	                } else if (tentativeGCost >= neighborNode.getGCost()) {
	                    continue;
	                }
	            }
	        }

	        return null;
	    }

	    private static boolean isValidCell(int[][] grid, int x, int y) {
	        int numRows = grid.length;
	        int numCols = grid[0].length;

	        return x >= 0 && x < numRows && y >= 0 && y < numCols && grid[x][y] != 1;
	    }

	    private static int calculateDistance(int startX, int startY, int endX, int endY) {
	        int dx = Math.abs(startX - endX);
	        int dy = Math.abs(startY - endY);

	        if (dx > dy) {
	            return COST_DIAGONAL * dy + COST_STRAIGHT * (dx - dy);
	        } else {
	            return COST_DIAGONAL * dx + COST_STRAIGHT * (dy - dx);
	        }
	    }

	    private static int calculateHeuristic(Node node, Node endNode) {
	        return calculateDistance(node.getX(), node.getY(), endNode.getX(), endNode.getY());
	    }

	    private static List<Node> reconstructPath(Node currentNode) {
	        List<Node> path = new ArrayList<>();

	        while (currentNode != null) {
	            path.add(currentNode);
	            currentNode = currentNode.getParent();
	        }

	        Collections.reverse(path);

	        return path;
	    }
	}

}
