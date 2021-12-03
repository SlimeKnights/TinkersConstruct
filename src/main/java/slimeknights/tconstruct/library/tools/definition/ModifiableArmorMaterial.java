package slimeknights.tconstruct.library.tools.definition;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import javax.annotation.Nullable;

/** Armor material that doubles as a container for tool definitions for each armor slot */
public class ModifiableArmorMaterial implements IArmorMaterial {
  /** Array of all four armor slot types */
  public static final EquipmentSlotType[] ARMOR_SLOTS = {EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD};

  /** Namespaced name of the armor */
  private final ResourceLocation name;
  /** Array of slot index to tool definition for the slot */
  private final ToolDefinition[] armorDefinitions;
  /** Sound to play when equipping the armor */
  @Getter
  private final SoundEvent soundEvent;

  public ModifiableArmorMaterial(ResourceLocation name, SoundEvent soundEvent, ToolDefinition... armorDefinitions) {
    this.name = name;
    this.soundEvent = soundEvent;
    if (armorDefinitions.length != 4) {
      throw new IllegalArgumentException("Must have an armor definition for each slot");
    }
    this.armorDefinitions = armorDefinitions;
  }

  /**
   * Gets the armor definition for the given armor slot, used in item construction
   * @param slotType  Slot type
   * @return  Armor definition
   */
  @Nullable
  public ToolDefinition getArmorDefinition(ArmorSlotType slotType) {
    return armorDefinitions[slotType.getIndex()];
  }

  /** Gets the value of a stat for the given slot */
  private float getStat(FloatToolStat toolStat, @Nullable ArmorSlotType slotType) {
    ToolDefinition toolDefinition = slotType == null ? null : getArmorDefinition(slotType);
    float defaultValue = toolStat.getDefaultValue();
    if (toolDefinition == null) {
      return defaultValue;
    }
    ToolDefinitionData data = toolDefinition.getData();
    return data.getBaseStat(toolStat) * data.getMultiplier(toolStat);
  }

  @Override
  public String getName() {
    return name.toString();
  }

  @Override
  public int getDurability(EquipmentSlotType slotIn) {
    return (int)getStat(ToolStats.DURABILITY, ArmorSlotType.fromEquipment(slotIn));
  }

  @Override
  public int getDamageReductionAmount(EquipmentSlotType slotIn) {
    return (int)getStat(ToolStats.ARMOR, ArmorSlotType.fromEquipment(slotIn));
  }

  @Override
  public float getToughness() {
    return getStat(ToolStats.ARMOR_TOUGHNESS, ArmorSlotType.CHESTPLATE);
  }

  @Override
  public float getKnockbackResistance() {
    return getStat(ToolStats.KNOCKBACK_RESISTANCE, ArmorSlotType.CHESTPLATE);
  }

  @Override
  public int getEnchantability() {
    return 0;
  }

  @Override
  public Ingredient getRepairMaterial() {
    return Ingredient.EMPTY;
  }


  /** Gets a builder for a modifiable armor material, creates tool definition for the selected slots */
  public static Builder builder(ResourceLocation name, ArmorSlotType... slotTypes) {
    return new Builder(name, slotTypes);
  }

  /** Gets a builder for a modifiable armor material, creates tool definition for all four armor slots */
  public static Builder builder(ResourceLocation name) {
    return builder(name, ArmorSlotType.values());
  }

  /**
   * Builds tool definitions that behave similar to vanilla armor
   */
  public static class Builder {
    private final ResourceLocation name;
    private final ToolDefinition.Builder[] builders;
    private final ArmorSlotType[] slotTypes;
    @Setter @Accessors(chain = true)
    private SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    protected Builder(ResourceLocation baseName, ArmorSlotType[] slotTypes) {
      this.name = baseName;
      builders = new ToolDefinition.Builder[4];
      this.slotTypes = slotTypes;
      for (ArmorSlotType slot : slotTypes) {
        builders[slot.getIndex()] = ToolDefinition.builder(new ResourceLocation(baseName.getNamespace(), baseName.getPath() + "_" + slot.getString()));
      }
    }

    /** Gets the builder for the given slot */
    private ToolDefinition.Builder getBuilder(ArmorSlotType slotType) {
      ToolDefinition.Builder builder = builders[slotType.getIndex()];
      if (builder == null) {
        throw new IllegalArgumentException("Unsupported slot type " + slotType + " for material " + name);
      }
      return builder;
    }

    /** Sets the stat provider for the given slot */
    public Builder setStatsProvider(ArmorSlotType slot, IToolStatProvider statProvider) {
      getBuilder(slot).setStatsProvider(statProvider);
      return this;
    }

    /** Sets the stat provider for all slots slot */
    public Builder setStatsProvider(IToolStatProvider statProvider) {
      for (ArmorSlotType slotType : slotTypes) {
        setStatsProvider(slotType, statProvider);
      }
      return this;
    }

    /** Tells the definition to not be registered with the loader, used internally for testing. In general mods wont need this */
    public Builder skipRegister() {
      for (ArmorSlotType slotType : slotTypes) {
        getBuilder(slotType).skipRegister();
      }
      return this;
    }

    /** Builds the final material */
    public ModifiableArmorMaterial build() {
      ToolDefinition[] toolDefinitions = new ToolDefinition[4];
      for (ArmorSlotType slotType : slotTypes) {
        toolDefinitions[slotType.getIndex()] = builders[slotType.getIndex()].build();
      }
      return new ModifiableArmorMaterial(name, soundEvent, toolDefinitions);
    }
  }
}
