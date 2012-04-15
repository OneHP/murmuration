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
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Line;

public class Bird {

	private static final int NEIGHBOURS_TO_AVERAGE = 6;
	private static final float SPACING = 0.2f;
	private static final float BIRD_TOP_SPEED = 1f;
	private static final float BIRD_TURN_SPEED = 1.5f;
	private static final float BIRD_SWITCH_CHANCE_PER_SEC = 0.3f;
	private static final float SEEK_DISTANCE = 2f;

	private static final Mesh BIRD_MESH = new BirdMesh();

	private final Geometry geometry;
	private final Random random;
	private float turnDirection;
	private float tpfCount;
	private final float id;
	private final SimpleApplication app;

	public Bird(Vector3f location, float direction, Material material,
			SimpleApplication app) {
		this.geometry = new Geometry("bird", BIRD_MESH);
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
				new NeighbourPredicate()));

		if (neighbours.size() > 0) {

			neighbours = ImmutableList.copyOf(Ordering.from(
					new DistanceComparator()).sortedCopy(neighbours));

			if (freeSpaceAroundBird(neighbours)) {

				// Work out the average position of our neighbours
				Vector3f averageNeighbour = new Vector3f();
				for (int i = 0; i < Math.min(neighbours.size(),
						NEIGHBOURS_TO_AVERAGE); i++) {
					averageNeighbour.addLocal(neighbours.get(i).getLocation());
					if (i > 0) {
						averageNeighbour.multLocal(0.5f);
					}
				}
				// drawIntention(Bird.this.getLocation(), averageNeighbour);

				// Work out if we need to turn clockwise or counter-clockwise
				Vector3f heading = Bird.this.geometry.getLocalRotation()
						.getRotationColumn(1, null).normalize();
				Vector3f direction = averageNeighbour.subtract(
						Bird.this.getLocation()).normalize();
				Vector3f change = direction.subtract(heading).normalize();
				boolean clockWise = heading.y * change.x > 0;

				// Apply the turn
				float turnValue = BIRD_TURN_SPEED * (clockWise ? -1 : 1) * tpf;
				this.geometry.rotate(0, 0, turnValue);

			} else {
				randomTurn(tpf);
			}
		} else {
			randomTurn(tpf);
		}
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	private boolean freeSpaceAroundBird(List<Bird> neighbours) {
		return Bird.this.getLocation()
				.distance(neighbours.get(0).getLocation()) > SPACING;
	}

	/**
	 * Ordering by distance from our bird, closest first
	 */
	private final class DistanceComparator implements Comparator<Bird> {
		@Override
		public int compare(Bird o1, Bird o2) {
			return Float.compare(
					Bird.this.getLocation().distance(o1.getLocation()),
					Bird.this.getLocation().distance(o2.getLocation()));
		}
	}

	/**
	 * Determine whether the given bird is a neighbour of our bird
	 */
	private final class NeighbourPredicate implements Predicate<Bird> {
		@Override
		public boolean apply(Bird input) {
			if (birdIsSame(input)) {
				return false;
			}
			if (birdIsNotClose(input)) {
				return false;
			}
			// Get the dot product of the direction of our bird to the neighbour
			// and the heading of our bird to work out if the neighbour is in
			// front of our bird
			Vector3f direction = input.getLocation()
					.subtract(Bird.this.getLocation()).normalize();
			Vector3f heading = Bird.this.geometry.getLocalRotation()
					.getRotationColumn(1, null).normalize();
			float dot = direction.dot(heading);
			if (dot < 0) {
				return false;
			}
			// Get the dot product of the heading of our bird and the heading of
			// the neighbour to work out if the neighbour is travelling in
			// roughly the same direction as our bird
			Vector3f otherHeading = input.getGeometry().getLocalRotation()
					.getRotationColumn(1, null).normalize();
			float withDot = otherHeading.dot(heading);
			if (withDot < 0) {
				return false;
			}
			return true;
		}

		private boolean birdIsNotClose(Bird input) {
			return SEEK_DISTANCE < Bird.this.getLocation().distance(
					input.getLocation());
		}

		private boolean birdIsSame(Bird input) {
			return Bird.this.id == input.id;
		}
	}
}
