package tconstruct.test;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

public class CustomTextureCreator {
  public static Map<String, TextureAtlasSprite> sprites = Maps.newHashMap();

  @SubscribeEvent
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    TextureMap map = event.map;

    TextureAtlasSprite head = map.getTextureExtry("tconstruct:items/pickaxe/_pickaxe_head");

    for(Material material : TinkerRegistry.getAllMaterials())
    {
      SimpleColoredTexture
          coloredTexture = new SimpleColoredTexture(material, head, String.format("tconstruct:items/pickaxe/%s_pickaxe_head", material.identifier));
      map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_head", material.identifier), coloredTexture);

      sprites.put("pick_head_" + material.identifier, coloredTexture);
    }

    TextureAtlasSprite handle = map.getTextureExtry("tconstruct:items/pickaxe/_pickaxe_handle");

    for(Material material : TinkerRegistry.getAllMaterials())
    {
      SimpleColoredTexture
          coloredTexture = new SimpleColoredTexture(material, handle, String.format("tconstruct:items/pickaxe/%s_pickaxe_handle", material.identifier));
      map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_handle", material.identifier), coloredTexture);

      sprites.put("pick_handle_" + material.identifier, coloredTexture);
    }

    TextureColoredTexture tex = new TextureColoredTexture(map.getTextureExtry("minecraft:blocks/netherrack"), head, String.format("tconstruct:items/pickaxe/%s_pickaxe_head", "woodlog"));
    map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_head", "woodlog"), tex);
    sprites.put("pick_head_woodlog", tex);
  }
}
