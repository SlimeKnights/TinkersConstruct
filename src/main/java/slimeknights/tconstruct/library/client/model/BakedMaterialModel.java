package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableMap;

import gnu.trove.map.hash.THashMap;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import javax.vecmath.Matrix4f;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

/**
 * This class represents something that has a single material. The base model is the default without a material. The
 * parts represent the different materials. Tools etc. are built out of multiple of these models
 *
 * ..basically it's a simple (Itemmeta -> Model) model
 */
public class BakedMaterialModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel, IPerspectiveAwareModel {

  protected Map<String, IFlexibleBakedModel> parts;
  private final ImmutableMap<TransformType, TRSRTransformation> transforms;

  public BakedMaterialModel(IFlexibleBakedModel base, ImmutableMap<TransformType, TRSRTransformation> transforms) {
    super(base, base.getFormat());

    this.parts = new THashMap<String, IFlexibleBakedModel>(TinkerRegistry.getAllMaterials().size());
    this.transforms = transforms;
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

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
  }
}
