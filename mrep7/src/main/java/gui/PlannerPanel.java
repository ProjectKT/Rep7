package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

public class PlannerPanel extends PhysicsPanel {
	
	private Robot robot;

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
			ground = world.createBody(bd);

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
			body = world.createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -1.5f);
			body = world.createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -2.5f);
			body = world.createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -3.5f);
			body = world.createBody(bd);
			body.createFixture(box, -0.5f);

			bd.position.set(0.0f, -4.5f);
			body = world.createBody(bd);
			body.createFixture(box, -0.5f);
		}

		// Robot
		robot = new Robot();
	}
	
	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println(e);
			switch (e.getKeyCode()) {
			case KeyEvent.VK_G: {
				robot.grab();
				e.consume();
				break;
			}
			case KeyEvent.VK_R: {
				robot.release();
				e.consume();
				break;
			}
			case KeyEvent.VK_H: {
				robot.higher();
				e.consume();
				break;
			}
			case KeyEvent.VK_L: {
				robot.lower();
				e.consume();
				break;
			}
			}
		}
	};
	
	private class Robot {
		Body palm;
		Body leftHand;
		Body rightHand;
		
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
				
				EdgeShape shape = new EdgeShape();
				
				FixtureDef fd = new FixtureDef();
				fd.shape = shape;
				fd.density = 0.0f;
				fd.friction = Float.MAX_VALUE;
				
				// 軸
				shape.set(new Vec2(-0.7f, -8.0f), new Vec2(0.7f, -8.0f));
				palm = world.createBody(bd);
				palm.createFixture(fd);
				
				// 左手
				shape.set(new Vec2(-0.7f, -8.0f), new Vec2(-0.7f, -7.0f));
				leftHand = world.createBody(bd);
				leftHand.createFixture(fd);
				
				// 右手
				shape.set(new Vec2(0.7f, -8.0f), new Vec2(0.7f, -7.0f));
				rightHand = world.createBody(bd);
				rightHand.createFixture(fd);
			}
		}
		
		public void grab() {
			final Vec2 deltaL = new Vec2( 0.2f, 0).addLocal(leftHand.getLinearVelocity());
			final Vec2 deltaR = new Vec2(-0.2f, 0).addLocal(rightHand.getLinearVelocity());
			addContactListener(grabContactAdapter);
			leftHand.setLinearVelocity(deltaL);
			rightHand.setLinearVelocity(deltaR);
		}
		
		public void release() {
			final Vec2 deltaL = new Vec2(-0.2f, 0);
			final Vec2 deltaR = new Vec2( 0.2f, 0);
			leftHand.setLinearVelocity(deltaL.add(leftHand.getLinearVelocity()));
			rightHand.setLinearVelocity(deltaR.add(rightHand.getLinearVelocity()));
		}
		
		public void higher() {
			final Vec2 delta = new Vec2(0, -0.2f);
			palm.setLinearVelocity(delta.add(palm.getLinearVelocity()));
			leftHand.setLinearVelocity(delta.add(leftHand.getLinearVelocity()));
			rightHand.setLinearVelocity(delta.add(rightHand.getLinearVelocity()));
		}
		
		public void lower() {
			final Vec2 delta = new Vec2(0, 0.2f);
			palm.setLinearVelocity(delta.add(palm.getLinearVelocity()));
			leftHand.setLinearVelocity(delta.add(leftHand.getLinearVelocity()));
			rightHand.setLinearVelocity(delta.add(rightHand.getLinearVelocity()));
		}
		
		private ContactListener grabContactAdapter = new ContactAdapter() {
			@Override
			public void beginContact(Contact contact) {
				System.out.println("contact! "+contact);
				removeContactListener(this);
			}
		};
		
		private ContactListener releaseContactAdapter = new ContactAdapter() {
			@Override
			public void endContact(Contact contact) {
				System.out.println("contact! "+contact);
				removeContactListener(this);
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
}
