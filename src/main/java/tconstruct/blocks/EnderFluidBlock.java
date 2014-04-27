package tconstruct.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fluids.Fluid;
import tconstruct.TConstruct;

public class EnderFluidBlock extends TConstructFluid
{
    public EnderFluidBlock(int id, Fluid fluid, Material material, String texture, int color)
    {
        super(id, fluid, material, texture, color);
    }

    @Override
    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity)
    {
        if (world.getTotalWorldTime() % 4 == 0)
        {
            int xPos = x - 8 + world.rand.nextInt(17);
            int yPos = y + world.rand.nextInt(8);
            int zPos = z - 8 + world.rand.nextInt(17);

            if (!world.getBlockMaterial(xPos, yPos, zPos).isSolid())
            {
                for (int i = 0; i < 12; i++)
                {
                    world.spawnParticle("portal", x + world.rand.nextFloat() * 5 - 2, y + world.rand.nextFloat() * 4, z + world.rand.nextFloat() * 5 - 2, 0, 0, 0);
                }
                if (entity instanceof EntityLivingBase)
                {
                    teleportEntityTo((EntityLivingBase) entity, xPos, yPos, zPos);
                }
                else
                {
                    entity.setPosition(xPos, yPos, zPos);
                    entity.worldObj.playSoundEffect(xPos, yPos, zPos, "mob.endermen.portal", 1.0F, 1.0F);
                    entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
                }
            }
        }
    }

    public static boolean teleportEntityTo (EntityLivingBase entity, double x, double y, double z)
    {
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }
        double xSound = entity.posX;
        double ySound = entity.posY;
        double zSound = entity.posZ;

        entity.posX = event.targetX;
        entity.posY = event.targetY;
        entity.posZ = event.targetZ;

        entity.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
        entity.worldObj.playSoundEffect(xSound, ySound, zSound, "mob.endermen.portal", 1.0F, 1.0F);
        entity.worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1.0F, 1.0F);
        entity.playSound("mob.endermen.portal", 1.0F, 1.0F);

        return true;
    }
}
