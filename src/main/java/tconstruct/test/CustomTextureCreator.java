package tconstruct.test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;
import tconstruct.library.utils.Log;

public class CustomTextureCreator {
  //public static Map<String, TextureAtlasSprite> sprites = Maps.newHashMap();
  private static Set<ResourceLocation> baseTextures = Sets.newHashSet();

  /**
   * Holds all sprites built from the base-texture used as the key.
   */
  public static Map<ResourceLocation, Map<String, TextureAtlasSprite>> sprites = Maps.newHashMap();

  public static void registerTextures(Collection<ResourceLocation> textures) {
    baseTextures.addAll(textures);
  }

  @SubscribeEvent
  public void createCustomTextures(TextureStitchEvent.Pre event) {
    TextureMap map = event.map;

    for(ResourceLocation baseTexture : baseTextures) {
      // exclude missingno :I
      if(baseTexture.toString().equals("minecraft:missingno"))
        continue;

      Map<String, TextureAtlasSprite> builtSprites = Maps.newHashMap();
      for(Material material : TinkerRegistry.getAllMaterials()) {
        TextureAtlasSprite base = map.getTextureExtry(baseTexture.toString());
        if(base == null) {
          Log.error("Missing base texture: " + baseTexture.toString());
          continue;
        }

        // todo: determine if the texture is present and does not have to be generated

        String location = baseTexture.toString() + "_" + material.identifier;

        TextureAtlasSprite sprite;
        sprite = new SimpleColoredTexture(material, base, location);

        map.setTextureEntry(location, sprite);

        builtSprites.put(material.identifier, sprite);
      }

      sprites.put(baseTexture, builtSprites);
    }
/*
    for(Item tool : TinkerRegistry.tools) {

    }

    TextureColoredTexture tex = new TextureColoredTexture(map.getTextureExtry("minecraft:items/fish_pufferfish_raw"), "tconstruct:items/pickaxe/_pickaxe_head", "full", String.format("tconstruct:items/pickaxe/%s_pickaxe_head", "Wood"));
    map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_head", "Wood"), tex);
    sprites.put("pick_head_" + "Wood", tex);

    TextureAtlasSprite sprite = map.getTextureExtry("tconstruct:items/pickaxe/pick_head_");

    for(Material material : TinkerRegistry.getAllMaterials())
    {
      SimpleColoredTexture coloredTexture;
      if(material.baseTexture.isEmpty())
        coloredTexture = new SimpleColoredTexture(material, sprite, String.format("tconstruct:items/pickaxe/%s_pickaxe_head", material.identifier));
      else
        coloredTexture = new SimpleColoredTexture(material, sprite.getIconName(), material.baseTexture, String.format("tconstruct:items/pickaxe/%s_pickaxe_head", material.identifier));
      map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_head", material.identifier), coloredTexture);

      sprites.put("pick_head_" + material.identifier, coloredTexture);
    }

    sprite = map.getTextureExtry("tconstruct:items/pickaxe/pick_handle_");

    for(Material material : TinkerRegistry.getAllMaterials())
    {
      SimpleColoredTexture coloredTexture;
      if(material.baseTexture.isEmpty())
        coloredTexture = new SimpleColoredTexture(material, sprite, String.format("tconstruct:items/pickaxe/%s_pickaxe_handle", material.identifier));
      else
        coloredTexture = new SimpleColoredTexture(material, sprite.getIconName(), material.baseTexture, String.format("tconstruct:items/pickaxe/%s_pickaxe_handle", material.identifier));
      map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_handle", material.identifier), coloredTexture);

      sprites.put("pick_handle_" + material.identifier, coloredTexture);
    }

    map.setTextureEntry(String.format("tconstruct:items/pickaxe/%s_pickaxe_head", "Wood"), tex);
    sprites.put("pick_head_" + "Wood", tex);
    */
  }
}
