package tconstruct.blocks;

import tconstruct.util.PHConstruct;

import java.util.List;

import tconstruct.client.block.PaneConnectedRender;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class GlassPaneConnected extends GlassBlockConnected {

	private Icon theIcon;

	public GlassPaneConnected(int par1, String location, boolean hasAlpha) {
		super(par1, location, hasAlpha);
	}

	@Override
	public int getRenderType() {
		return PaneConnectedRender.model;
//		return 0;
	}
	
	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		if(par5 == 0 || par5 == 1){
			return getSideTextureIndex();
		}else{
			return super.getBlockTexture(par1IBlockAccess, par2, par3, par4, par5);
		}
	}

	@Override
	public Icon getConnectedBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, Icon[] icons) {
		if(par5 == 0 || par5 == 1){
			return getSideTextureIndex();
		}
		
		if (PHConstruct.connectedTexturesMode == 0) {
			return icons[0];
		}

		boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

		switch (par5) {
		case 0:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[11];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[12];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[13];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[14];
			} else if (isOpenDown && isOpenUp) {
				return icons[5];
			} else if (isOpenLeft && isOpenRight) {
				return icons[6];
			} else if (isOpenDown && isOpenLeft) {
				return icons[8];
			} else if (isOpenDown && isOpenRight) {
				return icons[10];
			} else if (isOpenUp && isOpenLeft) {
				return icons[7];
			} else if (isOpenUp && isOpenRight) {
				return icons[9];
			} else if (isOpenDown) {
				return icons[3];
			} else if (isOpenUp) {
				return icons[4];
			} else if (isOpenLeft) {
				return icons[2];
			} else if (isOpenRight) {
				return icons[1];
			}
			break;
		case 1:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[11];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[12];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[13];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[14];
			} else if (isOpenDown && isOpenUp) {
				return icons[5];
			} else if (isOpenLeft && isOpenRight) {
				return icons[6];
			} else if (isOpenDown && isOpenLeft) {
				return icons[8];
			} else if (isOpenDown && isOpenRight) {
				return icons[10];
			} else if (isOpenUp && isOpenLeft) {
				return icons[7];
			} else if (isOpenUp && isOpenRight) {
				return icons[9];
			} else if (isOpenDown) {
				return icons[3];
			} else if (isOpenUp) {
				return icons[4];
			} else if (isOpenLeft) {
				return icons[2];
			} else if (isOpenRight) {
				return icons[1];
			}
			break;
		case 2:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[13];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[14];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[11];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[12];
			} else if (isOpenDown && isOpenUp) {
				return icons[6];
			} else if (isOpenLeft && isOpenRight) {
				return icons[5];
			} else if (isOpenDown && isOpenLeft) {
				return icons[9];
			} else if (isOpenDown && isOpenRight) {
				return icons[10];
			} else if (isOpenUp && isOpenLeft) {
				return icons[7];
			} else if (isOpenUp && isOpenRight) {
				return icons[8];
			} else if (isOpenDown) {
				return icons[1];
			} else if (isOpenUp) {
				return icons[2];
			} else if (isOpenLeft) {
				return icons[4];
			} else if (isOpenRight) {
				return icons[3];
			}
			break;
		case 3:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[14];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[13];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[11];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[12];
			} else if (isOpenDown && isOpenUp) {
				return icons[6];
			} else if (isOpenLeft && isOpenRight) {
				return icons[5];
			} else if (isOpenDown && isOpenLeft) {
				return icons[10];
			} else if (isOpenDown && isOpenRight) {
				return icons[9];
			} else if (isOpenUp && isOpenLeft) {
				return icons[8];
			} else if (isOpenUp && isOpenRight) {
				return icons[7];
			} else if (isOpenDown) {
				return icons[1];
			} else if (isOpenUp) {
				return icons[2];
			} else if (isOpenLeft) {
				return icons[3];
			} else if (isOpenRight) {
				return icons[4];
			}
			break;
		case 4:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[14];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[13];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[11];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[12];
			} else if (isOpenDown && isOpenUp) {
				return icons[6];
			} else if (isOpenLeft && isOpenRight) {
				return icons[5];
			} else if (isOpenDown && isOpenLeft) {
				return icons[10];
			} else if (isOpenDown && isOpenRight) {
				return icons[9];
			} else if (isOpenUp && isOpenLeft) {
				return icons[8];
			} else if (isOpenUp && isOpenRight) {
				return icons[7];
			} else if (isOpenDown) {
				return icons[1];
			} else if (isOpenUp) {
				return icons[2];
			} else if (isOpenLeft) {
				return icons[3];
			} else if (isOpenRight) {
				return icons[4];
			}
			break;
		case 5:
			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4))) {
				isOpenDown = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4))) {
				isOpenUp = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1))) {
				isOpenLeft = true;
			}

			if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1))) {
				isOpenRight = true;
			}

			if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight) {
				return icons[15];
			} else if (isOpenUp && isOpenDown && isOpenLeft) {
				return icons[13];
			} else if (isOpenUp && isOpenDown && isOpenRight) {
				return icons[14];
			} else if (isOpenUp && isOpenLeft && isOpenRight) {
				return icons[11];
			} else if (isOpenDown && isOpenLeft && isOpenRight) {
				return icons[12];
			} else if (isOpenDown && isOpenUp) {
				return icons[6];
			} else if (isOpenLeft && isOpenRight) {
				return icons[5];
			} else if (isOpenDown && isOpenLeft) {
				return icons[9];
			} else if (isOpenDown && isOpenRight) {
				return icons[10];
			} else if (isOpenUp && isOpenLeft) {
				return icons[7];
			} else if (isOpenUp && isOpenRight) {
				return icons[8];
			} else if (isOpenDown) {
				return icons[1];
			} else if (isOpenUp) {
				return icons[2];
			} else if (isOpenLeft) {
				return icons[4];
			} else if (isOpenRight) {
				return icons[3];
			}
			break;
		}

		return icons[0];
	}

	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
		boolean flag = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.NORTH);
		boolean flag1 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.SOUTH);
		boolean flag2 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.WEST);
		boolean flag3 = this.canPaneConnectTo(par1World, par2, par3, par4, ForgeDirection.EAST);

		if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
			if (flag2 && !flag3) {
				this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
				super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
			} else if (!flag2 && flag3) {
				this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
				super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
			}
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		}

		if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
			if (flag && !flag1) {
				this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
				super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
			} else if (!flag && flag1) {
				this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
				super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
			}
		} else {
			this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
		}
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		float f = 0.4375F;
		float f1 = 0.5625F;
		float f2 = 0.4375F;
		float f3 = 0.5625F;
		boolean flag = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.NORTH);
		boolean flag1 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.SOUTH);
		boolean flag2 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.WEST);
		boolean flag3 = this.canPaneConnectTo(par1IBlockAccess, par2, par3, par4, ForgeDirection.EAST);

		if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
			if (flag2 && !flag3) {
				f = 0.0F;
			} else if (!flag2 && flag3) {
				f1 = 1.0F;
			}
		} else {
			f = 0.0F;
			f1 = 1.0F;
		}

		if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
			if (flag && !flag1) {
				f2 = 0.0F;
			} else if (!flag && flag1) {
				f3 = 1.0F;
			}
		} else {
			f2 = 0.0F;
			f3 = 1.0F;
		}

		this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
	}

	public Icon getSideTextureIndex() {
		return this.theIcon;
	}

	public final boolean canThisPaneConnectToThisBlockID(int par1) {
		return Block.opaqueCubeLookup[par1] || par1 == this.blockID || par1 == Block.glass.blockID;
	}

	public void registerIcons(IconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		this.theIcon = par1IconRegister.registerIcon("tinker:glass/" + folder + "/glass_side");
	}

	public boolean canPaneConnectTo(IBlockAccess access, int x, int y, int z, ForgeDirection dir) {
		return canThisPaneConnectToThisBlockID(access.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) || access.isBlockSolidOnSide(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), false);
	}

}
