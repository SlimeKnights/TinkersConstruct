package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;

public class SlimyMudBlock extends Block {

  private final MudType mudType;

  public SlimyMudBlock(MudType mudType) {
    super(Block.Properties.create(Material.SAND).hardnessAndResistance(3.0f).slipperiness(0.8F).sound(SoundType.SAND));
    this.mudType = mudType;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    boolean shouldAdd = true;

    switch (this.mudType) {
      case SLIMY_MUD_BLUE:
        shouldAdd = TinkerCommons.blue_slime_ball != null;
        break;
      case SLIMY_MUD_MAGMA:
        shouldAdd = TinkerCommons.magma_slime_ball != null;
        break;
      case SLIMY_MUD_GREEN:
        shouldAdd = true;
        break;
    }

    if (shouldAdd) {
      super.fillItemGroup(group, items);
    }
  }

  @Override
  public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    switch (this.mudType) {
      case SLIMY_MUD_GREEN:
      case SLIMY_MUD_BLUE:
        this.processSlimyMud(entityIn);
        break;
    }
  }

  // slow down
  private void processSlimyMud(Entity entity) {
    entity.getMotion().mul(0.4D, 1.0D, 0.4D);
    if (entity instanceof LivingEntity) {
      ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.WEAKNESS, 1));
    }
  }

  @Override
  public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
    if (this.mudType == MudType.SLIMY_MUD_GREEN || this.mudType == MudType.SLIMY_MUD_BLUE) {
      return plantable.getPlantType(world, pos) == TinkerWorld.slimePlantType;
    }

    return super.canSustainPlant(state, world, pos, facing, plantable);
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.SHOVEL;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }

  public enum MudType {
    SLIMY_MUD_GREEN,
    SLIMY_MUD_BLUE,
    SLIMY_MUD_MAGMA
  }

}
