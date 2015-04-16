package tconstruct.test;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.IFlexibleBakedModel;

import java.util.Map;

import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.tools.TinkerMaterials;

public class BakedTinkerModel extends BakedMultiModel {
  private final Map<String, TextureAtlasSprite> textures = Maps.newHashMap();
  private final Map<String, TextureAtlasSprite> brokenTextures = Maps.newHashMap();

  public BakedTinkerModel(ItemCameraTransforms transforms,
                          IFlexibleBakedModel original,
                          IFlexibleBakedModel... models) {
    super(transforms, original, models);
  }

  public void addTexture(String material, int part, TextureAtlasSprite texture) {
    textures.put(material + part, texture);
  }

  public void addBrokenTexture(String material, int part, TextureAtlasSprite texture) {
    brokenTextures.put(material + part, texture);
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
/*
    if(tag == null || !tag.hasKey(Tags.TINKER_DATA))
      return null;

    tag = tag.getCompoundTag(Tags.TINKER_DATA);
    */

    tag = new NBTTagCompound();
    tag.setString("0", TinkerMaterials.stone.identifier);
    tag.setString("1", TinkerMaterials.wood.identifier);
    tag.setString("2", TinkerMaterials.wood.identifier);

    TextureAtlasSprite[] tex = new TextureAtlasSprite[subModels.size()];

    // get the texture for each part
    for(int i = 0; i < tex.length; i++) {
      String part = tag.getString(String.valueOf(i)) + i;
      TextureAtlasSprite partTexture;
      // todo: if broken use broken texture
      partTexture = textures.get(part);

      tex[i] = partTexture;
    }

    return bakeModel(tex);
  }
}
