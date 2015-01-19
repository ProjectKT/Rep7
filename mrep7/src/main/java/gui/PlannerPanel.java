package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.jbox2d.callbacks.ContactImpulse;
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
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class PlannerPanel extends PhysicsPanel implements PlannerController {
	
	private Robot robot;
	private List<Runnable> manipulations = Collections.synchronizedList(new LinkedList<Runnable>());

	public PlannerPanel() {
		initialize();
	}

	@Override
	protected float getInitialZoom() {
		return 40.0f;
	}

	private void initialize() {
		addKeyListener(keyAdapter);
		
		Body ground = null;
		{
			BodyDef bd = new BodyDef();
			ground = createBody(bd);

			EdgeShape shape = new EdgeShape();

			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 0.0f;
			fd.friction = 0.6f;

			shape.set(new Vec2(-20.0f, 0.0f), new Vec2(20.0f, 0.0f));
			ground.createFixture(fd);
		}

		// Boxes
		{
			PolygonShape box = new PolygonShape();
			box.setAsBox(0.5f, 0.5f);

			Body body = null;
			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;

			bd.position.set(0.0f, -0.5f);
			body = createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -1.5f);
			body = createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -2.5f);
			body = createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -3.5f);
			body = createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -4.5f);
			body = createBody(bd);
			body.createFixture(box, -0.5f);
		}

		// Robot
		robot = new Robot();
	}
	
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

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_G: {
				try { robot.moveTo(new Vec2(0,0));; } catch (Exception e0) { e0.printStackTrace(); }
				e.consume();
				break;
			}
			case KeyEvent.VK_R: {
				try { robot.release(); } catch (Exception e0) { e0.printStackTrace(); }
				e.consume();
				break;
			}
			case KeyEvent.VK_UP: {
				robot.higher();
				e.consume();
				break;
			}
			case KeyEvent.VK_DOWN: {
				robot.lower();
				e.consume();
				break;
			}
			}
		}
	};
	
	private class Robot {
		Body palm;
		Joint joint;
		boolean isGrabbing;

		private static final float PALM_WIDTH = 1.40f;
		private final Vec2 vLeft = new Vec2(-1.0f, 0);
		private final Vec2 vUp = new Vec2(0, -1.0f);
		
		public Robot() {
			initialize();
		}
		
		void initialize() {
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
				
				EdgeShape shape = new EdgeShape();
				
				FixtureDef fd = new FixtureDef();
				fd.shape = shape;
				fd.density = 0.2f;
				fd.friction = 0.5f;

				shape.set(new Vec2(-PALM_WIDTH/2, 0), new Vec2(PALM_WIDTH/2, 0));
				palm = createBody(bd);
				palm.createFixture(fd);
				
				addContactListener(grabContactAdapter);
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
				isGrabbing = false;
				synchronized (commandLock) {
					commandLock.wait();
				}
				
				addContactListener(grabContactAdapter);
			}
		}
		
		public void higher() {
			palm.setLinearVelocity(vUp.mul(1).addLocal(palm.getLinearVelocity()));
		}
		
		public void lower() {
			palm.setLinearVelocity(vUp.mul(-1).addLocal(palm.getLinearVelocity()));
		}
		
		public void moveTo(final Vec2 worldPosition) throws InterruptedException {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Vec2 diff = worldPosition.sub(palm.getWorldCenter());
						while (0.01f < diff.length()) {
							palm.setLinearVelocity(diff);
							Thread.sleep(100);
							diff = worldPosition.sub(palm.getWorldCenter());
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
		
		private ContactListener grabContactAdapter = new ContactAdapter() {
			@Override
			public void beginContact(Contact contact) {
				final Body bodyA = contact.getFixtureA().getBody();
				final Body bodyB = contact.getFixtureB().getBody();
				
				if (bodyA == palm || bodyB == palm) {
					Body hand = (bodyA == palm) ? bodyA : bodyB;
					Body obj = (bodyA == hand) ? bodyB : bodyA;
					
					final RevoluteJointDef jd = new RevoluteJointDef();
					jd.initialize(hand, obj, hand.getWorldCenter());
					jd.collideConnected = true;
					
					manipulations.add(new Runnable() {
						@Override
						public void run() {
							joint = createJoint(jd);
						}
					});
					isGrabbing = true;
					
					removeContactListener(this);
				}
			}
		};
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 1500, 1000);
		PlannerPanel p = new PlannerPanel();
		f.getContentPane().add(p);
		f.setVisible(true);
	}

	@Override
	public void pickup(String target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void place(String to) {
		// TODO Auto-generated method stub
		
	}
}
