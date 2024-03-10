package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/** Modifier hooks for wearing leggings boosting loot. One as a module in case you want to only apply the bonus to leggings/not leggings */
public interface LootingModifierHook {
  /** Default behavior for the looting modifier */
  LootingModifierHook DEFAULT = (tool, entry, holder, target, damageSource, looting) -> looting;

  /** Constructor for a merger that sums all children */
  Function<Collection<LootingModifierHook>, LootingModifierHook> SUM_MERGER = SumMerger::new;


  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param modifier      Modifier entry calling the hook
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting       Looting value set from previous modifiers
   * @return New looting value
   */
  int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting);


  /* Helpers */

  /**
   * Gets the looting value for the given tool
   * @param tool           Tool used
   * @param holder         Entity holding the tool
   * @param target         Target being looted
   * @param damageSource   Damage source for looting, may ben null if no attack
   * @return  Looting value for the tool
   */
  static int getToolLooting(IToolStackView tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource) {
    if (tool.isBroken()) {
      return 0;
    }
    return getLootingValue(TinkerHooks.TOOL_LOOTING, tool, holder, target, damageSource, 0);
  }

  /**
   * Gets the looting value for the leggings
   * @param holder         Entity holding the tool
   * @param target         Target being looted
   * @param damageSource   Damage source for looting, may ben null if no attack
   * @param toolLooting    Looting from the tool
   * @return  Looting value for the tool
   */
  static int getLeggingsLooting(LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int toolLooting) {
    ItemStack pants = holder.getItemBySlot(EquipmentSlot.LEGS);
    if (!pants.isEmpty() && pants.is(TinkerTags.Items.LEGGINGS)) {
      ToolStack pantsTool = ToolStack.from(pants);
      if (!pantsTool.isBroken()) {
        toolLooting = getLootingValue(TinkerHooks.LEGGINGS_LOOTING, pantsTool, holder, target, damageSource, toolLooting);
      }
    }
    return toolLooting;
  }

  /**
   * Gets the looting value from the given tool
   * @param hook           Hook to call
   * @param tool           Tool instance
   * @param holder         Entity holding the tool
   * @param target         Target of the attack
   * @param damageSource   Damage used to attack
   * @param looting        Previous looting value
   * @return  New looting value
   */
  static int getLootingValue(ModifierHook<LootingModifierHook> hook, IToolStackView tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    for (ModifierEntry entry : tool.getModifierList()) {
      looting = entry.getHook(hook).getLootingValue(tool, entry, holder, target, damageSource, looting);
    }
    return looting;
  }


  /** Constructor for a merger that sums all children */
  record SumMerger(Collection<LootingModifierHook> modules) implements LootingModifierHook {
    @Override
    public int getLootingValue(IToolStackView tool, ModifierEntry entry, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
      for (LootingModifierHook module : modules) {
        looting = module.getLootingValue(tool, entry, holder, target, damageSource, looting);
      }
      return looting;
    }
  }
}
