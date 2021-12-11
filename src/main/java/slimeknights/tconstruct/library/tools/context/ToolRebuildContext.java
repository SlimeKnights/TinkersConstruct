package slimeknights.tconstruct.library.tools.context;

import lombok.Data;
import net.minecraft.item.Item;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

/** A more limited view of {@link slimeknights.tconstruct.library.tools.nbt.IModifierToolStack} for use in tool rebuild hooks */
@Data
public class ToolRebuildContext implements IToolContext {
  /** Item being rebuilt */
  private final Item item;
  /** Tool definition of the item being rebuilt */
  private final ToolDefinition definition;
  /** Materials on the tool being rebuilt */
  private final MaterialNBT materials;
  /** List of recipe modifiers on the tool being rebuilt */
  private final ModifierNBT upgrades;
  /** List of all modifiers on the tool being rebuilt, from recipes and traits */
  private final ModifierNBT modifiers;
  /** Tool stats before modifiers add stats */
  private final StatsNBT stats;
  /** Persistent modifier data, intentionally read only */
  private final IModDataReadOnly persistentData;
  /** Volatile modifier data */
  private final IModDataReadOnly volatileData;
}
