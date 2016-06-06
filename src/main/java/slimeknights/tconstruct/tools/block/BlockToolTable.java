package slimeknights.tconstruct.tools.block;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.tileentity.TileCraftingStation;
import slimeknights.tconstruct.tools.tileentity.TilePartBuilder;
import slimeknights.tconstruct.tools.tileentity.TilePartChest;
import slimeknights.tconstruct.tools.tileentity.TilePatternChest;
import slimeknights.tconstruct.tools.tileentity.TileStencilTable;
import slimeknights.tconstruct.tools.tileentity.TileToolStation;

public class BlockToolTable extends BlockTable implements ITinkerStationBlock {

  public static final PropertyEnum<TableTypes> TABLES = PropertyEnum.create("type", TableTypes.class);

  public BlockToolTable() {
    super(Material.WOOD);
    this.setCreativeTab(TinkerRegistry.tabGeneral);

    this.setSoundType(SoundType.WOOD);
    this.setResistance(5f);
    this.setHardness(1f);

    // set axe as effective tool for all variants
    this.setHarvestLevel("axe", 0);
  }


  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    switch(TableTypes.fromMeta(meta)) {
      case CraftingStation:
        return new TileCraftingStation();
      case StencilTable:
        return new TileStencilTable();
      case PartBuilder:
        return new TilePartBuilder();
      case ToolStation:
        return new TileToolStation();
      case PatternChest:
        return new TilePatternChest();
      case PartChest:
        return new TilePartChest();
      default:
        return super.createNewTileEntity(worldIn, meta);
    }
  }

  @Override
  public boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    if(!world.isRemote) {
      player.openGui(TConstruct.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
      if(player.openContainer instanceof BaseContainer) {
        ((BaseContainer) player.openContainer).syncOnOpen((EntityPlayerMP) player);
      }
    }
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    // crafting station is boring
    list.add(new ItemStack(this, 1, TableTypes.CraftingStation.meta));

    // planks for the stencil table
    addBlocksFromOredict("plankWood", TableTypes.StencilTable.meta, list);

    list.add(new ItemStack(this, 1, TableTypes.PatternChest.meta));

    // logs for the part builder
    addBlocksFromOredict("logWood", TableTypes.PartBuilder.meta, list);

    list.add(new ItemStack(this, 1, TableTypes.PartChest.meta));

    // stencil table is boring
    //addBlocksFromOredict("workbench", TableTypes.ToolStation.ordinal(), list);
    list.add(new ItemStack(this, 1, TableTypes.ToolStation.meta));

  }

  private void addBlocksFromOredict(String oredict, int meta, List<ItemStack> list) {
    for(ItemStack stack : OreDictionary.getOres(oredict)) {
      Block block = getBlockFromItem(stack.getItem());
      int blockMeta = stack.getItemDamage();

      if(blockMeta == OreDictionary.WILDCARD_VALUE) {
        List<ItemStack> subBlocks = Lists.newLinkedList();
        block.getSubBlocks(stack.getItem(), null, subBlocks);

        for(ItemStack subBlock : subBlocks) {
          list.add(createItemstack(this, meta, getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
        }
      }
      else {
        list.add(createItemstack(this, meta, block, blockMeta));
      }
    }
  }

  @Override
  protected boolean keepInventory(IBlockState state) {
    return Config.chestsKeepInventory &&
           (state.getValue(TABLES) == TableTypes.PatternChest || state.getValue(TABLES) == TableTypes.PartChest);
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new ExtendedBlockState(this, new IProperty[]{TABLES}, new IUnlistedProperty[]{TEXTURE, INVENTORY, FACING});
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TABLES, TableTypes.fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(TABLES)).meta;
  }

  /* Bounds */
  private static AxisAlignedBB BOUNDS_Chest = new AxisAlignedBB(0, 0, 0, 1, 0.875, 1);

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    if(state.getValue(TABLES).isChest) {
      return BOUNDS_Chest;
    }

    return super.getBoundingBox(state, source, pos);
  }

  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
    if(blockState.getValue(TABLES).isChest) {
      return rayTrace(pos, start, end, BOUNDS_Chest);
    }

    return super.collisionRayTrace(blockState, worldIn, pos, start, end);
  }

  @Override
  public int getGuiNumber(IBlockState state) {
    switch(state.getValue(TABLES)) {
      case StencilTable: return 10;
      case PatternChest: return 15;
      case PartChest: return 16;
      case PartBuilder: return 20;
      case ToolStation: return 25;
      case CraftingStation: return 50;
      default: return 0;
    }
  }

  public enum TableTypes implements IStringSerializable {
    CraftingStation,
    StencilTable,
    PartBuilder,
    ToolStation,
    PatternChest(true),
    PartChest(true);

    TableTypes() {
      meta = this.ordinal();
      this.isChest = false;
    }

    TableTypes(boolean chest) {
      meta = this.ordinal();
      this.isChest = chest;
    }

    public final int meta;
    public final boolean isChest;

    public static TableTypes fromMeta(int meta) {
      if(meta < 0 || meta >= values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
