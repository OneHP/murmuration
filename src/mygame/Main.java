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

		Vector3f up = new Vector3f(0, 1, 0);
		this.getCamera().lookAt(Vector3f.ZERO, up);

		mouseInput.setCursorVisible(true);
		inputManager.clearMappings();

		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);

		birds = new ArrayList<>();
		Bird testBird = new Bird(Vector3f.ZERO, new Vector3f(0.1f, 0, 0), 0,
				mat);
		birds.add(testBird);
		Bird testBird2 = new Bird(Vector3f.ZERO, new Vector3f(0.1f, 0, 0), 0,
				mat);
		birds.add(testBird2);
		Bird testBird3 = new Bird(Vector3f.ZERO, new Vector3f(0.1f, 0, 0), 0,
				mat);
		birds.add(testBird3);
		Bird testBird4 = new Bird(Vector3f.ZERO, new Vector3f(0.1f, 0, 0), 0,
				mat);
		birds.add(testBird4);

		for (Bird bird : birds) {
			rootNode.attachChild(bird.getGeometry());
		}
	}

	@Override
	public void simpleUpdate(float tpf) {
		for (Bird bird : birds) {
			bird.update(tpf);
		}
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}
}
