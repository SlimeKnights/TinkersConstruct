package slimeknights.tconstruct.library.tools.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/** Base interface for all tools that can receive modifiers */
public interface IModifiable extends ItemLike {
  /** @deprecated use {@link IndestructibleItemEntity#INDESTRUCTIBLE_ENTITY} */
  @Deprecated(forRemoval = true)
  ResourceLocation INDESTRUCTIBLE_ENTITY = IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY;
  /** Volatile boolean key to make a tool spawn an indestructable entity */
  ResourceLocation SHINY = TConstruct.getResource("shiny");
  /** Volatile int key to increase a tool's range */
  ResourceLocation EXPANDED = TConstruct.getResource("expanded");
  /** @deprecated use {@link RarityModule#RARITY} */
  @Deprecated(forRemoval = true)
  ResourceLocation RARITY = RarityModule.RARITY;
  /** Modifier key to defer tool interaction to the offhand if present */
  ResourceLocation DEFER_OFFHAND = TConstruct.getResource("defer_offhand");
  /** Modifier key to entirely disable tool interaction */
  ResourceLocation NO_INTERACTION = TConstruct.getResource("no_interaction");

  /** Gets the definition of this tool for building and applying modifiers */
  ToolDefinition getToolDefinition();

  /** Gets the tool definition for the given item, or {@link ToolDefinition#EMPTY} if its not modifiable. */
  static ToolDefinition getToolDefinition(Item item) {
    if (item instanceof IModifiable modifiable) {
      return modifiable.getToolDefinition();
    }
    return ToolDefinition.EMPTY;
  }

  /**
   * Sets the rarity of the stack
   * @param volatileData     NBT
   * @param rarity  Rarity, only supports vanilla values
   */
  @Deprecated(forRemoval = true)
  static void setRarity(ModDataNBT volatileData, Rarity rarity) {
    RarityModule.setRarity(volatileData, rarity);
  }
}
