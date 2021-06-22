package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.internal.HarvestAbilityModifier.IHarvestModifier;

import javax.annotation.Nullable;

public class MagneticModifier extends Modifier implements IHarvestModifier {
  public MagneticModifier() {
    super(0x720000);
  }

  @Override
  public void afterBlockBreak(IModifierToolStack tool, int level, World world, BlockState state, BlockPos pos, LivingEntity living, boolean canHarvest, boolean wasEffective, boolean isAOEBlock) {
    if (!isAOEBlock) {
      TinkerModifiers.magneticEffect.get().apply(living, 30, level - 1);
    }
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, Hand hand, LivingEntity target, float damageDealt, boolean isCritical, float cooldown, boolean isExtraAttack) {
    if (!isExtraAttack) {
      TinkerModifiers.magneticEffect.get().apply(attacker, 30, level - 1);
    }
    return 0;
  }

  @Override
  public void afterHarvest(IModifierToolStack tool, int level, ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos) {
    PlayerEntity player = context.getPlayer();
    if (player != null) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, level - 1);
    }
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IHarvestModifier.class) {
      return (T) this;
    }
    return null;
  }
}
