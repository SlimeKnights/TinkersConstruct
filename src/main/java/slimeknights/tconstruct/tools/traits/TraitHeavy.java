package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.UUID;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitHeavy extends AbstractTrait {

  protected static final UUID KNOCKBACK_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

  public TraitHeavy() {
    super("heavy", 0xffffff);
  }

  @Override
  public void getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
    if(slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) {
      attributeMap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(KNOCKBACK_MODIFIER, "Knockback modifier", 1, 0));
    }
  }
}
