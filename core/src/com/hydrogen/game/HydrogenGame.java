package com.hydrogen.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

public class HydrogenGame extends ApplicationAdapter {

    OrthographicCamera camera;
    SpriteBatch batch;
	Texture headImage;
	Texture swordImage;
	Sound birdSound;
	Music whereMusic;

    Rectangle sword;
    Vector3 touchPos;

    Array<Rectangle> heads;
    long lastHeadTime;

    @Override
	public void create () {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        batch = new SpriteBatch();

        touchPos = new Vector3();

        headImage = new Texture("ic_top.png");
        swordImage = new Texture("ic_bot.png");

        birdSound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));
        whereMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        whereMusic.setLooping(true);
        whereMusic.play();

        sword = new Rectangle();
        sword.x = 800 / 2 - 64 / 2;
        sword.y = 20;
        sword.width = 64;
        sword.height = 64;

        heads = new Array<Rectangle>();
        spawnHeads();

    }

    private void spawnHeads() {
        Rectangle head = new Rectangle();
        head.x = MathUtils.random(0, 800 - 64);
        head.y = 480;
        head.width = 64;
        head.height = 64;
        lastHeadTime = TimeUtils.nanoTime();
        heads.add(head);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(swordImage, sword.x, sword.y);
        for (Rectangle head : heads) {
            batch.draw(headImage, head.x, head.y);
        }
        batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            sword.x = (int) (touchPos.x - 64 / 2);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sword.x -= 200 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sword.x += 200 * Gdx.graphics.getDeltaTime();
        }

        if (sword.x < 0) {
            sword.x = 0;
        }

        if (sword.x > 800 - 64) {
            sword.x = 800 - 64;
        }

        if (TimeUtils.nanoTime() - lastHeadTime > 1000000000) {
            spawnHeads();
        }

        Iterator<Rectangle> iterator = heads.iterator();
        while (iterator.hasNext()) {
            Rectangle head = iterator.next();
            head.y -= 200 * Gdx.graphics.getDeltaTime();

            if (head.y + 64 < 0) {
                iterator.remove();
            }

            if (head.overlaps(sword)) {
                birdSound.play();
                iterator.remove();
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        headImage.dispose();
        swordImage.dispose();
        birdSound.dispose();
        whereMusic.dispose();
        batch.dispose();
    }
}