package slimeknights.tconstruct.world.client;

import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

/**
 * Extension of generic model with an overlay
 */
public class HeadWithOverlayModel extends SkullModel {
  public HeadWithOverlayModel(ModelPart pRoot, int offsetX, int offsetY, int overlayX, int overlayY, int width, int height) {
    super(pRoot);
    //super(offsetX, offsetY, width, height);
    /*
    this.head.texOffs(overlayX, overlayY);
    this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
    this.head.setPos(0.0F, 0.0F, 0.0F);
     */
  }
}
