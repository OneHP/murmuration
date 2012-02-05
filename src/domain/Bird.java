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

	private static final float BIRD_TOP_SPEED = 2f;
	private static final float BIRD_TURN_SPEED = 1f;
	private static final float BIRD_SWITCH_CHANCE_PER_SEC = 0.3f;
	private static final float SEEK_DISTANCE = 0;

	private final float speed;
	private final Geometry geometry;
	private final Random random;
	private float turnDirection;
	private float tpfCount;

	public Bird(Vector3f location, float direction, float speed,
			Material material) {
		super();
		this.speed = speed;
		this.geometry = new Geometry("bird", makeView(location));
		this.geometry.setMaterial(material);
		this.geometry.rotate(0, 0, direction);
		this.geometry.setLocalTranslation(location);
		this.random = new Random();
		this.turnDirection = 1;
	}

	public Vector3f getLocation() {
		return this.geometry.getLocalTranslation();
	}

	private Line makeView(Vector3f location) {
		return new Line(Vector3f.ZERO, new Vector3f(0, 0.1f, 0));
	}

	public Geometry getGeometry() {
		return this.geometry;
	}

	public void update(List<Bird> birds, float tpf) {
		// seek(birds, tpf);
		randomTurn(tpf);
		move(tpf);
	}

	private void seek(List<Bird> birds, float tpf) {
		List<Bird> neighbours = new ArrayList<Bird>();
		for (Bird bird : birds) {
			if (!bird.equals(this)) {
				if (SEEK_DISTANCE > this.getLocation().distance(
						bird.getLocation())) {
					neighbours.add(bird);
				}
			}
		}
		if (neighbours.size() > 0) {
			Vector3f averageDirection = new Vector3f();
			for (int i = 0; i < neighbours.size(); i++) {
				averageDirection.addLocal(neighbours.get(i).getGeometry()
						.getLocalTransform().getTranslation());
				if (i > 0) {
					averageDirection.multLocal(0.5f);
				}
			}
			Vector3f difference = averageDirection.subtract(this.getGeometry()
					.getLocalTransform().getTranslation());

			float turnValue = BIRD_TURN_SPEED
					* ((difference.angleBetween(new Vector3f(0, 1, 0)) > 3.14f) ? 1
							: -1) * tpf;
			Quaternion quaternion = new Quaternion(new float[] { 0, 0,
					turnValue });
			// this.geometry.rotate(quaternion);

		} else {
			randomTurn(tpf);
		}
	}

	private void randomTurn(float tpf) {
		float turnValue = BIRD_TURN_SPEED * this.turnDirection * tpf;
		this.geometry.rotate(0, 0, turnValue);
		this.tpfCount += tpf;
		if (this.tpfCount > 1) {
			if (this.random.nextFloat() < BIRD_SWITCH_CHANCE_PER_SEC) {
				this.turnDirection *= -1;
			}
			this.tpfCount -= 1;
		}
	}

	private void move(float tpf) {
		Vector3f movement = this.geometry.getLocalRotation()
				.getRotationColumn(1, null).mult(tpf * BIRD_TOP_SPEED);
		this.geometry.move(movement);
	}
}
