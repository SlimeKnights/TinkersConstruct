package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FrostWalkerModifier extends AbstractWalkerModifier implements IArmorWalkModifier {
  public FrostWalkerModifier() {
    super(0x92B9FE);
  }

  @Override
  protected float getRadius(IModifierToolStack tool, int level) {
    return 3 + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  public boolean isSourceBlocked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount) {
    return source == DamageSource.HOT_FLOOR;
  }

  @Override
  protected void walkOn(IModifierToolStack tool, int level, LivingEntity living, World world, BlockPos target, Mutable mutable) {
    if (world.isAirBlock(target)) {
      BlockState frostedIce = Blocks.FROSTED_ICE.getDefaultState();
      mutable.setPos(target.getX(), target.getY() - 1, target.getZ());
      BlockState below = world.getBlockState(mutable);
      boolean isFull = below.getBlock() == Blocks.WATER && below.get(FlowingFluidBlock.LEVEL) == 0; //TODO: Forge, modded waters?
      if (below.getMaterial() == Material.WATER && isFull
          && frostedIce.isValidPosition(world, mutable) && world.placedBlockCollides(frostedIce, mutable, ISelectionContext.dummy())
          && !ForgeEventFactory.onBlockPlace(living, BlockSnapshot.create(world.getDimensionKey(), world, mutable), Direction.UP)) {
        world.setBlockState(mutable, frostedIce);
        world.getPendingBlockTicks().scheduleTick(mutable, Blocks.FROSTED_ICE, MathHelper.nextInt(living.getRNG(), 60, 120));
      }
    }
  }
}
