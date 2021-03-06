package com.interfacesgraphiqueset3d.tp5;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Application extends ApplicationAdapter {
	private FitViewport viewport;
	private PerspectiveCamera camera;
	private Pixmap pixels;
	private Texture textureWithPixels;
	private SpriteBatch spriteBatch;
	private Vector2 currentScreen; // X et Y du pixel courant
	private Vector3 currentScene;
	private Vector3 tmpVector3;
	private Rayon rayonLance;

	// ************** */
	private Sphere sphereMDR;
	private Vector3 centerVector;

	@Override
	public void create() {
		// Get screen dimensions, in pixels :
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		// Create a camera with perspective view :
		camera = new PerspectiveCamera(50.0f, screenWidth, screenHeight);
		camera.position.set(0f, 0f, -10f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 500f;
		camera.update();

		Vector3 currentPixel = new Vector3(50, 40, 0); // Representation d'un pixel de l'écran

		// Create a viewport to convert coords of screen space into coords of scene
		// space.
		viewport = new FitViewport(screenWidth, screenHeight, camera);

		Vector3 position3D = viewport.unproject(currentPixel);

		Vector3 directionRayon = new Vector3(position3D.x - camera.position.x, position3D.y - camera.position.y,
				position3D.z - camera.position.z); // calcul de la direction du rayon

		Rayon rayonLance = new Rayon(camera.position, directionRayon); // Création du rayon avec le centre et une
																		// direction

		// Create an array of pixels, initialized with grey color :
		pixels = Pixmap.createFromFrameBuffer(0, 0, screenWidth, screenHeight);
		for (int y = 0; y < screenHeight; y++) {
			for (int x = 0; x < screenWidth; x++) {
				pixels.setColor(0.1f, 0.1f, 0.1f, 1f);
				pixels.drawPixel(x, y);
			}
		}

		// Add pixels in a Texture in order to render them :
		spriteBatch = new SpriteBatch();
		textureWithPixels = new Texture(pixels);

		// ************ */

		centerVector = new Vector3(1.0f, 2.0f, 1.0f); // On crée le centre de la Sphère
		sphereMDR = new Sphere(centerVector, 9f); // On crée la Sphère

		Vector3 intersectionRS = new Vector3();

		boolean test = intersectRaySphere(rayonLance, centerVector, 9f, intersectionRS); // Test de l'intersection entre
																							// le rayonn et la sphère

		// Initialize coords of the first pixel, in screen space :
		currentScreen = new Vector2(0, 0);

		// Others initializations :
		currentScene = new Vector3();
		tmpVector3 = new Vector3();
	}

	/**
	 * Intersects a {@link Ray} and a sphere, returning the intersection point in
	 * intersection.
	 * 
	 * @param ray          The ray, the direction component must be normalized
	 *                     before calling this method
	 * @param center       The center of the sphere
	 * @param radius       The radius of the sphere
	 * @param intersection The intersection point (optional, can be null)
	 * @return Whether an intersection is present.
	 */
	public static boolean intersectRaySphere(Rayon ray, Vector3 center, float radius, Vector3 intersection) {
		final float len = ray.direction.dot(center.x - ray.origin.x, center.y - ray.origin.y, center.z - ray.origin.z);
		if (len < 0.f) // behind the ray
			return false;
		final float dst2 = center.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len,
				ray.origin.z + ray.direction.z * len);
		final float r2 = radius * radius;
		if (dst2 > r2)
			return false;
		if (intersection != null)
			intersection.set(ray.direction).scl(len - (float) Math.sqrt(r2 - dst2)).add(ray.origin);
		return true;
	}

	@Override
	public void render() {
		// If "ctrl + s" is pressed :
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.S)) {
			// Save the pixels into a png file :
			savePixelsInPngFile();
		}

		// If "escape" is pressed :
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			// Close th application :
			Gdx.app.exit();
		}

		// Reset the screen buffer colors :
		ScreenUtils.clear(0, 0, 0, 1);

		// Process pixels color :
		processPixel();

		// Render the texture with pixels :
		spriteBatch.begin();
		spriteBatch.draw(textureWithPixels, 0, 0);
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		textureWithPixels.dispose();
		pixels.dispose();
	}

	/**
	 * Compute the color of each screen pixels and store the results in the pixels
	 * map.
	 */
	private boolean processPixel() {
		boolean isOk = true;

		// Get color of current pixel :
		Vector3 color = getColor((int) currentScreen.x, (int) currentScreen.y);

		// Save color into pixels map :
		pixels.setColor(color.x, color.y, color.z, 1f);
		pixels.drawPixel((int) currentScreen.x, (int) currentScreen.y);

		return isOk;
	}

	/**
	 * Wrire pixels in the png file "core/assets/render.png". If the file already
	 * exists it will be overrided.
	 */
	private boolean savePixelsInPngFile() {
		boolean isOk = true;

		// Create file :
		FileHandle file = Gdx.files.local("render.png");

		// Write pixels in file :
		PixmapIO.writePNG(file, pixels);

		return isOk;
	}

	/**
	 * Return the color processed with path tracing and Phong method for the given
	 * pixel.
	 */
	private Vector3 getColor(int xScreen, int yScreen) {
		Vector3 color = new Vector3(1.f, 0f, 0f);

		// Get coords of current pixel, in scene space :
		tmpVector3.set(xScreen, yScreen, 0);
		currentScene = viewport.unproject(tmpVector3);

		// To be continued ...

		return color;
	}
}