package tconstruct.library.client;

import com.google.common.collect.Maps;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.model.BakedMultiModel;
import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.ToolPart;
import tconstruct.library.utils.TagUtil;
import tconstruct.tools.TinkerMaterials;

/**
 * This class represents something that has a single material.
 * The base model is the default without a material.
 * The parts represent the different materials.
 * Tools etc. are built out of multiple of these models
 *
 * ..basically it's a simple (Itemmeta -> Model) model
 */
public class BakedMaterialModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel {
  protected TIntObjectMap<IFlexibleBakedModel> parts;

  public BakedMaterialModel(IFlexibleBakedModel base)
  {
    super(base, Attributes.DEFAULT_BAKED_FORMAT);

    this.parts = new TIntObjectHashMap<>(TinkerRegistry.getAllMaterials().size());
  }

  public void addMaterialModel(Material material, IFlexibleBakedModel model) {
    parts.put(material.metadata, model);
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    return getModelByMetadata(stack.getItemDamage());
  }

  public IBakedModel getModelByMetadata(int meta) {
    IFlexibleBakedModel materialModel = parts.get(meta);
    if(materialModel == null)
      return this;

    return materialModel;
  }
}
