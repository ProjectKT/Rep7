package gui.objects;

import gui.PhysicsPanel;
import gui.Rect;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

public abstract class PhysicsObject {

	public Body body;
	private BodyDef bd;
	private FixtureDef fd;
	private Rect rect;

	public PhysicsObject(float w, float h, BodyDef bd) {
		this.bd = bd;
		fd = new FixtureDef();
		fd.shape = new PolygonShape();
		((PolygonShape) fd.shape).setAsBox(w/2, h/2);
		fd = shape(fd);
		rect = new Rect(w, h);
	}
	
	/**
	 * 形状を作る
	 * @param fd 初期化された FixtureDef
	 * @return 内容を変更した fd を返す
	 */
	abstract protected FixtureDef shape(FixtureDef fd);
	
	public PhysicsObject attachTo(PhysicsPanel panel) {
		body = panel.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(rect);
		return this;
	}
	
	public PhysicsObject detachFrom(PhysicsPanel panel) {
		if (body != null) {
			panel.getWorld().destroyBody(body);
		}
		return this;
	}

}
