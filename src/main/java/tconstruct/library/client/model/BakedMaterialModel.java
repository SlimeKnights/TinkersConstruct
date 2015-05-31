package tconstruct.library.client.model;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

/**
 * This class represents something that has a single material. The base model is the default without a material. The
 * parts represent the different materials. Tools etc. are built out of multiple of these models
 *
 * ..basically it's a simple (Itemmeta -> Model) model
 */
public class BakedMaterialModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel {

  protected TIntObjectMap<IFlexibleBakedModel> parts;

  public BakedMaterialModel(IFlexibleBakedModel base) {
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
    if (materialModel == null) {
      return this;
    }

    return materialModel;
  }
}
