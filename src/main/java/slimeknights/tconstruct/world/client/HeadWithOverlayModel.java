package slimeknights.tconstruct.world.client;

import net.minecraft.client.renderer.entity.model.GenericHeadModel;

/**
 * Extension of generic model with an overlay
 */
public class HeadWithOverlayModel extends GenericHeadModel {
  public HeadWithOverlayModel(int offsetX, int offsetY, int overlayX, int overlayY, int width, int height) {
    super(offsetX, offsetY, width, height);
    this.head.texOffs(overlayX, overlayY);
    this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
    this.head.setPos(0.0F, 0.0F, 0.0F);
  }
}
