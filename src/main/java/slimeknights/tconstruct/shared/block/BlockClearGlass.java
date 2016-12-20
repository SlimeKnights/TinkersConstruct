package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.BlockConnectedTexture;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockClearGlass extends BlockConnectedTexture {

  public BlockClearGlass() {
    super(Material.GLASS);

    this.setHardness(0.3f);
    setHarvestLevel("pickaxe", -1);
    this.setSoundType(SoundType.GLASS);

    this.setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
}
