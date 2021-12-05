package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.client.SlimelytraArmorModel;

import javax.annotation.Nullable;

public class SlimelytraItem extends ModifiableArmorItem {
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A base) {
    SlimelytraArmorModel.INSTANCE.setEntityAndBase(entityLiving, base);
    return (A)SlimelytraArmorModel.INSTANCE;
  }
}
