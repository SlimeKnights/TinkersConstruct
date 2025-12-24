package slimeknights.tconstruct.library.client.armor.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
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
@Setter
public class TintedArmorTexture implements ArmorTexture {
  private static final int MAX_LIGHT = LightTexture.pack(15, 15);

  private final ResourceLocation texture;
  @Getter
  private int color = -1;
  @Getter
  private int luminosity = 0;

  public TintedArmorTexture(ResourceLocation texture, int color) {
    this(texture, color, 0);
  }

  /** Applies luminosity to the given lightmap color. Assumes that {@code luminosity} is between 1 and 15. */
  public static int applyLuminosity(int packedLight, int luminosity) {
    // if full bright, skip some math
    if (luminosity >= 15) {
      return TintedArmorTexture.MAX_LIGHT;
    }
    // inlined version of methods from LightTexture
    return Math.max(luminosity, (packedLight & 0xFFFF) >> 4) << 4
      | Math.max(luminosity, packedLight >> 20 & 0xFFFF) << 20;
  }

  @Override
  public void renderTexture(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint) {
    VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(texture), false, hasGlint);
    if (luminosity > 0) {
      packedLight = applyLuminosity(packedLight, luminosity);
    }
    AbstractArmorModel.renderColored(model, matrices, buffer, packedLight, packedOverlay, color, red, green, blue, alpha);
  }
}
