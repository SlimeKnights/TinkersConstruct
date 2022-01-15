package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;

import javax.annotation.Nullable;

/** This item is mainly to return the proper model for a slimeskull */
public class SlimeskullItem extends SlimesuitItem {
  public SlimeskullItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.HELMET, properties);
  }

  @Nullable
  @Override
  @OnlyIn(Dist.CLIENT)
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A base) {
    return SlimeskullArmorModel.getModel(stack, base);
  }
}
