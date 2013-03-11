package mods.tinker.tconstruct.client.tmt;

import java.util.ArrayList;

import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.RenderManager;

public class TextureGroup
{
	public TextureGroup()
	{
		poly = new ArrayList<TexturedQuad>();
		texture = "";
	}
	
	public void addPoly(TexturedQuad quad)
	{
		poly.add(quad);
	}

	public void loadTexture()
	{
		loadTexture(-1);
	}
	
	public void loadTexture(int defaultTexture)
	{
		if(!texture.equals(""))
		{
			RenderEngine renderengine = RenderManager.instance.renderEngine;
	        renderengine.func_98187_b(texture);
		}
		/*else if(defaultTexture > -1)
		{
			RenderManager.instance.renderEngine.func_98187_b(defaultTexture);
		}*/
	}
	
	public ArrayList<TexturedQuad> poly;
	public String texture;
}
