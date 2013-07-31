package tconstruct.skill;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;

public class WallBuilding extends Skill
{
    private static final ResourceLocation one = new ResourceLocation("tinker", "textures/skill/Wall16x.png");
    private static final ResourceLocation two = new ResourceLocation("tinker", "textures/skill/Wall32x.png");
    private static final ResourceLocation three = new ResourceLocation("tinker", "textures/skill/Wall48x.png");

    @Override
    public ResourceLocation getResource (int guiscale)
    {
        if (guiscale == 2)
            return two;
        if (guiscale == 3)
            return three;
        return one;
    }

    @Override
    public String getSkillName ()
    {
        return "Wall Building";
    }

    @Override
    public void activate (Entity entity, World world)
    {
        this.active = !active;
        System.out.println("Active: " + active);
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

                        ItemStack copy = stack.copy();

                        for (int y = -1; y <= 1; y++)
                        {
                            for (int x = xMin; x <= xMax; x++)
                            {
                                for (int z = zMin; z <= zMax; z++)
                                {
                                    stack.getItem().onItemUse(stack, player, world, xPos + x, yPos + y, zPos + z, mop.sideHit, 0, 0, 0);
                                    if (player.capabilities.isCreativeMode)
                                    {
                                        stack = copy.copy();
                                    }

                                    if (stack.stackSize < 1)
                                    {
                                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                        break;
                                    }
                                }
                            }
                        }

                        if (player.capabilities.isCreativeMode)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                        }
                        Block block = Block.blocksList[copy.itemID];
                        if (block != null)
                        {
                            world.playSoundEffect((double) ((float) xPos + 0.5F), (double) ((float) yPos + 0.5F), (double) ((float) zPos + 0.5F), block.stepSound.getPlaceSound(),
                                    (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                        }
                        //world.playAuxSFX(2001, xPos, yPos, zPos, copy.itemID + (copy.getItemDamage() << 12));
                    }
                }
            }
        }
    }

}
