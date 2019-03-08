package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Font {
	private static final String FONT_NAME = "verdana.ttf";
	
	private static FreeTypeFontGenerator generator;
	private static FreeTypeFontParameter parameter;
		
	public static BitmapFont get(int size) {
		generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_NAME));
		parameter = new FreeTypeFontParameter();
		parameter.size = size;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		parameter = null;
		
		return font;
	}
		
	private Font() { }
}
