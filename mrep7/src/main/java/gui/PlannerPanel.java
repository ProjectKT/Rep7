package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class PlannerPanel extends PhysicsPanel implements PlannerController {
	
	interface Settings {
		// 箱の大きさ
		Vec2 BoxSize = new Vec2(1.0f, 1.0f);
		// 箱の形状
		PolygonShape BoxShape = new PolygonShape() {{
			setAsBox(BoxSize.x/2, BoxSize.y/2);
		}};
		// 箱の名前の描画色
		Color BoxNameColor = new Color(1.0f, 1.0f, 1.0f);
		// ロボットの形
		PolygonShape RobotShape = new PolygonShape() {{
			setAsBox(BoxSize.x/2 + 0.5f, 0.2f);
		}};
		// 地面の大きさ
		float GroundLength = 1000.0f;
		// ホームポジション
		Vec2 HomePosition = new Vec2(-BoxSize.x, -10.0f);
		// 積む位置
		Vec2 PilePosition = new Vec2(-BoxSize.x, -BoxSize.y/2);
	}
	
	// 操作用ロボット
	private Robot robot;
	// 描画前に world に対して行う操作のキュー
	private List<Runnable> manipulations = Collections.synchronizedList(new LinkedList<Runnable>());
	// 箱とその名前の対応
	private Map<String,Box> boxMap = new HashMap<String,Box>();
	// 今持っているボックス
	private Box holdingBox = null;
	// 一番高い位置にあるボックス
	private Box highestBox = null;
	// 現在の状態
	private ArrayList<String> states = new ArrayList<String>();
	// 状態の変化リスナー
	private StatesChangeListener statesChangeListener = null;
	

	public PlannerPanel() {
		initialize();
		camera.setCenter(Settings.HomePosition.mul(0.5f));
	}

	@Override
	protected float getInitialZoom() {
		return 20.0f;
	}

	private void initialize() {
		addKeyListener(keyAdapter);
		addContactListener(statesWatcher);
		
		// ground
		Body ground = null;
		{
			BodyDef bd = new BodyDef();
			ground = createBody(bd);

			EdgeShape shape = new EdgeShape();

			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 0.0f;
			fd.friction = 0.6f;

			shape.set(new Vec2(-Settings.GroundLength/2, 0.0f), new Vec2(Settings.GroundLength/2, 0.0f));
			ground.createFixture(fd);
		}

		// Robot
		robot = new Robot();
	}
	
	private void updateStates() {
		synchronized (states) {
			states.clear();
			
			synchronized (boxMap) {
				Iterator<Box> it = boxMap.values().iterator();
				while (it.hasNext()) {
					Box box = it.next();
					Box boxOn = null;
					Box boxAbove = null;
					
					ContactEdge edge = box.body.getContactList();
					while (edge != null) {
						final Contact contact = edge.contact;
						// この箱のボディ
						final Body bodyBox;
						// 接触しているオブジェクトのボディ
						final Body bodyObj;
						
						if (contact.getFixtureA().getBody() == box.body) {
							bodyBox = contact.getFixtureA().getBody();
							bodyObj = contact.getFixtureB().getBody();
						} else {
							bodyBox = contact.getFixtureB().getBody();
							bodyObj = contact.getFixtureA().getBody();
						}
						
						if (contact.isTouching()) {
							if (bodyBox.getWorldCenter().y < bodyObj.getWorldCenter().y) {
								String boxOnName = findBox(bodyObj);
								if (boxOnName != null) {
									boxOn = boxMap.get(boxOnName);
								}
							} else if (bodyObj.getWorldCenter().y < bodyBox.getWorldCenter().y){
								String boxAboveName = findBox(bodyObj);
								if (boxAboveName != null) {
									boxAbove = boxMap.get(boxAboveName);
								}
							}
						}
						
						edge = edge.next;
					}
					
					if (boxOn == null) {
						states.add("ontable "+box.name);
					} else {
						states.add(box.name+" on "+boxOn.name);
					}
					if (boxAbove == null) {
						states.add("clear "+box.name);
					}
				}
			}
			
			if (!robot.isGrabbing) {
				states.add("handEmpty");
			}
		}
		
		Collections.sort(states);
	}
	
	// --- interface implementation ---

	@Override
	public void putBox(final String name, String on) {
		final int i = boxMap.size();
		final Vec2 pos = new Vec2(Settings.BoxSize.x * 1.5f * i, -Settings.BoxSize.y/2);
		
		// 下敷きのオブジェクトを取得
		if (on != null && !on.isEmpty()) {
			// 箱の上に置く
			Box onBox = boxMap.get(on);
			if (onBox != null) {
				pos.set(onBox.body.getWorldCenter());
			}
		}
		
		Box box = boxMap.get(name);
		if (box == null) {
			box = new Box(name, pos);
			boxMap.put(name, box);
			updateStates();
			updateHighestBox(box);
		} else {
			final Box fbox = box;
			manipulations.add(new Runnable() {
				@Override
				public void run() {
					destroyBody(fbox.body);
					boxMap.remove(fbox);
					Box box = new Box(name, pos);
					boxMap.put(name, box);
					updateStates();
					updateHighestBox(box);
				}
			});
		}
	}
	
	private void updateHighestBox(Box box) {
		if (highestBox == null) {
			highestBox = box;
		} else {
			if (box.body.getWorldCenter().y < highestBox.body.getWorldCenter().y) {
				highestBox = box;
			}
		}
		
		if (highestBox == box) {
			Settings.HomePosition.y = highestBox.body.getWorldCenter().y-Settings.BoxSize.y*1.5f;
		}
	}
	
	@Override
	public void pickup(String target) throws InterruptedException {
		if (holdingBox != null) {
			System.out.println("Currently holding the box "+holdingBox.name+".");
			return;
		}
		
		holdingBox = boxMap.get(target);
		if (holdingBox != null) {
			final Vec2 pos = holdingBox.body.getWorldCenter();
			final Vec2 posTo = new Vec2(pos);
			posTo.y = Settings.HomePosition.y;
			robot.moveTo(posTo);
			posTo.y = pos.y;
			robot.moveTo(posTo);
			robot.grab();
			posTo.y = Settings.HomePosition.y;
			robot.moveTo(posTo);
		}
	}

	@Override
	public void place(String to) throws InterruptedException {
		if (holdingBox == null) {
			System.out.println("Currently holding no box.");
			return;
		}
		
		final Vec2 pos = new Vec2(Settings.HomePosition);
		if (to != null && !to.isEmpty()) {
			Box boxOn = boxMap.get(to);
			if (boxOn != null) {
				Vec2 posOn = boxOn.body.getWorldCenter();
				pos.set(posOn).subLocal(new Vec2(0, Settings.BoxSize.y*1.5f));
			}
		}
		
		final Vec2 posTo = new Vec2(pos);
		posTo.y = Settings.HomePosition.y;
		robot.moveTo(posTo);
		posTo.y = pos.y;
		robot.moveTo(posTo);
		robot.release();
		updateHighestBox(holdingBox);
		
		posTo.y = Settings.HomePosition.y;
		robot.moveTo(posTo);
		
		holdingBox = null;
	}
	
	public List<String> getStates() {
		updateStates();
		
		ArrayList<String> out = new ArrayList<String>(states);
		return out;
	}
	
	@Override
	public void setStatesChangeListener(StatesChangeListener l) {
		statesChangeListener = l;
	}
	
	private final ContactAdapter statesWatcher = new ContactAdapter() {
		@Override
		public void beginContact(Contact contact) { fire(); }
		@Override
		public void endContact(Contact contact) { fire(); }
		
		private void fire() {
			updateStates();
			if (statesChangeListener != null) {
				statesChangeListener.onChangeStates((List<String>) states.clone());
			}
		}
	};
	
	private String findBox(Body body) {
		synchronized (boxMap) {
			Iterator<Entry<String, Box>> it = boxMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Box> entry = it.next();
				if (entry.getValue().body == body) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
	
	// --------------------------------
	
	@Override
	public boolean render() {
		manipulateWorld();
		return super.render();
	}

	private void manipulateWorld() {
		if (manipulations != null && !manipulations.isEmpty()) {
			synchronized (manipulations) {
				for (Iterator<Runnable> it = manipulations.iterator(); it.hasNext(); it.remove()) {
					it.next().run();
				}
			}
		}
	}
	
	@Override
	protected void drawDebugData(Graphics2D g) {
		super.drawDebugData(g);
		
		drawBoxNames(g);
		drawStates(g);
	}
	
	private void drawBoxNames(Graphics2D g) {
		final Font origFont = g.getFont();
		
		// box の名前を表示する
		synchronized (boxMap) {
			final Vec2 screenSize = new Vec2();
			final Vec2 screenCenter = new Vec2();
			camera.transform.getWorldVectorToScreen(Settings.BoxSize, screenSize);
			g.setFont(getFont().deriveFont(screenSize.y));
			g.setColor(Settings.BoxNameColor);
			
			Iterator<Entry<String, Box>> it = boxMap.entrySet().iterator();
			while (it.hasNext()) {
				Box box = it.next().getValue();
				camera.transform.getWorldToScreen(box.body.getWorldCenter(), screenCenter);
				final int w = g.getFontMetrics().stringWidth(box.name);
				g.drawString(box.name, screenCenter.x - w/2.0f, screenCenter.y + screenSize.y/2);
			}
		}

		g.setFont(origFont);
	}
	
	private void drawStates(Graphics2D g) {
		int size = g.getFont().getSize();
		synchronized (states) {
			int i = 0;
			for (Iterator<String> it = states.iterator(); it.hasNext(); i++) {
				String state = it.next();
				g.drawString(state, 0, i*size);
			}
		}
	}

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			}
		}
	};
	
	private class Box {
		String name;
		Body body;
		
		public Box(String name, Vec2 pos) {
			this.name = name;
			initialize(pos);
		}
		
		private void initialize(Vec2 pos) {
			BodyDef bf = new BodyDef();
			bf.type = BodyType.DYNAMIC;
			bf.position.set(pos);
			body = createBody(bf);
			
			FixtureDef fd = new FixtureDef();
			fd.shape = Settings.BoxShape;
//			fd.density = 0.5f; // これを付けると回転するようになる
			fd.friction = 1.0f;
			
			body.createFixture(fd);
		}
	}
	
	private class Robot {
		Body palm;
		Joint joint;
		boolean isGrabbing;

		private static final float PALM_WIDTH = 1.40f;
		
		public Robot() {
			initialize();
		}
		
		private void initialize() {
			// Body
			{
				// TODO
			}
			
			// Arm
			{
				// TODO
			}
			
			// Hand
			{
				BodyDef bd = new BodyDef();
				bd.type = BodyType.KINEMATIC;
				bd.position.set(0, -8.0f);
				
				FixtureDef fd = new FixtureDef();
				fd.shape = Settings.RobotShape;
				fd.density = 0.2f;
				fd.friction = 0.5f;

				palm = createBody(bd);
				palm.createFixture(fd);
			}
		}
		
		public void grab() throws InterruptedException {
			if (!isGrabbing) {
				ContactEdge it = palm.getContactList();
				while (it != null) {
					Contact contact = it.contact;
					final Body bodyA = contact.getFixtureA().getBody();
					final Body bodyB = contact.getFixtureB().getBody();
					
					if (bodyA == palm || bodyB == palm) {
						Body hand = (bodyA == palm) ? bodyA : bodyB;
						Body obj = (bodyA == hand) ? bodyB : bodyA;
						Manifold manifold = contact.getManifold();
						
						final RevoluteJointDef jd = new RevoluteJointDef();
						jd.initialize(hand, obj, hand.getWorldPoint(manifold.localPoint));
						jd.collideConnected = true;
						
						final Object commandLock = new Object();
						manipulations.add(new Runnable() {
							@Override
							public void run() {
								joint = createJoint(jd);
								synchronized (commandLock) {
									commandLock.notifyAll();
								}
							}
						});
						synchronized (commandLock) {
							commandLock.wait();
						}
						isGrabbing = true;
						
						return;
					}
					it = it.next;
				}
			}
		}
		
		public void release() throws InterruptedException {
			if (isGrabbing) {
				final Object commandLock = new Object();
				manipulations.add(new Runnable() {
					@Override
					public void run() {
						destroyJoint(joint);
						synchronized (commandLock) {
							commandLock.notifyAll();
						}
					}
				});
				synchronized (commandLock) {
					commandLock.wait();
				}
				isGrabbing = false;
			}
		}
		
		public void moveTo(final Vec2 to) throws InterruptedException {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Vec2 diff = to.sub(palm.getWorldCenter());
						while (0.01f < diff.length()) {
							palm.setLinearVelocity(diff.mulLocal(20.0f));
							Thread.sleep(1);
							diff = to.sub(palm.getWorldCenter());
						}
					} catch (InterruptedException e) {
					} finally {
						palm.setLinearVelocity(new Vec2(0, 0));
					}
				}
			});
			t.start();
			t.join();
		}
		
		private ContactListener stopOnContactAdapter = new ContactAdapter() {
			@Override
			public void beginContact(Contact contact) {
				final Body bodyA = contact.getFixtureA().getBody();
				final Body bodyB = contact.getFixtureB().getBody();
				
				if (bodyA == palm || bodyB == palm) {
					Body hand = (bodyA == palm) ? bodyA : bodyB;
					Body obj = (bodyA == hand) ? bodyB : bodyA;
					
					// TODO 停止する
				}
			}
		};
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 500, 300);
		final PlannerPanel p = new PlannerPanel();
		f.getContentPane().add(p);
		f.setVisible(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try { Thread.sleep(1000); } catch (Exception e) {}
					System.out.println(p.getStates());
				}
			}
		}).start();
		
		try { 
			p.stop();
			p.putBox("1", null);
			p.putBox("2", "1");
			p.putBox("3", null);
			p.start();
			
			Thread.sleep(500);
			p.pickup("2");
			Thread.sleep(500);
			p.place("3");
			Thread.sleep(500);
			p.pickup("1");
			Thread.sleep(500);
			p.place("2");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
