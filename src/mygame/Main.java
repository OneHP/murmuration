package mygame;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;

import domain.Bird;

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

		Material mat = new Material(this.assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);

		this.birds = new ArrayList<Bird>();
		for (int i = 0; i < 100; i++) {
			Bird testBird = new Bird(new Vector3f(5 - i * 0.1f, 5 - i * 0.1f,
					-5), i, 0, mat);
			this.birds.add(testBird);
		}

		for (Bird bird : this.birds) {
			this.rootNode.attachChild(bird.getGeometry());
		}

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
