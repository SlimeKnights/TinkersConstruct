package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PiglinHeadModel extends GenericHeadModel {
  private final ModelRenderer head;

  public PiglinHeadModel() {
    super(0, 0, 64, 64);
    this.head = new ModelRenderer(this);
    this.head.texOffs( 0, 0).addBox(-5f, -8f, -4f, 10f, 8f, 8f, 0);
    this.head.texOffs(31, 1).addBox(-2f, -4f, -5f,  4f, 4f, 1f, 0);
    this.head.texOffs( 2, 4).addBox( 2f, -2f, -5f,  1f, 2f, 1f, 0);
    this.head.texOffs( 2, 0).addBox(-3f, -2f, -5f,  1f, 2f, 1f, 0);
    // right ear
    ModelRenderer rightEar = new ModelRenderer(this, 51, 6);
    rightEar.setPos(4.5F, -6.0F, 0.0F);
    rightEar.addBox(0f, 0f, -2f, 1f, 5f, 4f, 0f);
    rightEar.zRot = (float)(-Math.PI / 4.5f);
    head.addChild(rightEar);
    // left ear
    ModelRenderer leftEar = new ModelRenderer(this, 39, 6);
    leftEar.setPos(-4.5F, -6.0F, 0.0F);
    leftEar.addBox(-1f, 0f, -2f, 1f, 5f, 4f, 0f);
    leftEar.zRot = (float)(Math.PI / 4.5f);
    head.addChild(leftEar);
  }

  @Override
  public void setupAnim(float animationProgress, float pitch, float yaw) {
    this.head.yRot = pitch * ((float)Math.PI / 180F);
    this.head.xRot = yaw * ((float)Math.PI / 180F);
  }

  @Override
  public void renderToBuffer(MatrixStack matrices, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    this.head.render(matrices, buffer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
