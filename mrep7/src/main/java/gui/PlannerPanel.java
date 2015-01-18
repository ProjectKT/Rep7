package gui;

import javax.swing.JFrame;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class PlannerPanel extends PhysicsPanel {
	
	public PlannerPanel() {
		initialize();
	}
	
	private void initialize() {
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

	      float hs[] = {0.25f, 1.0f, 4.0f, 0.0f, 0.0f, -1.0f, -2.0f, -2.0f, -1.25f, 0.0f};

	      float x = 20.0f, y1 = 0.0f, dx = 5.0f;

	      for (int i = 0; i < 10; ++i) {
	        float y2 = hs[i];
	        shape.set(new Vec2(x, y1), new Vec2(x + dx, y2));
	        ground.createFixture(fd);
	        y1 = y2;
	        x += dx;
	      }

	      for (int i = 0; i < 10; ++i) {
	        float y2 = hs[i];
	        shape.set(new Vec2(x, y1), new Vec2(x + dx, y2));
	        ground.createFixture(fd);
	        y1 = y2;
	        x += dx;
	      }

	      shape.set(new Vec2(x, 0.0f), new Vec2(x + 40.0f, 0.0f));
	      ground.createFixture(fd);

	      x += 80.0f;
	      shape.set(new Vec2(x, 0.0f), new Vec2(x + 40.0f, 0.0f));
	      ground.createFixture(fd);

	      x += 40.0f;
	      shape.set(new Vec2(x, 0.0f), new Vec2(x + 10.0f, 5.0f));
	      ground.createFixture(fd);

	      x += 20.0f;
	      shape.set(new Vec2(x, 0.0f), new Vec2(x + 40.0f, 0.0f));
	      ground.createFixture(fd);

	      x += 40.0f;
	      shape.set(new Vec2(x, 0.0f), new Vec2(x, 20.0f));
	      ground.createFixture(fd);
	    }

	    // Boxes
	    {
	      PolygonShape box = new PolygonShape();
	      box.setAsBox(0.5f, 0.5f);

	      Body body = null;
	      BodyDef bd = new BodyDef();
	      bd.type = BodyType.DYNAMIC;

	      bd.position.set(230.0f, -0.5f);
	      body = world.createBody(bd);
	      body.createFixture(box, -0.5f);

	      bd.position.set(230.0f, -1.5f);
	      body = world.createBody(bd);
	      body.createFixture(box, -0.5f);

	      bd.position.set(230.0f, -2.5f);
	      body = world.createBody(bd);
	      body.createFixture(box, -0.5f);

	      bd.position.set(230.0f, -3.5f);
	      body = world.createBody(bd);
	      body.createFixture(box, -0.5f);

	      bd.position.set(230.0f, -4.5f);
	      body = world.createBody(bd);
	      body.createFixture(box, -0.5f);
	    }
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
