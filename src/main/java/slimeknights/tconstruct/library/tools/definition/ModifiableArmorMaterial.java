package slimeknights.tconstruct.library.tools.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.library.tools.item.armor.DummyArmorMaterial;

import javax.annotation.Nullable;

/** Armor material that doubles as a container for tool definitions for each armor slot */
public class ModifiableArmorMaterial extends DummyArmorMaterial {
  /** Array of all four player armor item types. */
  public static final ArmorItem.Type[] ARMOR_TYPES = {ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS};
  /** Array of all four armor slot types */
  public static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

  /** Array of slot index to tool definition for the slot */
  private final ToolDefinition[] armorDefinitions;

  private ModifiableArmorMaterial(ResourceLocation id, SoundEvent equipSound, ToolDefinition... armorDefinitions) {
    super(id, equipSound);
    if (armorDefinitions.length != 4) {
      throw new IllegalArgumentException("Must have an armor definition for each slot");
    }
    this.armorDefinitions = armorDefinitions;
  }

  /** Creates a modifiable armor material, creates tool definition for the selected slots */
  public static ModifiableArmorMaterial create(ResourceLocation id, SoundEvent equipSound, ArmorItem.Type... slots) {
    ToolDefinition[] definitions = new ToolDefinition[4];
    for (ArmorItem.Type slot : slots) {
      if (!slot.hasTrims()) {
        throw new IllegalArgumentException("Unsupported armor slot " + slot.getName());
      }
      definitions[slot.ordinal()] = ToolDefinition.create(id.withSuffix("_" + slot.getName()));
    }
    return new ModifiableArmorMaterial(id, equipSound, definitions);
  }

  /** Creates a modifiable armor material, creates tool definition for all four armor slots */
  public static ModifiableArmorMaterial create(ResourceLocation id, SoundEvent equipSound) {
    return create(id, equipSound, ARMOR_TYPES);
  }

  /**
   * Gets the armor definition for the given armor slot, used in item construction
   * @param slotType  Slot type
   * @return  Armor definition
   */
  @Nullable
  public ToolDefinition getArmorDefinition(ArmorItem.Type slotType) {
    if (!slotType.hasTrims()) {
      return null;
    }
    return armorDefinitions[slotType.ordinal()];
  }
}
