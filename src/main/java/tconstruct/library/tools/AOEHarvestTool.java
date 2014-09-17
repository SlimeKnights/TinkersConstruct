package tconstruct.library.tools;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.MovingObjectPosition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AOEHarvestTool extends HarvestTool {
    public int breakRadius;
    public int breakDepth;

    public AOEHarvestTool(int baseDamage, int breakRadius, int breakDepth) {
        super(baseDamage);

        this.breakRadius = breakRadius;
        this.breakDepth = breakDepth;
    }

    boolean antiRecurse;

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        // only effective materials matter. We don't want to aoe when beraking dirt with a hammer.
        Block block = player.worldObj.getBlock(x,y,z);
        if(block == null || !isEffective(block.getMaterial()))
            return super.onBlockStartBreak(stack, x,y,z, player);

        boolean originalBlock = true;
        // check if we're breaking the block we hit, or if this call belongs to one of the surrounding blocks broken by the AOE
        if(player.worldObj.isRemote)
        {
            originalBlock = Minecraft.getMinecraft().playerController.sameToolAndBlock(x,y,z);
        }
        // same check serverside
        else {
            ItemInWorldManager iiiwm = ((EntityPlayerMP) player).theItemInWorldManager;

            if(x != iiiwm.partiallyDestroyedBlockX || y != iiiwm.partiallyDestroyedBlockY || z != iiiwm.partiallyDestroyedBlockZ)
                originalBlock = false;
        }

        // the code below initiates block breaks, which again call this function. But we don't want to do the aoe-break-stuff again. This is to prevent recursive, infinite-range aoe blockbreaking.
        if(originalBlock || player.capabilities.isCreativeMode) {
            antiRecurse = true;
            MovingObjectPosition mop = AbilityHelper.raytraceFromEntity(player.worldObj, player, false, 4.5d);
            if(mop == null)
                return super.onBlockStartBreak(stack, x,y,z, player);
            int sideHit = mop.sideHit;
            //int sideHit = Minecraft.getMinecraft().objectMouseOver.sideHit;

            // we successfully destroyed a block. time to do AOE!
            int xRange = breakRadius;
            int yRange = breakRadius;
            int zRange = breakDepth;
            switch (sideHit) {
                case 0:
                case 1:
                    yRange = breakDepth;
                    zRange = breakRadius;
                    break;
                case 2:
                case 3:
                    xRange = breakRadius;
                    zRange = breakDepth;
                    break;
                case 4:
                case 5:
                    xRange = breakDepth;
                    zRange = breakRadius;
                    break;
            }

            for (int xPos = x - xRange; xPos <= x + xRange; xPos++)
                for (int yPos = y - yRange; yPos <= y + yRange; yPos++)
                    for (int zPos = z - zRange; zPos <= z + zRange; zPos++) {
                        // don't break the originally already broken block, duh
                        if (xPos == x && yPos == y && zPos == z)
                            continue;
                        breakExtraBlock(player.worldObj, xPos, yPos, zPos, sideHit, player);
                    }

            antiRecurse = false;
        }



        return super.onBlockStartBreak(stack, x, y, z, player);
    }

}
