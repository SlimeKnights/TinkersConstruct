package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelPart;

public class PiglinHeadModel extends SkullModel {
  //private final ModelPart head;

  public PiglinHeadModel(ModelPart pRoot) {
    super(pRoot);
    //super(0, 0, 64, 64);
    /*
    this.head = new ModelPart(this);
    this.head.texOffs( 0, 0).addBox(-5f, -8f, -4f, 10f, 8f, 8f, 0);
    this.head.texOffs(31, 1).addBox(-2f, -4f, -5f,  4f, 4f, 1f, 0);
    this.head.texOffs( 2, 4).addBox( 2f, -2f, -5f,  1f, 2f, 1f, 0);
    this.head.texOffs( 2, 0).addBox(-3f, -2f, -5f,  1f, 2f, 1f, 0);
    // right ear
    ModelPart rightEar = new ModelPart(this, 51, 6);
    rightEar.setPos(4.5F, -6.0F, 0.0F);
    rightEar.addBox(0f, 0f, -2f, 1f, 5f, 4f, 0f);
    rightEar.zRot = (float)(-Math.PI / 4.5f);
    head.addChild(rightEar);
    // left ear
    ModelPart leftEar = new ModelPart(this, 39, 6);
    leftEar.setPos(-4.5F, -6.0F, 0.0F);
    leftEar.addBox(-1f, 0f, -2f, 1f, 5f, 4f, 0f);
    leftEar.zRot = (float)(Math.PI / 4.5f);
    head.addChild(leftEar);
     */
  }

  @Override
  public void setupAnim(float animationProgress, float pitch, float yaw) {
    this.head.yRot = pitch * ((float)Math.PI / 180F);
    this.head.xRot = yaw * ((float)Math.PI / 180F);
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    this.head.render(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
