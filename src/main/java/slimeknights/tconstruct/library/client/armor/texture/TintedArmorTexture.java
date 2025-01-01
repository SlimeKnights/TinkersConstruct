package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;

/** Armor texture which tints the texture */
@AllArgsConstructor
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TintedArmorTexture implements ArmorTexture {
  private final ResourceLocation texture;
  @Setter
  @Getter
  private int color = -1;

  @Override
  public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
    VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(texture), false, hasGlint);
    AbstractArmorModel.renderColored(model, matrices, buffer, packedLight, packedOverlay, color, red, green, blue, alpha);
  }
}
