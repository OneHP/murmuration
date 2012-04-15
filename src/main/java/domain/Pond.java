package domain;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public class Pond {

	private final Geometry geometry;

	public Pond(Vector3f location, Material material) {
		this.geometry = new Geometry("pond", new PondMesh());
		this.geometry.setMaterial(material);
		this.geometry.setLocalTranslation(location);
	}

	public Geometry getGeometry() {
		return this.geometry;
	}
}
