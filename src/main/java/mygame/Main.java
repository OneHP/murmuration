package mygame;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

import domain.Bird;
import domain.Pond;

public class Main extends SimpleApplication {

	private List<Bird> birds;

	public static void main(String[] args) {
		Main app = new Main();
		app.start();
	}

	@Override
	public void simpleInitApp() {

		this.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		this.mouseInput.setCursorVisible(true);
		this.inputManager.clearMappings();

		Material birdMaterial = new Material(this.assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		birdMaterial.setColor("Color", ColorRGBA.White);

		Material pondMaterial = new Material(this.assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		pondMaterial.setColor("Color", ColorRGBA.Blue);

		this.birds = new ArrayList<Bird>();
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				Bird testBird = new Bird(new Vector3f(4 - i * 0.3f,
						3 - j * 0.2f, -15), FastMath.nextRandomFloat() * i * j,
						birdMaterial, this);
				this.birds.add(testBird);
			}
		}

		for (Bird bird : this.birds) {
			this.rootNode.attachChild(bird.getGeometry());
		}

		this.rootNode.attachChild(new Pond(new Vector3f(0, 0, -16),
				pondMaterial).getGeometry());

	}

	@Override
	public void simpleUpdate(float tpf) {
		for (Bird bird : this.birds) {
			bird.update(this.birds, tpf);
		}
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}
}
