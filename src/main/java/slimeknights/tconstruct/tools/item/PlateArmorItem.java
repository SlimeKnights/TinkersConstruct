package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.client.PlateArmorModel;

import javax.annotation.Nullable;

public class PlateArmorItem extends ModifiableArmorItem {
  public PlateArmorItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
  }

  @Nullable
  @Override
  @OnlyIn(Dist.CLIENT)
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A base) {
    return PlateArmorModel.getModel(stack, armorSlot, base);
  }
}
