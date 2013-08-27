package tconstruct.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.blocks.CrystalBlock;

public class LightCrystalItem extends ItemBlock
{
    private int bID;

    public LightCrystalItem(int id)
    {
        super(id);
        setMaxDamage(0);
        this.bID = id + 256;
        //setHasSubtypes(true);
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        int i1 = world.getBlockId(x, y, z);

        if (i1 == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID
                && (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(world, x, y, z)))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        else if (y == 255 && Block.blocksList[this.bID].blockMaterial.isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(this.bID, x, y, z, false, side, player, stack))
        {
            Block block = Block.blocksList[this.bID];

            int crystalValue = 0;
            if (stack.hasTagCompound())
            {
                crystalValue = stack.getTagCompound().getInteger("Value");
            }

            int placeMeta = getBaseMeta(crystalValue);

            if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, placeMeta))
            {
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.getPlaceSound(),
                        (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --stack.stackSize;

                int height = CrystalBlock.getCrystalHeight(crystalValue);
                if (height > 1)
                {
                    int localID = world.getBlockId(x, y + 1, z);
                    if (localID != Block.vine.blockID && localID != Block.tallGrass.blockID && localID != Block.deadBush.blockID
                            && (Block.blocksList[localID] == null || !Block.blocksList[localID].isBlockReplaceable(world, x, y+1, z)))
                    {
                        placeBlockAt(stack, player, world, x, y+1, z, side, hitX, hitY, hitZ, secondMeta(crystalValue));
                    }
                }
                if (height > 2)
                {
                    int localID = world.getBlockId(x, y + 2, z);
                    if (localID != Block.vine.blockID && localID != Block.tallGrass.blockID && localID != Block.deadBush.blockID
                            && (Block.blocksList[localID] == null || !Block.blocksList[localID].isBlockReplaceable(world, x, y+2, z)))
                    {
                        placeBlockAt(stack, player, world, x, y+2, z, side, hitX, hitY, hitZ, thirdMeta(crystalValue));
                    }
                }
                if (height > 3)
                {
                    int localID = world.getBlockId(x, y + 3, z);
                    if (localID != Block.vine.blockID && localID != Block.tallGrass.blockID && localID != Block.deadBush.blockID
                            && (Block.blocksList[localID] == null || !Block.blocksList[localID].isBlockReplaceable(world, x, y+3, z)))
                    {
                        placeBlockAt(stack, player, world, x, y+3, z, side, hitX, hitY, hitZ, topMeta(crystalValue));
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    int getBaseMeta (int crystalValue)
    {
        if (crystalValue >= 120)
        {
            return 5;
        }
        if (crystalValue >= 80)
        {
            return 4;
        }
        if (crystalValue >= 48)
        {
            return 3;
        }
        if (crystalValue >= 24)
        {
            return 2;
        }
        if (crystalValue >= 8)
        {
            return 1;
        }

        return 0;
    }

    int secondMeta (int crystalValue)
    {
        if (crystalValue >= 224)
        {
            return 9;
        }
        if (crystalValue >= 168)
        {
            return 8;
        }
        if (crystalValue >= 120)
        {
            return 7;
        }
        return 6;
    }

    int thirdMeta (int crystalValue)
    {
        if (crystalValue >= 440)
        {
            return 13;
        }
        if (crystalValue >= 360)
        {
            return 12;
        }
        if (crystalValue >= 288)
        {
            return 11;
        }
        return 10;
    }

    public int topMeta (int crystalValue)
    {
        if (crystalValue >= 528)
        {
            return 15;
        }
        return 14;
    }

    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.hasTagCompound())
        {
            int value = stack.getTagCompound().getInteger("Value");
            list.add("Crystal Value: " + value);
        }
    }
}
