package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.client.ElytraArmorModel;

import javax.annotation.Nullable;

public class SlimelytraItem extends ModifiableArmorItem {
  private final ResourceLocation materialName;
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
    materialName = material.getNameLocation();
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, @Nullable String type) {
    return String.format("%s:textures/models/armor/%s_wings%s.png", materialName.getNamespace(), materialName.getPath(), type == null ? "" : "_" + type);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
    ElytraArmorModel.INSTANCE.setRotationAngles(entityLiving, 0, 0, 0, 0, 0);
    return (A)ElytraArmorModel.INSTANCE;
  }
}
