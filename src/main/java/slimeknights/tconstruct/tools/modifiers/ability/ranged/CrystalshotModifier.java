package slimeknights.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

import java.util.function.Predicate;

public class CrystalshotModifier extends NoLevelsModifier implements BowAmmoModifierHook {

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.BOW_AMMO);
  }

  @Override
  public int getPriority() {
    // TODO: rethink ordering of ammo modifiers
    return 60; // after trick quiver, before bulk quiver, can't go after bulk due to desire to use inventory
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    // color the display name for the variant
    String variant = tool.getPersistentData().getString(getId());
    if (!variant.isEmpty()) {
      String key = getTranslationKey();
      return Component.translatable(getTranslationKey())
        .withStyle(style -> style.withColor(ResourceColorManager.getTextColor(key + "." + variant)));
    }
    return super.getDisplayName();
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    return CrystalshotItem.withVariant(tool.getPersistentData().getString(getId()), 64);
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    ToolDamageUtil.damageAnimated(tool, 4 * needed, shooter, shooter.getUsedItemHand());
  }
}
