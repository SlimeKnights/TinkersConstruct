package slimeknights.tconstruct.library.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModel;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.TextureType;

/** Armor model that just applies the list of textures */
public class MultilayerArmorModel extends AbstractArmorModel {
  public static final MultilayerArmorModel INSTANCE = new MultilayerArmorModel();

  protected ItemStack armorStack = ItemStack.EMPTY;
  protected ArmorModel model = ArmorModel.EMPTY;
  protected RegistryAccess registryAccess = RegistryAccess.EMPTY;

  protected MultilayerArmorModel() {}

  /** Prepares this model */
  public Model setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base, ArmorModel model) {
    this.model = model;
    this.registryAccess = living.level().registryAccess();
    if (!model.layers().isEmpty()) {
      setup(living, stack, slot, base);
      this.armorStack = stack;
    } else {
      this.armorStack = ItemStack.EMPTY;
    }
    return this;
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (this.base != null && buffer != null) {
      boolean armorGlint = hasGlint;
      boolean wingGlint = hasGlint;
      for (ArmorTextureSupplier textureSupplier : model.layers()) {
        ArmorTexture texture = textureSupplier.getArmorTexture(armorStack, textureType, registryAccess);
        if (texture != ArmorTexture.EMPTY) {
          texture.renderTexture(base, matrices, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha, armorGlint);
          armorGlint = false;
        }
        if (hasWings) {
          texture = textureSupplier.getArmorTexture(armorStack, TextureType.WINGS, registryAccess);
          if (texture != ArmorTexture.EMPTY) {
            renderWings(matrices, packedLightIn, packedOverlayIn, texture, red, green, blue, alpha, wingGlint);
            wingGlint = false;
          }
        }
      }
    }
  }
}
