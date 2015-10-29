package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class BlockSlimeGrass extends BlockGrass {
  public static PropertyEnum TYPE = PropertyEnum.create("type", DirtType.class);
  public static PropertyEnum FOLIAGE = PropertyEnum.create("foliage", FoliageType.class);

  public BlockSlimeGrass() {
    this.setCreativeTab(TinkerRegistry.tabWorld);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(FoliageType grass : FoliageType.values()) {
      for(DirtType type : DirtType.values()) {
        list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(TYPE, type).withProperty(FOLIAGE, grass))));
      }
    }
  }

  @Override
  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    BlockPos blockpos1 = pos.up();
    int i = 0;

    while (i < 128)
    {
      BlockPos blockpos2 = blockpos1;
      int j = 0;

      while (true)
      {
        if (j < i / 16)
        {
          blockpos2 = blockpos2.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

          if (worldIn.getBlockState(blockpos2.down()).getBlock() == this && !worldIn.getBlockState(blockpos2).getBlock().isNormalCube())
          {
            ++j;
            continue;
          }
        }
        else if (worldIn.isAirBlock(blockpos2))
        {
          IBlockState plantState;
          if (rand.nextInt(8) == 0)
          {
            plantState = TinkerWorld.slimeGrassTall.getDefaultState().withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.FERN);
          }
          else
          {
            plantState = TinkerWorld.slimeGrassTall.getDefaultState().withProperty(BlockTallSlimeGrass.TYPE, BlockTallSlimeGrass.SlimePlantType.TALL_GRASS);
          }

          plantState = plantState.withProperty(BlockTallSlimeGrass.FOLIAGE, state.getValue(FOLIAGE));

          if (TinkerWorld.slimeGrassTall.canBlockStay(worldIn, blockpos2, plantState))
          {
            worldIn.setBlockState(blockpos2, plantState, 3);
          }
        }

        ++i;
        break;
      }
    }
  }

  @Override
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if(worldIn.isRemote) {
      return;
    }

    if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getBlock().getLightOpacity(worldIn, pos.up()) > 2)
    {
      // convert grass back to dirt of the corresponding type
      worldIn.setBlockState(pos, getDirtState(state));
    }
    else
    {
      // spread to surrounding blocks
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
      {
        for (int i = 0; i < 4; ++i)
        {
          BlockPos pos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
          Block block = worldIn.getBlockState(pos1.up()).getBlock();
          IBlockState state1 = worldIn.getBlockState(pos1);

          if(worldIn.getLightFromNeighbors(pos1.up()) >= 4 && block.getLightOpacity(worldIn, pos1.up()) <= 2) {
            convert(worldIn, pos1, state1, (FoliageType) state.getValue(FOLIAGE));
          }
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

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE, FOLIAGE, BlockGrass.SNOWY);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta > 14) meta = 0;

    return this.getDefaultState().withProperty(TYPE, DirtType.values()[meta%5]).withProperty(FOLIAGE, FoliageType.values()[meta/5]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    DirtType type = ((DirtType) state.getValue(TYPE));
    FoliageType grass = (FoliageType) state.getValue(FOLIAGE);

    //type goes from 0-5, grass goes from 0-2 resulting in 0-5, 6-10, 11-15
    return type.ordinal() + grass.ordinal()*5;
  }

  @Override
  public int damageDropped(IBlockState state) {
    DirtType type = (DirtType) state.getValue(TYPE);
    if(type == DirtType.VANILLA)
      return 0;

    return ((BlockSlimeDirt.DirtType)getDirtState(state).getValue(BlockSlimeDirt.TYPE)).getMeta();
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(getDirtState(state).getBlock());
  }

  /** Returns the blockstate for the dirt underneath the grass */
  public IBlockState getDirtState(IBlockState grassState) {
    DirtType type = (DirtType) grassState.getValue(TYPE);
    switch(type) {
      case VANILLA:
        return Blocks.dirt.getDefaultState();
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
    if(dirtState.getBlock() == Blocks.dirt && dirtState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
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
    }

    return null;
  }

  @Override
  public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
    // can sustain both slimeplants and normal plants
    return plantable.getPlantType(world, pos) == TinkerWorld.slimePlantType || plantable.getPlantType(world, pos) == EnumPlantType.Plains;
  }

  @Override
  public int getBlockColor() {
    return SlimeColorizer.colorBlue;
  }

  // Used for the item
  @SideOnly(Side.CLIENT)
  @Override
  public int getRenderColor(IBlockState state) {
    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorStaticBGR(foliageType);
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState state = worldIn.getBlockState(pos);
    if(state.getBlock() != this) return getBlockColor();

    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorForPos(pos, foliageType);
  }

  public enum FoliageType implements IStringSerializable {
    BLUE,
    PURPLE,
    ORANGE;

    @Override
    public String getName() {
      return this.toString();
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
      return this.toString();
    }
  }
}
