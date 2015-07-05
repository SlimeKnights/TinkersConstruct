package tconstruct.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

import tconstruct.tools.TinkerTools;

public class ToolTableBlock extends BlockTable {
  public static final PropertyEnum TABLES = PropertyEnum.create("type", TableTypes.class);

  public ToolTableBlock() {
    super(Material.wood);
    this.setCreativeTab(CreativeTabs.tabFood);
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(TableTypes type : TableTypes.values()) {
      ItemStack stack = new ItemStack(this, 1, type.ordinal());
      list.add(stack);

      stack = stack.copy();
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("texture", "minecraft:blocks/diamond_block");
      stack.setTagCompound(tag);
      list.add(stack);
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
  public int getMetaFromState(IBlockState state) {
    return ((TableTypes)state.getValue(TABLES)).ordinal();
  }

  public enum TableTypes implements IStringSerializable {
    StencilTable,
    PartBuilder,
    ToolStation;

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
