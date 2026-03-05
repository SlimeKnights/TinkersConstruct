package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/** Variant of {@link slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule} for adding soulspeed with a tooltip. */
public record SoulSpeedModule(LevelingInt level, ModifierCondition<IToolStackView> condition) implements ModifierModule, TooltipModifierHook, EnchantmentModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SoulSpeedModule>defaultHooks(ModifierHooks.ENCHANTMENTS, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<SoulSpeedModule> LOADER = RecordLoadable.create(LevelingInt.LOADABLE.directField(SoulSpeedModule::level), ModifierCondition.TOOL_FIELD, SoulSpeedModule::new);

  @Override
  public RecordLoadable<SoulSpeedModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
    if (enchantment == Enchantments.SOUL_SPEED && condition.matches(tool, modifier)) {
      level += this.level.compute(modifier);
    }
    return level;
  }

  @Override
  public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment, Integer> map) {
    if (condition.matches(tool, modifier)) {
      EnchantmentModifierHook.addEnchantment(map, Enchantments.SOUL_SPEED, this.level.compute(modifier));
    }
  }

  /** Gets the position this entity is standing on, cloned from protected living entity method */
  private static BlockPos getOnPosition(LivingEntity living) {
    Vec3 position = living.position();
    int x = Mth.floor(position.x);
    int y = Mth.floor(position.y - (double)0.2F);
    int z = Mth.floor(position.z);
    BlockPos pos = new BlockPos(x, y, z);
    Level level = living.level();
    if (level.isEmptyBlock(pos)) {
      BlockPos below = pos.below();
      BlockState blockstate = level.getBlockState(below);
      if (blockstate.collisionExtendsVertically(level, below, living)) {
        return below;
      }
    }
    return pos;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // must either have no player or a player on soulsand
    int level = this.level.compute(entry);
    if (level > 0 && condition.matches(tool, entry) && (player == null || key != TooltipKey.SHIFT || (!player.isFallFlying() && player.level().getBlockState(getOnPosition(player)).is(BlockTags.SOUL_SPEED_BLOCKS)))) {
      // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
      // percentages make sense
      Modifier modifier = entry.getModifier();
      TooltipModifierHook.addPercentBoost(modifier, modifier.getDisplayName(), 0.3f + level * 0.105f, tooltip);
    }
  }
}
