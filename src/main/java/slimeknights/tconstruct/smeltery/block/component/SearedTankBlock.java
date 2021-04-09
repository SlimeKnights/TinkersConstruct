package slimeknights.tconstruct.smeltery.block.component;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Locale;

public class SearedTankBlock extends SearedBlock implements BlockEntityProvider {
  private final FluidAmount capacity;

  public SearedTankBlock(Settings properties, FluidAmount capacity) {
    super(properties);
    this.capacity = capacity;
  }

  @Deprecated
  @Override
  @Environment(EnvType.CLIENT)
  public float getAmbientOcclusionLightLevel(BlockState state, BlockView worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public BlockEntity createBlockEntity(BlockView worldIn) {
    return new TankTileEntity(this);
  }

  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (ITankTileEntity.interactWithTank(world, pos, player, hand, hit)) {
      return ActionResult.SUCCESS;
    }

    return super.onUse(state, world, pos, player, hand, hit);
  }
//TODO Figure out fabric analog.
//  @Override
//  public int getLightValue(BlockState state, BlockView world, BlockPos pos) {
//    BlockEntity te = world.getBlockEntity(pos);
//    if (te instanceof TankTileEntity) {
//      FluidVolume fluid = ((TankTileEntity) te).getTank().getFluid();
//      return fluid.getFluidKey().luminosity;
//    }
//    return super.getLightValue(state, world, pos);
//  }

  @Override
  public void onPlaced(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      TileEntityHelper.getTile(TankTileEntity.class, worldIn, pos).ifPresent(te -> te.updateTank(nbt.getCompound(Tags.TANK)));
    }
    super.onPlaced(worldIn, pos, state, placer, stack);
  }

  @Deprecated
  @Override
  public boolean hasComparatorOutput(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }

  public FluidAmount getCapacity() {
    return capacity;
  }

  public enum TankType implements StringIdentifiable {
    TANK(TankTileEntity.DEFAULT_CAPACITY),
    GAUGE(MaterialValues.METAL_BLOCK.mul(3)),
    WINDOW(TankTileEntity.DEFAULT_CAPACITY);

    private final FluidAmount capacity;

    TankType(FluidAmount capacity) {
      this.capacity = capacity;
    }

    @Override
    public String asString() {
      return this.toString().toLowerCase(Locale.US);
    }

    public FluidAmount getCapacity() {
      return capacity;
    }
  }
}
