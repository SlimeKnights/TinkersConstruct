package mods.tinker.tconstruct.client.tmt;

import java.util.ArrayList;

import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class TextureGroup
{
    public TextureGroup()
    {
        poly = new ArrayList<TexturedQuad>();
        texture = "";
    }

    public void addPoly (TexturedQuad quad)
    {
        poly.add(quad);
    }

    public void loadTexture ()
    {
        loadTexture(-1);
    }

    public void loadTexture (int defaultTexture)
    {
        if (!texture.equals(""))
        {
            TextureManager renderengine = RenderManager.instance.renderEngine;
            renderengine.func_110577_a(new ResourceLocation(texture));
        }
        /*else if(defaultTexture > -1)
        {
        	RenderManager.instance.renderEngine.bindTexture(defaultTexture);
        }*/
    }

    public ArrayList<TexturedQuad> poly;
    public String texture;
}
