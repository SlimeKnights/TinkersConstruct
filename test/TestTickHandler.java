package test;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TestTickHandler implements ITickHandler
{
    public static Minecraft mc;
    //private RenderBlocks renderBlocksInstance = new RenderBlocks();

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        if (mc.theWorld != null)
        {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(mc.theWorld, mc.thePlayer, false);
            mod_Test.mop = movingobjectposition;
        }
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel ()
    {
        return "render.ghost";
    }

    public MovingObjectPosition getMovingObjectPositionFromPlayer (World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double) f;
        double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double) f + 1.62D - (double) par2EntityPlayer.yOffset;
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double) f;
        Vec3 vec3 = par1World.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 500.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP) par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return par1World.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }
}
