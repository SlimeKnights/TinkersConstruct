package slimeknights.tconstruct.smeltery.client.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Set;

/**
 * Render type buffer builder to render cooling items transparent and tinted them based on cooling time
 */
public class CastingItemRenderTypeBuffer implements MultiBufferSource {
  private static final Set<String> MAKE_TRANSPARENT = ImmutableSet.of("entity_solid", "entity_cutout", "entity_cutout_no_cull", "entity_translucent", "entity_no_outline");

  /** Base render type buffer */
  private final MultiBufferSource inner;
  /** Calculated colors to pass into {@link TintedVertexBuilder} */
  private final int alpha, red, green, blue;

  /**
   * Creates a new instance of this class
   * @param inner        Base render type buffer
   * @param alpha        Opacity of the item from 0 to 255. 255 is the end of the animation.
   * @param temperature  Temperature of the item from 0 to 255. 0 is the end of the animation when the item is "cool"/untinted
   */
  public CastingItemRenderTypeBuffer(MultiBufferSource inner, int alpha, int temperature) {
    this.inner = inner;
    // alpha is a direct fade from 0 to 255
    this.alpha = Mth.clamp(alpha, 0, 0xFF);
    // RGB based on temperature, fades from 0xB06020 tint to 0xFFFFFF
    temperature = Mth.clamp(temperature, 0, 0xFF);
    this.red   = 0xFF - (temperature * (0xFF - 0xB0) / 0xFF);
    this.green = 0xFF - (temperature * (0xFF - 0x60) / 0xFF);
    this.blue  = 0xFF - (temperature * (0xFF - 0x20) / 0xFF);
  }

  @Override
  public VertexConsumer getBuffer(RenderType type) {
    if (alpha < 255 && MAKE_TRANSPARENT.contains(type.name) && type instanceof RenderType.CompositeRenderType composite && composite.state.textureState instanceof TextureStateShard textureState) {
      ResourceLocation texture = textureState.texture.orElse(InventoryMenu.BLOCK_ATLAS);
      type = RenderType.entityTranslucentCull(texture);
    }

    return new TintedVertexBuilder(inner.getBuffer(type), red, green, blue, alpha);
  }
}
