package tconstruct.tools.block;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Set;

import tconstruct.common.block.BlockTable;
import tconstruct.library.TinkerRegistry;
import tconstruct.tools.tileentity.TileToolStation;

// This literally only is its own block because it has a different material
public class BlockToolForge extends BlockTable implements ITinkerStationBlock {

  public final Set<String> baseBlocks = Sets.newHashSet(); // oredict list of toolforge blocks

  public BlockToolForge() {
    super(Material.iron);
    this.setCreativeTab(TinkerRegistry.tabTools);

    this.setStepSound(soundTypeMetal);
    this.setResistance(10f);
    this.setHardness(5f);

    this.setHarvestLevel("pickaxe", 0);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileToolStation();
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    // toolforge has custom blocks
    for(String oredict : baseBlocks) {
      // only add the first entry per oredict
      List<ItemStack> ores = OreDictionary.getOres(oredict);
      if(ores.size() > 0) {
        list.add(createItemstack(this, 0, Block.getBlockFromItem(ores.get(0).getItem()),
                                 ores.get(0).getItemDamage()));
      }
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{TEXTURE});
  }

  @Override
  public boolean isMaster(IBlockState state) {
    return false;
  }

  @Override
  public int getGuiNumber(IBlockState state) {
    // same as toolstation
    return 25;
  }
}
