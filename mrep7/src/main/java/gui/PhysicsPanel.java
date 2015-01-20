package gui;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.Profile;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class PhysicsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int INIT_WIDTH = 300;
	private static final int INIT_HEIGHT = 300;
	
	public static final int FPS = 60;
	public static final int TEXT_LINE_SPACE = 13;
	public static final int TEXT_SECTION_SPACE = 20;
	
	// 設定
	public class Settings {
		// スクロールするか
		boolean scrollScreen = true;
		// Body を描画するか
		boolean drawShapes = true;
		// Joint を描画するか
		boolean drawJoints = true;
		// AABB を描画するか
		boolean drawAABBs = false;
		// COM を描画するか
		boolean drawCOMs = false;
		// Tree を描画するか
		boolean drawTree = false;
		// draw stats
		public boolean drawStats = false;
		
		boolean allowSleep = false;
		public boolean enableWarmStarting = false;
		public boolean enableSubStepping = false;
		public boolean enableContinuousCollision = false;
	}
	public Settings settings = new Settings();

	// 画面を更新する描画ワーカー
	private Animator animator = new Animator();
	// World インスタンス
	private World world = null;
	// 地面
	private Body groundBody;
	// カメラ
	protected final Camera camera = new Camera(0, 0, getInitialZoom());
	// 
	private Random random = new Random();
	// 描画用 Graphics2D
	private Graphics2D dbg = null;
	// 描画用 Image
	private Image dbImage = null;
	// 描画ヘルパー
	private final PhysicsDebugDraw debugDraw = new PhysicsDebugDraw(this);
	
	// デバッグ情報表示用
	protected String title = null;
	protected int textLine;
	private final LinkedList<String> textList = new LinkedList<String>();
	private final ArrayList<String> statsList = new ArrayList<String>();
	
	// 再生成しないための作業用オブジェクト
	private int panelWidth, panelHeight;
	private Vec2 p1 = new Vec2();
	
	// lock
	private final Object worldLock = new Object();

	public PhysicsPanel() {
		initialize();
	}
	
	protected float getInitialZoom() {
		return 1.0f;
	}
	
	/**
	 * マウスドラッグでスクロールをするかどうか
	 * @param enable true: スクロール有効, false: スクロール向こう
	 */
	public void enableScrollScreen(boolean enable) {
		settings.scrollScreen = enable;
	}
	
	/**
	 * オブジェクト初期化
	 */
	private void initialize() {
		// イベントリスナー登録
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// update
				updateSize(getWidth(), getHeight());
				dbImage = null;
			}
		});
		
		// パネル初期化
		setFocusable(true);
		setBackground(Color.black);
		setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
		updateSize(getWidth(), getHeight());
		textLine = 0;
		
		// jbox2dの初期化
		Vec2 gravity = new Vec2(0, 10f);
		world = new World(gravity);
		world.setContactListener(contactListener);
		world.setDebugDraw(debugDraw);

		// 地面の定義
		BodyDef bd = new BodyDef();
		groundBody = world.createBody(bd);
		
		// 描画スレッド開始
		try {
			startAnimating();
		} catch (InterruptedException e) { }
	}
	
	/**
	 * 描画スレッドを開始する
	 * @throws InterruptedException
	 */
	public void startAnimating() throws InterruptedException {
		animator.start();
	}
	
	/**
	 * 描画スレッドが動いているかどうか返す
	 * @return
	 */
	public boolean isAnimating() {
		return animator.isAnimating();
	}
	
	/**
	 * 描画スレッドを停止する
	 * @throws InterruptedException
	 */
	public void stopAnimating() throws InterruptedException {
		animator.stop();
	}
	
	public Body createBody(BodyDef def) {
		synchronized(worldLock) {
			return world.createBody(def);
		}
	}
	
	public void destroyBody(Body body) {
		if (body != null) {
			synchronized(worldLock) {
				world.destroyBody(body);
			}
		}
	}
	
	public Joint createJoint(JointDef def) {
		synchronized(worldLock) {
			return world.createJoint(def);
		}
	}
	
	public void destroyJoint(Joint j) {
		if (j != null) {
			synchronized(worldLock) {
				world.destroyJoint(j);
			}
		}
	}
	
	public void clearAll() throws InterruptedException {
		final boolean animate = isAnimating();
		stopAnimating();
		
		Body body = world.getBodyList();
		while (body != null) {
			world.destroyBody(body);
			body = body.getNext();
		}
		
		Joint joint = world.getJointList();
		while (joint != null) {
			world.destroyJoint(joint);
			joint = joint.getNext();
		}
		
		if (animate) {
			startAnimating();
		}
	}
	
	private Body getBodyAt(float x, float y) {
		// どの body が点 (x,y) を含んでいるか
		// 四角形のバウンド AABB を使って調べる
		queryAABB.lowerBound.set(x - 0.001f, y - 0.001f);
		queryAABB.upperBound.set(x + 0.001f, y + 0.001f);
		queryAABBCallback.point.set(x, y);
		queryAABBCallback.fixture = null;
		world.queryAABB(queryAABBCallback, queryAABB);
		
		if (queryAABBCallback.fixture != null) {
			return queryAABBCallback.fixture.getBody();
		}
		return null;
	}

	// --- 描画周り ---
	
	/**
	 * レンダリング用 Image の初期化を行う
	 * @return
	 */
	public boolean render() {
		if (dbImage == null) {
//			System.out.println("creating dbImage");
			if (panelWidth <= 0 || panelHeight <= 0) {
				return false;
			}
			dbImage = createImage(panelWidth, panelHeight);
			if (dbImage == null) {
				System.out.println("dbImage is still null, ignoring render call");
				return false;
			}
			dbg = (Graphics2D) dbImage.getGraphics();
			dbg.setFont(new Font("Courier New", Font.PLAIN, 12));
		}
		dbg.setColor(Color.black);
		dbg.fillRect(0, 0, panelWidth, panelHeight);
		return true;
	}
	
	/**
	 * レンダリング用 Image を描画する
	 */
	public void paintScreen() {
		try {
			Graphics g = this.getGraphics();
			if ((g != null) && dbImage != null && isShowing()) {
				g.drawImage(dbImage, 0, 0, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
			}
		} catch (AWTError e) {
			System.out.println("Graphics context error"+e);
		}
	}
	
	private void updateSize(int width, int height) {
		panelWidth = width;
		panelHeight = height;
		camera.getTransform().setExtents(width / 2, height / 2);
	}
	
	public Graphics2D getDBGraphics() {
		return dbg;
	}
	
	public synchronized void step(float hz, int velocityIterations, int positionIterations) {
		float timeStep = hz > 0f ? 1f / hz : 0;

		textLine = 20;

		if (title != null) {
			debugDraw.drawString(camera.getTransform().getExtents().x, 15, title, Color3f.WHITE);
			textLine += TEXT_LINE_SPACE;
		}

		int flags = 0;
		flags += settings.drawShapes ? DebugDraw.e_shapeBit : 0;
		flags += settings.drawJoints ? DebugDraw.e_jointBit : 0;
		flags += settings.drawAABBs ? DebugDraw.e_aabbBit : 0;
		flags += settings.drawCOMs ? DebugDraw.e_centerOfMassBit : 0;
		flags += settings.drawTree ? DebugDraw.e_dynamicTreeBit : 0;
		debugDraw.setFlags(flags);

		world.setAllowSleep(settings.allowSleep);
		world.setWarmStarting(settings.enableWarmStarting);
		world.setSubStepping(settings.enableSubStepping);
		world.setContinuousPhysics(settings.enableContinuousCollision);

		world.step(timeStep, velocityIterations, positionIterations);

		world.drawDebugData();
		drawDebugData(debugDraw.getGraphics());

		if (settings.drawStats) {
			// Vec2.watchCreations = true;
			debugDraw.drawString(5, textLine, "Engine Info", Color3f.GREEN);
			textLine += TEXT_LINE_SPACE;
			debugDraw.drawString(5, textLine, "Framerate: toBeCalculated", Color3f.WHITE);
			textLine += TEXT_LINE_SPACE;
			debugDraw.drawString(5, textLine,
					"bodies/contacts/joints/proxies = "
							+ world.getBodyCount() + "/"
							+ world.getContactCount() + "/"
							+ world.getJointCount() + "/"
							+ world.getProxyCount(), Color3f.WHITE);
			textLine += TEXT_LINE_SPACE;
			debugDraw.drawString(5, textLine, "World mouse position: " + mouseScreen.toString(), Color3f.WHITE);
			textLine += TEXT_LINE_SPACE;

			statsList.clear();
			Profile p = world.getProfile();
			p.toDebugStrings(statsList);

			for (String s : statsList) {
				debugDraw.drawString(5, textLine, s, Color3f.WHITE);
				textLine += TEXT_LINE_SPACE;
			}
			textLine += TEXT_SECTION_SPACE;
		}

		if (!textList.isEmpty()) {
			debugDraw.drawString(5, textLine, "Test Info", Color3f.GREEN);
			textLine += TEXT_LINE_SPACE;
			for (String s : textList) {
				debugDraw.drawString(5, textLine, s, Color3f.WHITE);
				textLine += TEXT_LINE_SPACE;
			}
			textList.clear();
		}

		if (mouseJoint != null) {
			mouseJoint.getAnchorB(p1);
			Vec2 p2 = mouseJoint.getTarget();

			debugDraw.drawSegment(p1, p2, Color3f.BLUE);
		}

//		if (settings.getSetting(TestbedSettings.DrawContactPoints).enabled) {
//			final float k_impulseScale = 0.1f;
//			final float axisScale = 0.3f;
//
//			for (int i = 0; i < pointCount; i++) {
//
//				ContactPoint point = points[i];
//
//				if (point.state == PointState.ADD_STATE) {
//					debugDraw.drawPoint(point.position, 10f, color1);
//				} else if (point.state == PointState.PERSIST_STATE) {
//					debugDraw.drawPoint(point.position, 5f, color2);
//				}
//
//				if (settings.getSetting(TestbedSettings.DrawContactNormals).enabled) {
//					p1.set(point.position);
//					p2.set(point.normal).mulLocal(axisScale).addLocal(p1);
//					debugDraw.drawSegment(p1, p2, color3);
//
//				} else if (settings
//						.getSetting(TestbedSettings.DrawContactImpulses).enabled) {
//					p1.set(point.position);
//					p2.set(point.normal).mulLocal(k_impulseScale)
//							.mulLocal(point.normalImpulse).addLocal(p1);
//					debugDraw.drawSegment(p1, p2, color5);
//				}
//
//				if (settings.getSetting(TestbedSettings.DrawFrictionImpulses).enabled) {
//					Vec2.crossToOutUnsafe(point.normal, 1, tangent);
//					p1.set(point.position);
//					p2.set(tangent).mulLocal(k_impulseScale)
//							.mulLocal(point.tangentImpulse).addLocal(p1);
//					debugDraw.drawSegment(p1, p2, color5);
//				}
//			}
//		}
	}
	
	protected void drawDebugData(Graphics2D g) {
		
	}

	// --------------
	
	// --- Mouse Joint ----

	// QueryAABB
	private final AABB queryAABB = new AABB();
	// Callback
	private final QueryAABBCallback queryAABBCallback = new QueryAABBCallback();
	// MouseJoint
	private MouseJoint mouseJoint = null;
	
	private boolean spawnMouseJoint(Vec2 p) {
		if (mouseJoint != null) {
			return true;
		}
		Body body = getBodyAt(p.x, p.y);
		if (body != null) {
			MouseJointDef def = new MouseJointDef();
			def.bodyA = groundBody;
			def.bodyB = body;
			def.target.set(p);
			def.maxForce = 1000f * body.getMass();
			mouseJoint = (MouseJoint) world.createJoint(def);
			body.setAwake(true);
			return true;
		}
		return false;
	}
	
	private void updateMouseJoint(Vec2 p) {
		if (mouseJoint != null) {
			mouseJoint.setTarget(p);
		}
	}
	
	private void destroyMouseJoint() {
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
	}
	
	private class QueryAABBCallback implements QueryCallback {
		public final Vec2 point = new Vec2();
		public Fixture fixture;
		
		@Override
		public boolean reportFixture(Fixture fixture) {
			Body body = fixture.getBody();
		    if (body.getType() == BodyType.DYNAMIC) {
		      boolean inside = fixture.testPoint(point);
		      if (inside) {
		        this.fixture = fixture;
		        return false;
		      }
		    }

		    return true;
		}
	}
	
	// -------------------

	// ワールド座標系でのマウスの位置
	private final Vec2 mouseWorld = new Vec2();
	// スクリーン上でのマウスの位置
	private final Vec2 mouseScreen = new Vec2();
	private final Vec2 oldMouseScreen = new Vec2();
	private boolean draggingBody = false;
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			updateMouse(e);
			if (settings.scrollScreen) {
				if (0 < e.getPreciseWheelRotation()) {
					camera.zoomOut(mouseScreen);
				} else {
					camera.zoomIn(mouseScreen);
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {
			updateMouse(e);
			oldMouseScreen.set(mouseScreen);
			draggingBody = spawnMouseJoint(mouseWorld);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			if (!draggingBody && settings.scrollScreen) {
				camera.move(oldMouseScreen.sub(mouseScreen));
			}
			updateMouse(e);
			if (draggingBody) {
				updateMouseJoint(mouseWorld);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			destroyMouseJoint();
			draggingBody = false;
		}
		
		private void updateMouse(MouseEvent e) {
			oldMouseScreen.set(mouseScreen);
			mouseScreen.set(e.getX(), e.getY());
			camera.getTransform().getScreenToWorld(mouseScreen, mouseWorld);
		}
	};
	
	// --- Contact Listener ---
	private final ArrayList<ContactListener> contactListeners = new ArrayList<ContactListener>();
	
	public void addContactListener(ContactListener listener) {
		synchronized(contactListeners) {
			if (!contactListeners.contains(listener)) {
				contactListeners.add(listener);
			}
		}
	}
	
	public void removeContactListener(ContactListener listener) {
		synchronized(contactListeners) {
			int index = contactListeners.indexOf(listener);
			if (index != -1) {
				contactListeners.remove(index);
			}
		}
	}
	
	private ContactListener contactListener = new ContactListener() {
		@Override
		public void beginContact(Contact contact) {
			synchronized (contactListeners) {
				for (int i = contactListeners.size()-1; i >= 0; i--) {
					contactListeners.get(i).beginContact(contact);
				}
			}
		}
		@Override
		public void endContact(Contact contact) {
			synchronized (contactListeners) {
				for (int i = contactListeners.size()-1; i >= 0; i--) {
					contactListeners.get(i).endContact(contact);
				}
			}
		}
		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			synchronized (contactListeners) {
				for (int i = contactListeners.size()-1; i >= 0; i--) {
					contactListeners.get(i).preSolve(contact, oldManifold);
				}
			}
		}
		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			synchronized (contactListeners) {
				for (int i = contactListeners.size()-1; i >= 0; i--) {
					contactListeners.get(i).postSolve(contact, impulse);
				}
			}
		}
	};
	
	/**
	 * ContactListener の実装クラス
	 */
	protected class ContactAdapter implements ContactListener {
		@Override
		public void beginContact(Contact contact) { }
		@Override
		public void endContact(Contact contact) { }
		@Override
		public void preSolve(Contact contact, Manifold oldManifold) { }
		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) { }
	}
	// ------------------------
	
	/**
	 * 描画するワーカー
	 */
	private class Animator implements Runnable {
		Thread thread;
		boolean loop;
		
		/**
		 * 描画ループを開始する
		 * @throws InterruptedException 
		 */
		public void start() throws InterruptedException {
			stop();
			loop = true;
			thread = new Thread(this);
			thread.start();
		}
		
		public boolean isAnimating() {
			return loop;
		}
		
		/**
		 * 描画ループを終了する
		 * @throws InterruptedException
		 */
		public void stop() throws InterruptedException {
			if (thread != null) {
				loop = false;
				thread.interrupt();
				thread.join();
			}
		}
		
		@Override
		public void run() {
			long beforeTime, afterTime, sleepTime;
			beforeTime = System.nanoTime();
			sleepTime = 0;
			
			try {
				while (loop) {
					if (render()) {
						step(FPS, 8, 8);
						paintScreen();
					}
					
					afterTime = System.nanoTime();
					
					sleepTime = (1000000000 / FPS - (afterTime - beforeTime)) / 1000000;
					if (0 < sleepTime) {
						Thread.sleep(sleepTime);
					}
					
					beforeTime = System.nanoTime();
				}
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 500, 300);
		PhysicsPanel p = new PhysicsPanel();
		f.getContentPane().add(p);
		f.setVisible(true);
	}

}
