package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyArmorStandEntity;
import slimeknights.tconstruct.gadgets.entity.FancyArmorStandEntity.StandType;

import java.util.EnumMap;
import java.util.Map;

/** Renderer for fancy armor stands */
public class FancyArmorStandRenderer extends ArmorStandRenderer {
  private static final Map<StandType, ResourceLocation> TEXTURES = new EnumMap<>(StandType.class);
  static {
    ResourceLocation base = TConstruct.getResource("textures/entity/armorstand/");
    for (StandType type : StandType.values()) {
      TEXTURES.put(type, base.withSuffix(type.name().toLowerCase() + ".png"));
    }
  }

  public FancyArmorStandRenderer(Context context) {
    super(context);
  }

  @Override
  public ResourceLocation getTextureLocation(ArmorStand entity) {
    return TEXTURES.get(((FancyArmorStandEntity)entity).getStandType());
  }

  @Override
  public void render(ArmorStand entity, float yaw, float partialTicks, PoseStack poses, MultiBufferSource buffer, int packedLight) {
    if (((FancyArmorStandEntity)entity).getStandType().isFullbright()) {
      packedLight = 0x00F000F0;
    }
    super.render(entity, yaw, partialTicks, poses, buffer, packedLight);
  }

  @Override
  protected boolean isBodyVisible(ArmorStand stand) {
    return !stand.isInvisible() && !((FancyArmorStandEntity)stand).isClearHidden();
  }
}
