package domain;

import java.util.Random;

import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class Bird {

	private static final float BIRD_TOP_SPEED = 20;
	private static final float BIRD_TURN_SPEED = 8;
	private static final float BIRD_TURN_CHANCE = 0.5f;
	private static final float BIRD_SWITCH_CHANCE = 0.999f;

	private final Vector3f location;
	private Vector3f direction;
	private final float speed;
	private final Geometry geometry;
	private final Random random;
	private float turnDirection;

	public Bird(Vector3f location, Vector3f direction, float speed,
			Material material) {
		super();
		this.location = location;
		this.direction = direction;
		this.speed = speed;
		geometry = new Geometry("bird", makeView());
		geometry.setMaterial(material);
		random = new Random();
		turnDirection = 1;
	}

	private Line makeView() {
		return new Line(location, tailLocation());
	}

	private Vector3f tailLocation() {
		float x = location.x - direction.x;
		float y = location.y - direction.y;
		return new Vector3f(x, y, 0);
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void update(float tpf) {
		turn(tpf);
		move(tpf);
	}

	private void turn(float tpf) {
		float r = random.nextFloat();
		if (r > BIRD_TURN_CHANCE) {
			float turnValue = BIRD_TURN_SPEED * r * turnDirection * tpf;
			Quaternion quaternion = new Quaternion(new float[] { 0, 0,
					turnValue });
			geometry.rotate(quaternion);
			direction = quaternion.mult(direction);
			if (r > BIRD_SWITCH_CHANCE) {
				turnDirection *= -1;
			}
		}
	}

	private void move(float tpf) {
		Vector3f movement = direction.mult(tpf * BIRD_TOP_SPEED);
		location.add(movement);
		geometry.move(movement);
	}
}
