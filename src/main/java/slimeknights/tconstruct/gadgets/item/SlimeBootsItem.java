package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import slimeknights.mantle.item.ArmorTooltipItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;

public class SlimeBootsItem extends ArmorTooltipItem {

  public SlimeBootsItem(SlimeBlock.SlimeType slimeType, Properties props) {
    super(new SlimeArmorMaterial(slimeType.getName() + "_slime"), EquipmentSlotType.FEET, props);
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
    return HashMultimap.<String, AttributeModifier>create();
  }

  public static class SlimeArmorMaterial implements IArmorMaterial {
    private final Ingredient empty_repair_material = Ingredient.fromItems(Items.AIR);
    private final String name;

    public SlimeArmorMaterial(String slimeName) {
      name = slimeName;
    }

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
      return 0;
    }

    @Override
    public int getEnchantability() {
      return 0;
    }

    @Override
    public SoundEvent getSoundEvent() {
      return SoundEvents.BLOCK_SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairMaterial() {
      return this.empty_repair_material;
    }

    @Override
    public String getName() {
      return Util.resource(name);
    }

    @Override
    public float getToughness() {
      return 0;
    }
  }
}
