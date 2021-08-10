package slimeknights.tconstruct.library.tools;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

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
  public ToolDefinition getArmorDefinition(EquipmentSlotType slotType) {
    return armorDefinitions[slotType.getIndex()];
  }

  /** Gets the value of a stat for the given slot */
  private float getStat(FloatToolStat toolStat, EquipmentSlotType slotType) {
    ToolDefinition toolDefinition = getArmorDefinition(slotType);
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
    return (int)getStat(ToolStats.DURABILITY, slotIn);
  }

  @Override
  public int getDamageReductionAmount(EquipmentSlotType slotIn) {
    return (int)getStat(ToolStats.ARMOR, slotIn);
  }

  @Override
  public float getToughness() {
    return getStat(ToolStats.ARMOR_TOUGHNESS, EquipmentSlotType.CHEST);
  }

  @Override
  public float getKnockbackResistance() {
    return getStat(ToolStats.KNOCKBACK_RESISTANCE, EquipmentSlotType.CHEST);
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
  public static Builder builder(ResourceLocation name, EquipmentSlotType... slotTypes) {
    return new Builder(name, slotTypes);
  }

  /** Gets a builder for a modifiable armor material, creates tool definition for all four armor slots */
  public static Builder builder(ResourceLocation name) {
    return builder(name, ARMOR_SLOTS);
  }

  /**
   * Builds tool definitions that behave similar to vanilla armor
   */
  public static class Builder {
    private final ResourceLocation name;
    private final ToolDefinition.Builder[] builders;
    private final EquipmentSlotType[] slotTypes;
    @Setter @Accessors(chain = true)
    private SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    protected Builder(ResourceLocation baseName, EquipmentSlotType[] slotTypes) {
      this.name = baseName;
      builders = new ToolDefinition.Builder[4];
      this.slotTypes = slotTypes;
      for (EquipmentSlotType slot : slotTypes) {
        if (slot.getSlotType() != Group.ARMOR) {
          throw new IllegalArgumentException("Invalid armor slot " + slot);
        }
        builders[slot.getIndex()] = ToolDefinition.builder(new ResourceLocation(baseName.getNamespace(), baseName.getPath() + "_" + slot.getName()));
      }
    }

    /** Gets the builder for the given slot */
    private ToolDefinition.Builder getBuilder(EquipmentSlotType slotType) {
      if (slotType.getSlotType() != Group.ARMOR) {
        throw new IllegalArgumentException("Invalid armor slot " + slotType);
      }
      ToolDefinition.Builder builder = builders[slotType.getIndex()];
      if (builder == null) {
        throw new IllegalArgumentException("Unsupported slot type " + slotType + " for material " + name);
      }
      return builder;
    }

    /** Sets the stat provider for the given slot */
    public Builder setStatsProvider(EquipmentSlotType slot, IToolStatProvider statProvider) {
      getBuilder(slot).setStatsProvider(statProvider);
      return this;
    }

    /** Sets the stat provider for all slots slot */
    public Builder setStatsProvider(IToolStatProvider statProvider) {
      for (EquipmentSlotType slotType : slotTypes) {
        setStatsProvider(slotType, statProvider);
      }
      return this;
    }

    /** Tells the definition to not be registered with the loader, used internally for testing. In general mods wont need this */
    public Builder skipRegister() {
      for (EquipmentSlotType slotType : slotTypes) {
        getBuilder(slotType).skipRegister();
      }
      return this;
    }

    /** Builds the final material */
    public ModifiableArmorMaterial build() {
      ToolDefinition[] toolDefinitions = new ToolDefinition[4];
      for (EquipmentSlotType slotType : slotTypes) {
        toolDefinitions[slotType.getIndex()] = builders[slotType.getIndex()].build();
      }
      return new ModifiableArmorMaterial(name, soundEvent, toolDefinitions);
    }
  }
}
