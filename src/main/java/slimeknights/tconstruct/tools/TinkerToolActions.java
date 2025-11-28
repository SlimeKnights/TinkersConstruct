package slimeknights.tconstruct.tools;

import net.minecraftforge.common.ToolAction;

/** Custom tool actions defined by the mod */
public class TinkerToolActions {
  /** Tinker tools that can disable shields on attack */
  public static final ToolAction SHIELD_DISABLE = ToolAction.get("shield_disable");
  /** Fishing rods that can act as a grappling hook */
  public static final ToolAction GRAPPLE_HOOK = ToolAction.get("grapple_hook");
  /** Makes the tool use the drill attack during its dash action */
  public static final ToolAction DRILL_ATTACK = ToolAction.get("drill_attack");
  /** Fishing rods that can collect items */
  public static final ToolAction ITEM_HOOK = ToolAction.get("item_hook");
}
