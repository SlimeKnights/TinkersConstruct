package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.tools.client.SlimelytraArmorModel;

import javax.annotation.Nullable;

public class SlimelytraItem extends SlimesuitItem {
  public SlimelytraItem(ModifiableArmorMaterial material, Properties properties) {
    super(material, ArmorSlotType.CHESTPLATE, properties);
  }

  @Nullable
  @Override
  @OnlyIn(Dist.CLIENT)
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A base) {
    return SlimelytraArmorModel.getModel(entityLiving, itemStack, base);
  }
}
