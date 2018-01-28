package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockSlimeGrass extends Block implements IGrowable {

  public static PropertyEnum<DirtType> TYPE = PropertyEnum.create("type", DirtType.class);
  public static PropertyEnum<FoliageType> FOLIAGE = PropertyEnum.create("foliage", FoliageType.class);
  public static final PropertyBool SNOWY = PropertyBool.create("snowy");

  public BlockSlimeGrass() {
    super(Material.GRASS);
    this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, Boolean.FALSE));
    this.setTickRandomly(true);
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setHardness(0.65f);
    this.setSoundType(SoundType.PLANT);
    this.slipperiness += 0.05f;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(FoliageType grass : FoliageType.values()) {
      for(DirtType type : DirtType.values()) {
        list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(TYPE, type).withProperty(FOLIAGE, grass))));
      }
    }
  }

  @Override
  public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
    return true;
  }

  @Override
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    return true;
  }

  @Override
  public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    BlockPos blockpos1 = pos.up();
    int i = 0;

    while(i < 128) {
      BlockPos blockpos2 = blockpos1;
      int j = 0;

      while(true) {
        if(j < i / 16) {
          blockpos2 = blockpos2.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

          if(worldIn.getBlockState(blockpos2.down()).getBlock() == this && !worldIn.getBlockState(blockpos2).getBlock().isNormalCube(state)) {
            ++j;
            continue;
          }
        }
        else if(worldIn.isAirBlock(blockpos2)) {
          IBlockState plantState;
          if(rand.nextInt(8) == 0) {
            plantState = TinkerWorld.slimeGrassTall.getDefaultState().withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.FERN);
          }
          else {
            plantState = TinkerWorld.slimeGrassTall.getDefaultState().withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.TALL_GRASS);
          }

          plantState = plantState.withProperty(BlockTallSlimeGrass.FOLIAGE, state.getValue(FOLIAGE));

          if(TinkerWorld.slimeGrassTall.canBlockStay(worldIn, blockpos2, plantState)) {
            worldIn.setBlockState(blockpos2, plantState, 3);
          }
        }

        ++i;
        break;
      }
    }
  }

  @Override
  public void updateTick(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nonnull Random rand) {
    if(worldIn.isRemote) {
      return;
    }

    // spread to surrounding blocks
    if(worldIn.getLightFromNeighbors(pos.up()) >= 9) {
      for(int i = 0; i < 4; ++i) {
        BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

        if(blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
          return;
        }

        IBlockState iblockstate = worldIn.getBlockState(blockpos.up());
        IBlockState iblockstate1 = worldIn.getBlockState(blockpos);

        if(worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2) {
          convert(worldIn, blockpos, iblockstate1, state.getValue(FOLIAGE));
        }
      }
    }
  }

  public void convert(World world, BlockPos pos, IBlockState state, FoliageType foliageType) {
    IBlockState newState = getStateFromDirt(state);
    if(newState != null) {
      world.setBlockState(pos, newState.withProperty(FOLIAGE, foliageType));
    }
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, FOLIAGE, BlockGrass.SNOWY);
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta > 14) {
      meta = 0;
    }

    return this.getDefaultState().withProperty(TYPE, DirtType.values()[meta % 5]).withProperty(FOLIAGE, FoliageType.values()[meta / 5]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    DirtType type = state.getValue(TYPE);
    FoliageType grass = state.getValue(FOLIAGE);

    //type goes from 0-5, grass goes from 0-2 resulting in 0-5, 6-10, 11-15
    return type.ordinal() + grass.ordinal() * 5;
  }

  @Override
  public int damageDropped(IBlockState state) {
    DirtType type = state.getValue(TYPE);
    if(type == DirtType.VANILLA) {
      return 0;
    }

    return getDirtState(state).getValue(BlockSlimeDirt.TYPE).getMeta();
  }

  @Override
  public Item getItemDropped(IBlockState state, @Nonnull Random rand, int fortune) {
    return Item.getItemFromBlock(getDirtState(state).getBlock());
  }

  @Nonnull
  @Override
  public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
    return this.getSilkTouchDrop(world.getBlockState(pos));
  }

  /** Returns the blockstate for the dirt underneath the grass */
  public IBlockState getDirtState(IBlockState grassState) {
    DirtType type = grassState.getValue(TYPE);
    switch(type) {
      case VANILLA:
        return Blocks.DIRT.getDefaultState();
      case GREEN:
        return TinkerWorld.slimeDirt.getStateFromMeta(BlockSlimeDirt.DirtType.GREEN.getMeta());
      case BLUE:
        return TinkerWorld.slimeDirt.getStateFromMeta(BlockSlimeDirt.DirtType.BLUE.getMeta());
      case PURPLE:
        return TinkerWorld.slimeDirt.getStateFromMeta(BlockSlimeDirt.DirtType.PURPLE.getMeta());
      case MAGMA:
        return TinkerWorld.slimeDirt.getStateFromMeta(BlockSlimeDirt.DirtType.MAGMA.getMeta());
    }
    return TinkerWorld.slimeDirt.getStateFromMeta(BlockSlimeDirt.DirtType.GREEN.getMeta());
  }

  /** Returns the grass blockstate for the given dirt type or null */
  public IBlockState getStateFromDirt(IBlockState dirtState) {
    // vanilla dirt?
    if(dirtState.getBlock() == Blocks.DIRT && dirtState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
      return this.getDefaultState().withProperty(TYPE, DirtType.VANILLA);
    }
    // slimedirt block?
    if(dirtState.getBlock() == TinkerWorld.slimeDirt) {
      // green slimedirt
      if(dirtState.getValue(BlockSlimeDirt.TYPE) == BlockSlimeDirt.DirtType.GREEN) {
        return this.getDefaultState().withProperty(TYPE, DirtType.GREEN);
      }
      // blue slimedirt
      else if(dirtState.getValue(BlockSlimeDirt.TYPE) == BlockSlimeDirt.DirtType.BLUE) {
        return this.getDefaultState().withProperty(TYPE, DirtType.BLUE);
      }
      // purple slimedirt
      else if(dirtState.getValue(BlockSlimeDirt.TYPE) == BlockSlimeDirt.DirtType.PURPLE) {
        return this.getDefaultState().withProperty(TYPE, DirtType.PURPLE);
      }
      // magma slimedirt
      else if(dirtState.getValue(BlockSlimeDirt.TYPE) == BlockSlimeDirt.DirtType.MAGMA) {
        return this.getDefaultState().withProperty(TYPE, DirtType.MAGMA);
      }
    }

    return null;
  }

  @Override
  public boolean canSustainPlant(@Nonnull IBlockState state, @Nonnull IBlockAccess world, BlockPos pos, @Nonnull EnumFacing direction, IPlantable plantable) {
    // can sustain both slimeplants and normal plants
    return plantable.getPlantType(world, pos) == TinkerWorld.slimePlantType || plantable.getPlantType(world, pos) == EnumPlantType.Plains;
  }

  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer()
  {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }

  public enum FoliageType implements IStringSerializable, EnumBlock.IEnumMeta {
    BLUE,
    PURPLE,
    ORANGE;

    public static FoliageType getValFromMeta(int meta) {
      if(meta < 0 || meta >= values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public int getMeta() {
      return ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }

  public enum DirtType implements IStringSerializable {
    VANILLA,
    GREEN,
    BLUE,
    PURPLE,
    MAGMA;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
