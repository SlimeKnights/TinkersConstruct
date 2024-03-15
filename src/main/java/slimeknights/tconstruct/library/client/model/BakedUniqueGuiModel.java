package slimeknights.tconstruct.library.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

/**
 * Wrapper that swaps the model for the GUI
 */
public class BakedUniqueGuiModel extends BakedModelWrapper<BakedModel> {

  private final BakedModel gui;

  public BakedUniqueGuiModel(BakedModel base, BakedModel gui) {
    super(base);
    this.gui = gui;
  }

  @Override
  public BakedModel applyTransform(TransformType cameraTransformType, PoseStack mat, boolean applyLeftHandTransform) {
    if (cameraTransformType == TransformType.GUI) {
      return gui.applyTransform(cameraTransformType, mat, applyLeftHandTransform);
    }
    return originalModel.applyTransform(cameraTransformType, mat, applyLeftHandTransform);
  }
}
