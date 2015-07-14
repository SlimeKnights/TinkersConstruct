package tconstruct.tools.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Set;

import tconstruct.TConstruct;
import tconstruct.common.block.BlockTable;
import tconstruct.common.tileentity.TileTable;
import tconstruct.tools.tileentity.TileCraftingStation;

public class BlockToolTable extends BlockTable {

  public static final PropertyEnum TABLES = PropertyEnum.create("type", TableTypes.class);
  public final Set<String> toolForgeBlocks = Sets.newHashSet(); // oredict list of toolforge blocks

  public BlockToolTable() {
    super(Material.wood);
    this.setCreativeTab(CreativeTabs.tabFood); // todo: fix

    this.setStepSound(soundTypeWood);
    this.setResistance(5.0f);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    switch(TableTypes.fromMeta(meta)) {
      case CraftingStation:
        return new TileCraftingStation();
      case StencilTable:
      case PartBuilder:
      case ToolStation:
      case ToolForge:
      default:
        return super.createNewTileEntity(worldIn, meta);
    }
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    player.openGui(TConstruct.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    // crafting station is boring
    list.add(new ItemStack(this, 1, TableTypes.CraftingStation.meta));

    // planks for the stencil table
    addBlocksFromOredict("plankWood", TableTypes.StencilTable.meta, list);

    // logs for the part builder
    addBlocksFromOredict("logWood", TableTypes.PartBuilder.meta, list);

    // stencil table is boring
    //addBlocksFromOredict("workbench", TableTypes.ToolStation.ordinal(), list);
    list.add(new ItemStack(this, 1, TableTypes.ToolStation.meta));

    // toolforge has custom blocks
    for(String oredict : toolForgeBlocks) {
      // only add the first entry per oredict
      List<ItemStack> ores = OreDictionary.getOres(oredict);
      if(ores.size() > 0) {
        list.add(createItemstack(this, TableTypes.ToolForge.meta, Block.getBlockFromItem(ores.get(0).getItem()),
                                 ores.get(0).getItemDamage()));
      }
    }
  }

  private void addBlocksFromOredict(String oredict, int meta, List<ItemStack> list) {
    for(ItemStack stack : OreDictionary.getOres(oredict)) {
      Block block = Block.getBlockFromItem(stack.getItem());
      int blockMeta = stack.getItemDamage();

      if(blockMeta == OreDictionary.WILDCARD_VALUE) {
        List<ItemStack> subBlocks = Lists.newLinkedList();
        block.getSubBlocks(stack.getItem(), null, subBlocks);

        for(ItemStack subBlock : subBlocks) {
          list.add(createItemstack(this, meta, Block.getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
        }
      }
      else {
        list.add(createItemstack(this, meta, block, blockMeta));
      }
    }
  }


  @Override
  protected BlockState createBlockState() {
    return new ExtendedBlockState(this, new IProperty[]{TABLES}, new IUnlistedProperty[]{TEXTURE});
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TABLES, TableTypes.fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((TableTypes) state.getValue(TABLES)).meta;
  }

  public enum TableTypes implements IStringSerializable {
    CraftingStation,
    StencilTable,
    PartBuilder,
    ToolStation,
    ToolForge;

    TableTypes() {
      meta = this.ordinal();
    }

    public final int meta;

    public static TableTypes fromMeta(int meta) {
      if(meta < 0 || meta > values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
