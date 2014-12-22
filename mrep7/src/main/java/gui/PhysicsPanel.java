package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class PhysicsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final int FPS = 60;

	private Worker worker = new Worker();
	private World world = null;
	private Random random = new Random();
	
	// 表示に関する情報
	private int scale = 20;   // 表示倍率
	private Vec2 camera = new Vec2();
	private int mouseX = 0;
	private int mouseY = 0;

	public PhysicsPanel() {
		initialize();
	}
	public PhysicsPanel(LayoutManager layout) {
		super(layout);
		initialize();
	}
	public PhysicsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initialize();
	}
	public PhysicsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initialize();
	}
	
	private void initialize() {
		// イベントリスナー登録
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
		
		// jbox2dの初期化
		Vec2 gravity = new Vec2(0, 10f);
		world = new World(gravity);

		// 地面の定義
		BodyDef bd = new BodyDef();
		bd.position.set(5.0f, 9.0f);
		bd.angle = (float)Math.PI / 180 * 15;

		float w = 8.0f;
		float h = 1.0f;

		Body body = world.createBody(bd);

		PolygonShape ps = new PolygonShape();
		ps.setAsBox(w / 2, h / 2);

		body.createFixture(ps, 0f);
		body.setUserData(new Rect(w, h));

		// 描画スレッド開始
		try {
			worker.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public void paint(Graphics g) {
		// 回転を戻す
		AffineTransform at = new AffineTransform();
		((Graphics2D) g).setTransform(at);

		// 初期化
		g.clearRect(0,0,getWidth(),getHeight());
		
		// 再描画
		draw((Graphics2D) g);
	}
	
	protected void draw(Graphics2D g) {
		for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
			try {
				Vec2 position = body.getPosition();
				Rect obj = (Rect) body.getUserData();

				// カメラの左上座標(ピクセル)
				int cxb = (int) (camera.x * scale - getWidth() / 2);
				int cyb = (int) (camera.y * scale - getHeight() / 2);

				// オブジェクトの基準座標(ピクセル)
				int oxc = (int) (position.x * scale);
				int oyc = (int) (position.y * scale);

				// オブジェクトの左上座標(ピクセル)
				int oxb = (int) ((position.x - obj.width / 2.0f) * scale);
				int oyb = (int) ((position.y - obj.height / 2.0f) * scale);

				// オブジェクトのサイズ(ピクセル)
				int ow = (int) (obj.width * scale);
				int oh = (int) (obj.height * scale);

				AffineTransform at = new AffineTransform();
				at.setToRotation(body.getAngle(), oxc - cxb, oyc - cyb);
				g.setTransform(at);

				g.drawRect(oxb - cxb, oyb - cyb, ow, oh);
			} catch (RuntimeException e) {
			}
		}
	}
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			scale -= (e.getWheelRotation() * 2);
			if (scale <= 10) scale = 10;
			if (scale >= 50) scale = 50;
		}
		@Override
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			camera.x += (float) (mouseX - e.getX()) / scale;
			camera.y += (float) (mouseY - e.getY()) / scale;
			mouseX = e.getX();
			mouseY = e.getY();
		}
	};
	
	/**
	 * 描画するワーカー
	 */
	private class Worker implements Runnable {
		Thread thread;
		long pt = 0;
		long ct = 0;
		
		/**
		 * 描画ループを開始する
		 * @throws InterruptedException 
		 */
		public void start() throws InterruptedException {
			stop();
			thread = new Thread(this);
			thread.start();
		}
		
		/**
		 * 描画ループを終了する
		 * @throws InterruptedException
		 */
		public void stop() throws InterruptedException {
			if (thread != null) {
				thread.interrupt();
				thread.join();
			}
		}
		
		@Override
		public void run() {
			try {
				pt = System.nanoTime();
				while (!thread.isInterrupted()) {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							ct = System.nanoTime();
							world.step((ct-pt) / 1000f / 1000f / 1000f, 8, 8);
							repaint();
							pt = ct;
						}
					});
					
					Thread.sleep(1000 / FPS);
				}
			} catch (InterruptedException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
