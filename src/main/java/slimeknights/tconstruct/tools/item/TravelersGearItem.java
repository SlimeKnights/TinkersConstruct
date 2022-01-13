package slimeknights.tconstruct.tools.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.client.TravelersGearModel;

import javax.annotation.Nullable;

public class TravelersGearItem extends ModifiableArmorItem {
  /** Golden texture for armor */
  private static final String GOLDEN_ARMOR = TConstruct.resourceString("textures/models/armor/travelers_golden_1.png");
  /** Golden texture for leggings */
  private static final String GOLDEN_LEGS = TConstruct.resourceString("textures/models/armor/travelers_golden_2.png");

  public TravelersGearItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.get()) > 0) {
      return slot == EquipmentSlotType.LEGS ? GOLDEN_LEGS : GOLDEN_ARMOR;
    }
    return null;
  }

  @Nullable
  @Override
  @OnlyIn(Dist.CLIENT)
  public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A base) {
    return TravelersGearModel.getModel(stack, armorSlot, base);
  }
}
