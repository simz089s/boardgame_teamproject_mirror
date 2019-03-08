package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TextureLoader {
	private static Skin skin;
	private static TextureAtlas atlas;
			
	public static Skin getSkin() {
		if(skin == null) new TextureLoader();
		return skin;
	}
	
	public static Drawable getDrawable(String name) {
		if(skin == null) new TextureLoader();
		return skin.getDrawable(name);
	}
	
	public static Drawable getDrawable(int width, int height, Color col) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		pixmap.setColor(col);
		pixmap.fill();
		Drawable drw = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
		pixmap.dispose();
				
		return drw;
	}

}
