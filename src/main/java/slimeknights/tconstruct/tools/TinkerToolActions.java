package slimeknights.tconstruct.tools;

import net.neoforged.neoforge.common.ItemAbility;

/** Custom tool actions defined by the mod */
public class TinkerToolActions {
  /** Tinker tools that can disable shields on attack */
  public static final ItemAbility SHIELD_DISABLE = ItemAbility.get("shield_disable");
  /** Fishing rods that can act as a grappling hook */
  public static final ItemAbility GRAPPLE_HOOK = ItemAbility.get("grapple_hook");
  /** Makes the tool use the drill attack during its dash action */
  public static final ItemAbility DRILL_ATTACK = ItemAbility.get("drill_attack");
  /** Fishing rods that can collect items */
  public static final ItemAbility ITEM_HOOK = ItemAbility.get("item_hook");
}
