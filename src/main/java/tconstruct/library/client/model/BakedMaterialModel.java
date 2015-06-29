package tconstruct.library.client.model;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.materials.Material;
import tconstruct.library.tinkering.IMaterialItem;

/**
 * This class represents something that has a single material. The base model is the default without a material. The
 * parts represent the different materials. Tools etc. are built out of multiple of these models
 *
 * ..basically it's a simple (Itemmeta -> Model) model
 */
public class BakedMaterialModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel {

  protected Map<String, IFlexibleBakedModel> parts;

  public BakedMaterialModel(IFlexibleBakedModel base) {
    super(base, Attributes.DEFAULT_BAKED_FORMAT);

    this.parts = new THashMap<>(TinkerRegistry.getAllMaterials().size());
  }

  public void addMaterialModel(Material material, IFlexibleBakedModel model) {
    parts.put(material.identifier, model);
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    if(stack.getItem() instanceof IMaterialItem) {
      String id = ((IMaterialItem) stack.getItem()).getMaterialID(stack);
      return getModelByIdentifier(id);
    }
    return this;
  }

  public IFlexibleBakedModel getModelByIdentifier(String identifier) {
    IFlexibleBakedModel materialModel = parts.get(identifier);
    if(materialModel == null) {
      return this;
    }

    return materialModel;
  }
}
