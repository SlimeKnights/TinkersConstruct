package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.model.BakedWrapper;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * This class represents something that has a single material. The base model is the default without a material. The
 * parts represent the different materials. Tools etc. are built out of multiple of these models
 *
 * ..basically it's a simple (Itemmeta -> Model) model
 */
public class BakedMaterialModel extends BakedWrapper.Perspective implements IBakedModel {

  protected Map<String, IBakedModel> parts;

  public BakedMaterialModel(IBakedModel base, ImmutableMap<TransformType, TRSRTransformation> transforms) {
    super(base, transforms);

    this.parts = new THashMap<>(TinkerRegistry.getAllMaterials().size());
  }

  public void addMaterialModel(Material material, IBakedModel model) {
    parts.put(material.identifier, model);
  }

  public IBakedModel getModelByIdentifier(String identifier) {
    IBakedModel materialModel = parts.get(identifier);
    if(materialModel == null) {
      return this;
    }

    return materialModel;
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides() {
    return MaterialItemOverrideList.INSTANCE;
  }

  private static class MaterialItemOverrideList extends ItemOverrideList {

    static MaterialItemOverrideList INSTANCE = new MaterialItemOverrideList();

    private MaterialItemOverrideList() {
      super(ImmutableList.of());
    }

    @Nonnull
    @Override
    public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      String id = ((IMaterialItem) stack.getItem()).getMaterialID(stack);
      return ((BakedMaterialModel) originalModel).getModelByIdentifier(id);
    }
  }
}
