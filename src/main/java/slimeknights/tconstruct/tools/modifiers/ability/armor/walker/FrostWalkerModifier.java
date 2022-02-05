package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FrostWalkerModifier extends AbstractWalkerModifier implements IArmorWalkModifier {
  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 3 + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  public boolean isSourceBlocked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return source == DamageSource.HOT_FLOOR;
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    if (world.isEmptyBlock(target)) {
      BlockState frostedIce = Blocks.FROSTED_ICE.defaultBlockState();
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      BlockState below = world.getBlockState(mutable);
      boolean isFull = below.getBlock() == Blocks.WATER && below.getValue(LiquidBlock.LEVEL) == 0; //TODO: Forge, modded waters?
      if (below.getMaterial() == Material.WATER && isFull
          && frostedIce.canSurvive(world, mutable) && world.isUnobstructed(frostedIce, mutable, CollisionContext.empty())
          && !ForgeEventFactory.onBlockPlace(living, BlockSnapshot.create(world.dimension(), world, mutable), Direction.UP)) {
        world.setBlockAndUpdate(mutable, frostedIce);
        world.scheduleTick(mutable, Blocks.FROSTED_ICE, Mth.nextInt(living.getRandom(), 60, 120));
      }
    }
  }
}
