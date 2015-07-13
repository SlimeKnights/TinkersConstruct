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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Set;

public class ToolTableBlock extends BlockTable {

  public static final PropertyEnum TABLES = PropertyEnum.create("type", TableTypes.class);
  public final Set<String> toolForgeBlocks = Sets.newHashSet(); // oredict list of toolforge blocks

  public ToolTableBlock() {
    super(Material.wood);
    this.setCreativeTab(CreativeTabs.tabFood); // todo: fix

    this.setStepSound(soundTypeWood);
    this.setResistance(5.0f);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    // planks for the stencil table
    addBlocksFromOredict("plankWood", TableTypes.StencilTable.ordinal(), list);

    // logs for the part builder
    addBlocksFromOredict("logWood", TableTypes.PartBuilder.ordinal(), list);

    // stencil table is boring
    //addBlocksFromOredict("workbench", TableTypes.ToolStation.ordinal(), list);
    list.add(new ItemStack(this, TableTypes.ToolStation.ordinal()));

    // toolforge has custom blocks
    for(String oredict : toolForgeBlocks) {
      addBlocksFromOredict(oredict, TableTypes.ToolForge.ordinal(), list);
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
          list.add(createItemstackWithBlock(this, meta,
                                            Block.getBlockFromItem(subBlock.getItem()), subBlock.getItemDamage()));
        }
      }
      else {
        list.add(createItemstackWithBlock(this, meta, block, blockMeta));
      }
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new ExtendedBlockState(this, new IProperty[]{TABLES}, new IUnlistedProperty[]{TEXTURE});
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta > TableTypes.values().length || meta < 0) {
      meta = 0;
    }

    return this.getDefaultState().withProperty(TABLES, TableTypes.values()[meta]);
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((TableTypes) state.getValue(TABLES)).ordinal();
  }

  public enum TableTypes implements IStringSerializable {
    StencilTable,
    PartBuilder,
    ToolStation,
    ToolForge;

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
