package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import entities.BulletShoot;
//Importação dos Packages
import entities.Enemy;
import entities.Entity;
import entities.Player;
import graficos.Spritesheet;
import graficos.UI;
import world.Camera;
import world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener,MouseMotionListener{

	private static final long serialVersionUID = 1L;
	// Variables
	// Janela e Run Game
	public static JFrame frame;
	private boolean isRunning = true;
	private Thread thread;
	public static final int WIDTH = 240, HEIGHT = 160, SCALE = 3;

	// Imagens e Gráficos
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private BufferedImage image;
	private Graphics g;
	// Entities
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bulletShoot;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static Random random;
	public UI ui;
	
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelart.ttf");
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	
	
	public Menu menu;
	
	
	public int[] pixels;
	
	
/***/
	public boolean saveGame = false;
	
	public int mx,my;
	public Game() {
		Sound.musicBackground.play();
		//Sound.musicBackground.loop();
		random = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando Objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		

		class PixelManipulationExample {

		    public static void main(String[] args) {
		        try {
		        	// Carrega a imagem
		            BufferedImage image = ImageIO.read(new File("input.jpg"));

		            // Obtém a largura e altura da imagem
		            int width = image.getWidth();
		            int height = image.getHeight();

		            // Percorre todos os pixels da imagem
		            for (int y = 0; y < height; y++) {
		                for (int x = 0; x < width; x++) {
		                    // Obtém o valor do pixel (RGB)
		                    int pixel = image.getRGB(x, y);

		                    // Separa os componentes de cor (vermelho, verde, azul)
		                    int red = (pixel >> 16) & 0xFF;
		                    int green = (pixel >> 8) & 0xFF;
		                    int blue = pixel & 0xFF;

		                    // Manipula os componentes de cor
		                    // Exemplo: inverte as cores
		                    int invertedRed = 255 - red;
		                    int invertedGreen = 255 - green;
		                    int invertedBlue = 255 - blue;

		                    // Combina os componentes de cor invertidos
		                    int invertedPixel = (invertedRed << 16) | (invertedGreen << 8) | invertedBlue;

		                    // Define o novo valor do pixel na imagem
		                    image.setRGB(x, y, invertedPixel);
		                }
		            }

		            // Salva a imagem resultante
		            File output = new File("output.jpg");
		            ImageIO.write(image, "jpg", output);

		            System.out.println("Manipulação de pixels concluída!");
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}

		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bulletShoot = new ArrayList<BulletShoot>();
		menu = new Menu();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
	}

	// Criação da Janela
	public void initFrame() {
		frame = new JFrame("Lirio Dos Vales");
		frame.add(this);
		frame.setResizable(false);// Usuário não irá ajustar janela
		frame.pack();
		frame.setLocationRelativeTo(null);// Janela inicializa no centro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Fechar o programa por completo
		frame.setVisible(true);// Dizer que estará visível
	}

	// Threads
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Método Principal
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	// Ticks do Jogo
	public void tick() {
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[]opt1 = {"level"};
				int[] opt2 = {this.CUR_LEVEL};
				System.out.println(" O jogo foi salvo com sucesso");
			}
			this.restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}

			for (int i = 0; i < bulletShoot.size(); i++) {
				bulletShoot.get(i).tick();
			}
		
			if(enemies.size() <= 0) {
				//System.out.println("Next Level");
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				//System.out.println(newWord);
				World.restartGame(newWorld);
			}
		} else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 15) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver)
					this.showMessageGameOver = false;
					else
						this.showMessageGameOver = true;
			}
			
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL =1;
				String newWorld = "level"+CUR_LEVEL+".png";
				//System.out.println(newWord);
				World.restartGame(newWorld);
			}
		} else if(gameState == "MENU") {
			//
			menu.tick();
		}
	}

	// O que será mostrado em tela
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();// Sequência de buffer para otimizar a renderização, lidando com
														// performace gráfica
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		g = image.getGraphics();// Renderizar imagens na tela
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

		/* Render do jogo */
		world.render(g);

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bulletShoot.size(); i++) {
			bulletShoot.get(i).render(g);
		}

		ui.render(g);
		/***/
		g.dispose();// Limpar dados de imagem não usados
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 17));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 600, 40);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.setColor(Color.white);
			g.drawString("Game Over",WIDTH*SCALE / 2 - 50, HEIGHT*SCALE / 2 - 20);
			if(showMessageGameOver)
			g.drawString(">Presseione Enter Para Reniciar<",WIDTH*SCALE / 2 - 280, HEIGHT*SCALE / 2 + 40);
		}else if(gameState == "MENU") {
			menu.render(g);
			/*
			Graphics2D g2 = (Graphics2D) g;
			double angleMouse = Math.atan2(200+25 - my,  200+25 - mx);
			
			g2.rotate(angleMouse, 200+25, 200+25);
			g.setColor(Color.RED);
			g.fillRect(200, 200, 50, 50);
			*/
			
		}
		bs.show();
	}

	// Controle de FPS
	public void run() {
		// Variables
		long lastTime = System.nanoTime();// Usa o tempo atual do computador em nano segundos, bem mais preciso
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;// Calculo exato de Ticks
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		// Ruuner Game
		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop(); // Garante que todas as Threads relacionadas ao computador foram terminadas,
				// para garantir performance.

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			System.out.println("Direita");
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			System.out.println("Esquerda");
			player.left = true;
		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			System.out.println("Cima");
			player.up = true;
			
			if(gameState == "MENU") {
				menu.up = true;
			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			System.out.println("Baixo");
			player.down = true;
			
			if(gameState == "MENU") {
				menu.down = true;
			}

		}

		if (e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame =true;
			if(gameState == "MENU") {
				menu.enter = true;
				
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) { 
			this.saveGame = true;
		}
		

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {

			System.out.println("Direita Solto");
			player.right = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {

			System.out.println("Esquerda Solto");
			player.left = false;

		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {

			System.out.println("Cima Solto");
			player.up = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			System.out.println("Baixo Solto");
			player.down = false;

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
		
	}

}

