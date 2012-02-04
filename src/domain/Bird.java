package domain;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

public class Bird extends Geometry{

	private Vector3f location;
	private Vector3f direction;
	private float speed;
	private Geometry geometry;
	
	public Bird(Vector3f location, Vector3f direction, float speed) {
		super();
		this.location = location;
		this.direction = direction;
		this.speed = speed;
		geometry = new Geometry("bird", makeView());
	}
	
	private Line makeView(){
		return new Line(location, tailLocation());
	}
	
	private Vector3f tailLocation(){
		float x = location.x - direction.x;
		float y = location.y - direction.y;
		return new Vector3f(x,y,0);
	}
	
	public Geometry getGeometry(){
		return this.geometry;
	}
	
}
