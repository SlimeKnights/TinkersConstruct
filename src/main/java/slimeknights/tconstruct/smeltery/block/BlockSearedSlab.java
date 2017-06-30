package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockSearedSlab extends EnumBlockSlab<BlockSearedSlab.SearedType> implements ITileEntityProvider {

  public final static PropertyEnum<SearedType> TYPE = PropertyEnum.create("type", SearedType.class);

  public BlockSearedSlab() {
    super(Material.ROCK, TYPE, SearedType.class);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);
    this.isBlockContainer = true; // has TE
  }

  @Override
  public IBlockState getFullBlock(IBlockState state) {
    if(TinkerSmeltery.searedBlock == null) {
      return null;
    }
    return TinkerSmeltery.searedBlock.getDefaultState().withProperty(BlockSeared.TYPE, state.getValue(TYPE).asSearedBlock());
  }
  
  /* Multiblock stuff */

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileSmelteryComponent();
  }

  @Override
  public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    TinkerSmeltery.searedBlock.breakBlock(worldIn, pos, state);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TinkerSmeltery.searedBlock.onBlockPlacedBy(worldIn, pos, state, placer, stack);
  }

  @Override
  @Deprecated
  public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
    return TinkerSmeltery.searedBlock.eventReceived(state, worldIn, pos, id, param);
  }

  // using a separate Enum than BlockSeared since there are more variants than the 8 types slabs support
  public enum SearedType implements IStringSerializable, EnumBlock.IEnumMeta {
    STONE,
    COBBLE,
    PAVER,
    BRICK,
    BRICK_CRACKED,
    BRICK_FANCY,
    BRICK_SQUARE,
    ROAD;

    public final int meta;

    SearedType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    public BlockSeared.SearedType asSearedBlock() {
      switch(this) {
        case STONE:
          return BlockSeared.SearedType.STONE;
        case COBBLE:
          return BlockSeared.SearedType.COBBLE;
        case PAVER:
          return BlockSeared.SearedType.PAVER;
        case BRICK:
          return BlockSeared.SearedType.BRICK;
        case BRICK_CRACKED:
          return BlockSeared.SearedType.BRICK_CRACKED;
        case BRICK_FANCY:
          return BlockSeared.SearedType.BRICK_FANCY;
        case BRICK_SQUARE:
          return BlockSeared.SearedType.BRICK_SQUARE;
        case ROAD:
          return BlockSeared.SearedType.ROAD;
        default:
          throw new IllegalArgumentException("Unknown enum value? Impossibru!");
      }
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
