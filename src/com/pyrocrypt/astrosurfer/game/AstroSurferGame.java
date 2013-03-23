package com.pyrocrypt.astrosurfer.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class AstroSurferGame implements ApplicationListener {
	Texture starTexture;
	Texture shipTexture;
	Texture midFireTexture;
	Texture midFireButtonTexture;
	
	Music bgm;
	
	Sound midfireSound;
	
	OrthographicCamera mCamera;
	
	SpriteBatch mBatch;

	Array<Rectangle> starRects;
	Array<Rectangle> midFireRects;
	
	Rectangle shipRect;
	Rectangle fireButtonRect;	
	
	Vector3 fireButtonTouchPos;
	
	long starLastSpawn;
	long midFireLastSpawn;
	long spawnFreq = 10000000;
	long midFireSpawnFreq = 100000000;
	
	final int accelationMultiplier = 700;
		
	float startYVelocity = 200;
	float midFireYVelocity = 600;
	float shipXVelocity = 0.0f;
	float prevAccel = 0.0f;
	final float cameraViewPortHeight= 960;
	final float cameraViewPortWidth= 1600;
	
	final double midFireLocationRatioP1 = 2.244897959;

	private void spawnStar() {
		Rectangle rectStar = new Rectangle();
		rectStar.x = MathUtils.random(0, cameraViewPortWidth - starTexture.getWidth());
		rectStar.y = cameraViewPortHeight;
		rectStar.height = starTexture.getHeight();
		rectStar.width = starTexture.getWidth();
		starRects.add(rectStar);
		starLastSpawn = TimeUtils.nanoTime();
	}
	
	private void spawnMidFire(float x){
		Rectangle midFireRect = new Rectangle();
		midFireRect.x = x;
		midFireRect.y = shipTexture.getHeight();
		midFireRect.width = midFireTexture.getWidth();
		midFireRect.height = midFireTexture.getHeight();
		midFireRects.add(midFireRect);
		midFireLastSpawn = TimeUtils.nanoTime();
	}

	@Override
	public void create() {
		starTexture = new Texture(Gdx.files.internal("star.png"));
		shipTexture = new Texture(Gdx.files.internal("ship2_new.png"));
		midFireTexture = new Texture(Gdx.files.internal("mid_fire.png"));
		midFireButtonTexture = new Texture(Gdx.files.internal("fire_button.png"));
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("loop.mp3"));
		bgm.setLooping(true);
		bgm.play();
		
		midfireSound = Gdx.audio.newSound(Gdx.files.internal("midFireSound.mp3"));

		mCamera = new OrthographicCamera();
		mCamera.setToOrtho(false, cameraViewPortWidth, cameraViewPortHeight);

		mBatch = new SpriteBatch();
		
		fireButtonRect = new Rectangle();
		fireButtonRect.x = 0;
		fireButtonRect.y = cameraViewPortHeight-midFireButtonTexture.getHeight();
		fireButtonRect.width = midFireButtonTexture.getWidth();
		fireButtonRect.height = midFireButtonTexture.getHeight();

		starRects = new Array<Rectangle>();
		midFireRects = new Array<Rectangle>();
		
		shipRect = new Rectangle();
		shipRect.x = cameraViewPortWidth/2;
		shipRect.y = 0;
		shipRect.width = shipTexture.getWidth();
		shipRect.height = shipTexture.getHeight();
				
		spawnStar();
	}

	@Override
	public void resize(int width, int height) {
	}

	
	@Override
	
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		mCamera.update();
		
		mBatch.setProjectionMatrix(mCamera.combined);

		mBatch.begin();
			for (Rectangle rectStar : starRects) mBatch.draw(starTexture, rectStar.x, rectStar.y);
			mBatch.draw(shipTexture, shipRect.x, shipRect.y);
			if(midFireRects.size>0) for (Rectangle rectMidFire : midFireRects) mBatch.draw(midFireTexture, rectMidFire.x, rectMidFire.y);
			mBatch.draw(midFireButtonTexture, fireButtonRect.x, fireButtonRect.y);
		mBatch.end();

		if ((TimeUtils.nanoTime() - starLastSpawn) > spawnFreq){
			if (spawnFreq > 100000) spawnFreq -= 100000;
			spawnStar();	
		}
		
		Iterator<Rectangle> starIter = starRects.iterator();
		while (starIter.hasNext()) {
			Rectangle rect = starIter.next();
			rect.y -= startYVelocity * Gdx.graphics.getDeltaTime();
			if (rect.y + starTexture.getHeight() < 0) starIter.remove();
		}
				
		Iterator<Rectangle> midFireIter = midFireRects.iterator();
		while (midFireIter.hasNext()) {
			Rectangle rect = midFireIter.next();
			rect.y += midFireYVelocity * Gdx.graphics.getDeltaTime();
			if (rect.y - midFireTexture.getHeight() > cameraViewPortHeight) midFireIter.remove();
		}
		
		startYVelocity += 0.1;
		
		final float curAccel = Gdx.input.getAccelerometerY()*accelationMultiplier;
		final float fameTime = Gdx.graphics.getDeltaTime();
		if(prevAccel*curAccel>0) shipXVelocity += curAccel*fameTime;
		else shipXVelocity = shipXVelocity/50 + curAccel*fameTime;
		prevAccel=curAccel;
		shipRect.x += shipXVelocity*fameTime/2;
		if(shipRect.x < 0) shipRect.x=0;
		else if (shipRect.x + shipTexture.getWidth() > cameraViewPortWidth)
			shipRect.x = cameraViewPortWidth - shipTexture.getWidth();
		
		if(TimeUtils.nanoTime() - midFireLastSpawn > midFireSpawnFreq){
			if(Gdx.input.isTouched()){
				fireButtonTouchPos = new Vector3();
				fireButtonTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);			
				mCamera.unproject(fireButtonTouchPos);
				if (fireButtonTouchPos.x < midFireButtonTexture.getWidth()
						&& fireButtonTouchPos.y > (cameraViewPortHeight - midFireButtonTexture
								.getHeight())) {
					spawnMidFire((float)(shipRect.x + shipTexture.getWidth()/midFireLocationRatioP1 - midFireTexture.getWidth()/2));
					midfireSound.play();
				}
			}
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		starTexture.dispose();
		shipTexture.dispose();
		midFireTexture.dispose();
		midFireButtonTexture.dispose();
		bgm.dispose();
		mBatch.dispose();		
	}
}