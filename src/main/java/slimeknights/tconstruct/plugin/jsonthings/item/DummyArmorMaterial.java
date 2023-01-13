package slimeknights.tconstruct.plugin.jsonthings.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** Armor material that returns 0 except for name, since we bypass all the usages */
@RequiredArgsConstructor
public class DummyArmorMaterial implements ArmorMaterial {
  private final ResourceLocation name;
  @Getter
  private final SoundEvent equipSound;

  @Override
  public String getName() {
    return name.toString();
  }


  /* Required dummy methods */

  @Override
  public int getDurabilityForSlot(EquipmentSlot pSlot) {
    return 0;
  }

  @Override
  public int getDefenseForSlot(EquipmentSlot pSlot) {
    return 0;
  }

  @Override
  public int getEnchantmentValue() {
    return 0;
  }

  @Override
  public Ingredient getRepairIngredient() {
    return Ingredient.EMPTY;
  }

  @Override
  public float getToughness() {
    return 0;
  }

  @Override
  public float getKnockbackResistance() {
    return 0;
  }
}
