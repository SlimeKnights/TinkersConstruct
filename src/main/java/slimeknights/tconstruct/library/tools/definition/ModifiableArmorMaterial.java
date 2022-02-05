package slimeknights.tconstruct.library.tools.definition;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Armor material that doubles as a container for tool definitions for each armor slot */
public class ModifiableArmorMaterial implements ArmorMaterial {
  /** Array of all four armor slot types */
  public static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

  /** Namespaced name of the armor */
  private final ResourceLocation name;
  /** Array of slot index to tool definition for the slot */
  private final ToolDefinition[] armorDefinitions;
  /** Sound to play when equipping the armor */
  @Getter
  private final SoundEvent equipSound;

  public ModifiableArmorMaterial(ResourceLocation name, SoundEvent equipSound, ToolDefinition... armorDefinitions) {
    this.name = name;
    this.equipSound = equipSound;
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

  /** Gets the name as a resource location */
  public ResourceLocation getNameLocation() {
    return name;
  }

  @Override
  public int getDurabilityForSlot(EquipmentSlot slotIn) {
    return (int)getStat(ToolStats.DURABILITY, ArmorSlotType.fromEquipment(slotIn));
  }

  @Override
  public int getDefenseForSlot(EquipmentSlot slotIn) {
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
  public int getEnchantmentValue() {
    return 0;
  }

  @Override
  public Ingredient getRepairIngredient() {
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
  @SuppressWarnings("unused")
  public static class Builder {
    private final ResourceLocation name;
    private final ToolDefinition.Builder[] builders;
    private final ArmorSlotType[] slotTypes;
    @Setter @Accessors(chain = true)
    private SoundEvent soundEvent = SoundEvents.ARMOR_EQUIP_LEATHER;
    protected Builder(ResourceLocation baseName, ArmorSlotType[] slotTypes) {
      this.name = baseName;
      builders = new ToolDefinition.Builder[4];
      this.slotTypes = slotTypes;
      for (ArmorSlotType slot : slotTypes) {
        builders[slot.getIndex()] = ToolDefinition.builder(new ResourceLocation(baseName.getNamespace(), baseName.getPath() + "_" + slot.getSerializedName()));
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

    /** Generic method to set any property on a tool definition builder */
    public Builder set(ArmorSlotType slot, Consumer<ToolDefinition.Builder> builderConsumer) {
      builderConsumer.accept(getBuilder(slot));
      return this;
    }

    /** Generic method to set any property on a tool definition builder */
    public Builder set(Consumer<ToolDefinition.Builder> builderConsumer) {
      for (ArmorSlotType slotType : slotTypes) {
        set(slotType, builderConsumer);
      }
      return this;
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
