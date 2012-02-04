package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class Bird {

	private static final float BIRD_TOP_SPEED = 20;
	private static final float BIRD_TURN_SPEED = 5;
	private static final float BIRD_TURN_CHANCE = 0.3f;
	private static final float BIRD_SWITCH_CHANCE = 0.9999f;
	private static final float SEEK_DISTANCE = 2;

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
		this.geometry = new Geometry("bird", makeView());
		this.geometry.setMaterial(material);
		this.geometry.setLocalTranslation(location);
		this.random = new Random();
		this.turnDirection = 1;
	}

	public Vector3f getLocation() {
		return this.location;
	}

	private Line makeView() {
		return new Line(this.location, tailLocation());
	}

	private Vector3f tailLocation() {
		float x = this.location.x - this.direction.x;
		float y = this.location.y - this.direction.y;
		return new Vector3f(x, y, 0);
	}

	public Geometry getGeometry() {
		return this.geometry;
	}

	public void update(List<Bird> birds, float tpf) {
		seek(birds, tpf);
		move(tpf);
	}

	private void seek(List<Bird> birds, float tpf) {
		List<Bird> neighbours = new ArrayList<>();
		for (Bird bird : birds) {
			if (!bird.equals(this)) {
				// System.out.println(this.location.distance(bird.getLocation()));
				if (SEEK_DISTANCE > this.location.distance(bird.getLocation())) {
					neighbours.add(bird);
				}
			}
		}
		if (neighbours.size() > 0) {
			Vector3f averageLocation = Vector3f.ZERO;
			for (int i = 0; i < neighbours.size(); i++) {
				averageLocation.add(neighbours.get(i).getLocation());
				if (i > 0) {
					averageLocation.mult(0.5f);
				}
			}
			float angleToAverage = this.location.angleBetween(averageLocation);
			// System.out.println(angleToAverage);
			randomTurn(tpf);
		} else {
			randomTurn(tpf);
		}
	}

	private void randomTurn(float tpf) {
		float r = this.random.nextFloat();
		boolean turnBird = r * tpf * 1000 > BIRD_TURN_CHANCE;
		boolean switchBird = r * tpf * 1000 > BIRD_SWITCH_CHANCE;

		if (turnBird) {
			// System.out.println("Direction Before: " + this.direction);
			float turnValue = BIRD_TURN_SPEED * r * this.turnDirection * tpf;
			Quaternion quaternion = new Quaternion(new float[] { 0, 0,
					turnValue });
			this.geometry.rotate(quaternion);
			this.direction = quaternion.mult(this.direction);
			if (switchBird) {
				this.turnDirection *= -1;
			}
			// System.out.println("Direction After: " + this.direction);
		}
	}

	private void move(float tpf) {
		// System.out.println("Location Before: " + this.location);
		// System.out.println("Geometry Before: "
		// + this.geometry.getLocalTransform().getTranslation());
		Vector3f movement = this.direction.mult(tpf * BIRD_TOP_SPEED);
		this.location.addLocal(movement);
		this.geometry.move(movement);
		// System.out.println("Location After: " + this.location);
		// System.out.println("Geometry After: "
		// + this.geometry.getLocalTransform().getTranslation());
	}
}
