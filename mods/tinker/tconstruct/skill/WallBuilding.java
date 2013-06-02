package mods.tinker.tconstruct.skill;

import mods.tinker.tconstruct.library.tools.AbilityHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class WallBuilding extends Skill
{

	@Override
	public String getTextureFile (int guiscale)
	{
		if (guiscale == 2)
			return "/mods/tinker/textures/skill/Wall32x.png";
		if (guiscale == 3)
			return "/mods/tinker/textures/skill/Wall48x.png";

		return "/mods/tinker/textures/skill/Wall16x.png";
	}

	@Override
	public String getSkillName ()
	{
		return "Wall Building";
	}

	@Override
	public void activate (Entity entity, World world)
	{
		if (!world.isRemote)
			this.active = !active;
	}

	@Override
	public void rightClickActivate (Entity entity, World world)
	{
		if (!world.isRemote && active)
		{
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				ItemStack stack = player.getCurrentEquippedItem();
				if (stack != null && stack.getItem() instanceof ItemBlock)
				{
					MovingObjectPosition mop = AbilityHelper.raytraceFromEntity(world, entity, true, 6);

					if (mop != null)
					{
						int xPos = mop.blockX;
						int yPos = mop.blockY;
						int zPos = mop.blockZ;
						/*ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);
						switch (sideHit)
						{
						case UP:
							yPos += 1;
							break;
						case DOWN:
							yPos -= 1;
							break;
						case NORTH:
							zPos -= 1;
							break;
						case SOUTH:
							zPos += 1;
							break;
						case EAST:
							xPos += 1;
							break;
						case WEST:
							xPos -= 1;
							break;
						default:
							break;
						}*/

						int facing = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
						int xMin = 0;
						int xMax = 0;
						int zMin = 0;
						int zMax = 0;
						if (facing % 2 == 0)
						{
							xMin = -1;
							xMax = 1;
						}
						else
						{
							zMin = -1;
							zMax = 1;
						}

						for (int y = -1; y <= 1; y++)
						{
							for (int x = xMin; x <= xMax; x++)
							{
								for (int z = zMin; z <= zMax; z++)
								{
									stack.getItem().onItemUse(stack, player, world, xPos + x, yPos + y, zPos + z, mop.sideHit, 0, 0, 0);
									if (stack.stackSize < 1)
									{
										player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
										break;
									}
									//world.setBlock(xPos + x, yPos + y, zPos + z, Block.whiteStone.blockID);
								}
							}
						}
						//entity.worldObj.setBlock(xPos, yPos, zPos, Block.stone.blockID, 0, 3);
						world.playAuxSFX(2001, xPos, yPos, zPos, Block.stone.blockID + (0 << 12));
					}
				}
			}
			/*for (int x = -2; x <= 2; x++)
			{
				for (int y = -1; y <= 1; y++)
				{
					//for (int z = -2; z <= 2; z++)
					{
						world.setBlock((int) Math.floor(entity.posX) + x, (int) Math.floor(entity.posY) + y, (int) Math.floor(entity.posZ) + 2, Block.whiteStone.blockID);
					}
				}
			}
			world.playAuxSFX(2001, (int) entity.posX, (int) entity.posY, (int) entity.posZ, Block.stone.blockID + (0 << 12));*/
		}
	}

}
