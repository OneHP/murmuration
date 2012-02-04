package mygame;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;

import domain.Bird;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

	private List<Bird> birds;
	
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	
    	Vector3f up = new Vector3f(0,1,0);
    	this.getCamera().lookAt(Vector3f.ZERO, up);
    	
    	this.mouseInput.setCursorVisible(true);
        this.inputManager.clearMappings();
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
    	
        birds = new ArrayList<>();
        Bird testBird = new Bird(Vector3f.ZERO, new Vector3f(0.1f, 0, 0), 0, mat);
        birds.add(testBird);

        for(Bird bird : birds){
        	rootNode.attachChild(bird.getGeometry());
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
    	 for(Bird bird : birds){
    		 bird.update(tpf);
    	}
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
