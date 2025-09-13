package slimeknights.tconstruct.world.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

/** Slime that spawns wearing travelers gear or plate gear */
public abstract class TravelersPlateSlimeEntity extends ArmoredSlimeEntity {
  public TravelersPlateSlimeEntity(EntityType<? extends TravelersPlateSlimeEntity> type, Level world) {
    super(type, world);
  }

  /** Gets the primary plating material */
  protected abstract MaterialId getPlating();

  @Override
  protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    // spawn with plate armor with the selected material as the main plating
    // vanilla logic but simplified down to just helmets
    float multiplier = difficulty.getSpecialMultiplier();
    if (this.random.nextFloat() < 0.15f) {
      setMetal(true);
    }
    if (this.random.nextFloat() < 0.15F * multiplier) {
      // start by randomly choosing plate or travelers. Starts at a 35% chance of plate but plate becomes more common with difficulty
      IModifiable helmetItem;
      if (this.random.nextFloat() < 0.35f * multiplier) {
        helmetItem = TinkerTools.plateArmor.get(ArmorItem.Type.HELMET);
      } else {
        helmetItem = TinkerTools.travelersGear.get(ArmorItem.Type.HELMET);
      }
      // next select materials; first is always fixed
      ToolDefinition definition = helmetItem.getToolDefinition();
      ToolStack tool = ToolStack.createTool(
        helmetItem.asItem(), definition,
        RandomMaterial.build(ToolMaterialHook.stats(definition), List.of(RandomMaterial.fixed(getPlating()), RandomMaterial.ancient()), random));
      ModifierNBT modifiers = tool.getUpgrades();
      ToolDataNBT persistentData = tool.getPersistentData();

      // add some random defense modifiers
      int max = tool.getFreeSlots(SlotType.DEFENSE);
      for (int i = 0; i < max; i++) {
        if (this.random.nextFloat() > 0.5f * multiplier) {
          break;
        }
        persistentData.addSlots(SlotType.DEFENSE, -1);
        modifiers = modifiers.withModifier(randomModifier(this.random, TinkerTags.Modifiers.SLIME_DEFENSE), 1);
      }
      // chance of diamond or emerald
      if (tool.getFreeSlots(SlotType.UPGRADE) > 0 && this.random.nextFloat() < 0.5f * multiplier) {
        persistentData.addSlots(SlotType.UPGRADE, -1);
        modifiers = modifiers.withModifier(randomModifier(this.random, TinkerTags.Modifiers.GEMS), 1);
      }

      // triggers stat rebuild
      tool.setUpgrades(modifiers);

      // finally, give the slime the helmet
      this.setItemSlot(EquipmentSlot.HEAD, tool.createStack());
    }
  }

  /** Gets a random defense modifier from the tag */
  private static ModifierId randomModifier(RandomSource random, TagKey<Modifier> tag) {
    List<Modifier> options = ModifierManager.getTagValues(tag);
    return options.get(random.nextInt(options.size())).getId();
  }
}
