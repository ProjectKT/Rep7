package gui.objects;

import gui.PhysicsPanel;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Box extends PhysicsObject {
	
	private static final BodyDef DEFAULT_BODY_DEF = new BodyDef() {{
		type = BodyType.DYNAMIC;
		position.set(0f, 0f);
		angle = 0f;
	}};
	

	public Box(float w, float h) {
		super(w, h, DEFAULT_BODY_DEF);
	}


	@Override
	protected FixtureDef shape(FixtureDef fd) {
		// 密度
		fd.density = 0.5f;
		// 摩擦
		fd.friction = 0.2f;
		// 反発
		fd.restitution = 0.1f;
		
		return fd;
	}

}
