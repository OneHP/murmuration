package domain;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class Bird {

	private static final float BIRD_TOP_SPEED = 1f;
	private static final float BIRD_TURN_SPEED = 1.5f;
	private static final float BIRD_SWITCH_CHANCE_PER_SEC = 0.3f;
	private static final float SEEK_DISTANCE = 2f;

	private final float speed;
	private final Geometry geometry;
	private final Random random;
	private float turnDirection;
	private float tpfCount;
	private final float id;
	private final SimpleApplication app;

	public Bird(Vector3f location, float direction, float speed,
			Material material, SimpleApplication app) {
		super();
		this.speed = speed;
		this.geometry = new Geometry("bird", makeView(location));
		this.geometry.setMaterial(material);
		this.geometry.rotate(0, 0, direction);
		this.geometry.setLocalTranslation(location);
		this.random = new Random();
		this.turnDirection = -1;
		this.id = this.random.nextFloat();
		this.app = app;
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
		// removeIntention();
		seek(birds, tpf);
		move(tpf);
	}

	private void seek(List<Bird> birds, float tpf) {
		List<Bird> neighbours = Lists.newArrayList(Iterables.filter(birds,
				new Predicate<Bird>() {
					@Override
					public boolean apply(Bird input) {
						if (Bird.this.id == input.id) {
							return false;
						}
						if (SEEK_DISTANCE < Bird.this.getLocation().distance(
								input.getLocation())) {
							return false;
						}
						Vector3f direction = input.getLocation()
								.subtract(Bird.this.getLocation()).normalize();
						Vector3f heading = Bird.this.geometry
								.getLocalRotation().getRotationColumn(1, null)
								.normalize();
						float dot = direction.dot(heading);
						if (dot < 0) {
							return false;
						}
						Vector3f otherHeading = input.getGeometry()
								.getLocalRotation().getRotationColumn(1, null)
								.normalize();
						float withDot = otherHeading.dot(heading);
						if (withDot < 0) {
							return false;
						}
						return true;
					}
				}));

		if (neighbours.size() > 0) {

			neighbours = ImmutableList.copyOf(Ordering.from(
					new Comparator<Bird>() {
						@Override
						public int compare(Bird o1, Bird o2) {
							return Float.compare(Bird.this.getLocation()
									.distance(o1.getLocation()), Bird.this
									.getLocation().distance(o2.getLocation()));
						}
					}).sortedCopy(neighbours));

			if (Bird.this.getLocation().distance(
					neighbours.get(0).getLocation()) > 0.2f) {
				Vector3f averageNeighbour = new Vector3f();
				for (int i = 0; i < Math.min(neighbours.size(), 6); i++) {
					averageNeighbour.addLocal(neighbours.get(i).getLocation());
					if (i > 0) {
						averageNeighbour.multLocal(0.5f);
					}
				}
				// drawIntention(Bird.this.getLocation(), averageNeighbour);

				Vector3f heading = Bird.this.geometry.getLocalRotation()
						.getRotationColumn(1, null).normalize();
				Vector3f direction = averageNeighbour.subtract(
						Bird.this.getLocation()).normalize();
				Vector3f change = direction.subtract(heading).normalize();

				boolean clockWise = heading.y * change.x > 0;

				float turnValue = BIRD_TURN_SPEED * (clockWise ? -1 : 1) * tpf;

				this.geometry.rotate(0, 0, turnValue);

			} else {
				randomTurn(tpf);
			}
		} else {
			randomTurn(tpf);
		}
	}

	private void drawIntention(Vector3f from, Vector3f to) {
		Material mat = new Material(this.app.getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);

		Material mat2 = new Material(this.app.getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setColor("Color", ColorRGBA.Yellow);

		Line line = new Line(from, to);
		Geometry geometry2 = new Geometry(Float.toString(this.id), line);
		geometry2.setMaterial(mat);
		this.app.getRootNode().attachChild(geometry2);

		Line line2 = new Line(from, from.subtract(new Vector3f(0, 0.1f, 0)));
		Geometry geometry3 = new Geometry(Float.toString(this.id) + ":", line2);
		geometry3.setMaterial(mat2);
		this.app.getRootNode().attachChild(geometry3);
	}

	private void removeIntention() {
		this.app.getRootNode().detachChildNamed(Float.toString(this.id));
		this.app.getRootNode().detachChildNamed(Float.toString(this.id) + ":");
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
