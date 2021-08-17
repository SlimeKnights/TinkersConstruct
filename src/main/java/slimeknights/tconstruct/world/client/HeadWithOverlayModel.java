package slimeknights.tconstruct.world.client;

import net.minecraft.client.renderer.entity.model.GenericHeadModel;

/**
 * Extension of generic model with an overlay
 */
public class HeadWithOverlayModel extends GenericHeadModel {
  public HeadWithOverlayModel(int offsetX, int offsetY, int overlayX, int overlayY, int width, int height) {
    super(offsetX, offsetY, width, height);
    this.field_217105_a.setTextureOffset(overlayX, overlayY);
    this.field_217105_a.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
    this.field_217105_a.setRotationPoint(0.0F, 0.0F, 0.0F);
  }
}
