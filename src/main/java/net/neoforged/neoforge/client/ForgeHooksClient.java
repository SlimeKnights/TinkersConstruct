package slimeknights.tconstruct.compat.neoforged.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;

/** Compatibility facade for old Forge client hook names. */
public final class ForgeHooksClient {
  private ForgeHooksClient() {}

  public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform) {
    return ClientHooks.handleCameraTransforms(poseStack, model, cameraTransformType, applyLeftHandTransform);
  }

  public static Model getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
    return ClientHooks.getArmorModel(entity, stack, slot, original);
  }

  public static String getArmorTexture(Entity entity, ItemStack stack, String defaultTexture, EquipmentSlot slot, String type) {
    return defaultTexture;
  }
}
