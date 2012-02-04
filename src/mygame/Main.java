package mygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		Vector3f up = new Vector3f(0, 1, 0);
		this.getCamera().lookAt(Vector3f.ZERO, up);

		this.mouseInput.setCursorVisible(true);
		this.inputManager.clearMappings();

		Material mat = new Material(this.assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);

		Random random = new Random();

		this.birds = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Bird testBird = new Bird(new Vector3f(0, 0, 0), new Vector3f(0.1f,
					0, 0), 0, mat);
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
