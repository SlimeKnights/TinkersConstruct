package tconstruct.tools.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.items.tools.Dagger;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.tools.*;

public class DaggerEntity extends ProjectileBase
{
    public int roll;

    public DaggerEntity(World world) {
        super(world);
    }

    public DaggerEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
    }

    public DaggerEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);

        float pitch = Math.max(-90f, player.rotationPitch - 20f);
        // same as in the others, but with pitch upped
        this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, pitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.posY -= 0.10000000149011612D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, accuracy);
    }

    @Override
    public void onHitBlock(MovingObjectPosition movingobjectposition)
    {
        super.onHitBlock(movingobjectposition);
        this.defused = true;
    }

    @Override
    public void onUpdate() {
        // you turn me right round baby
        if(this.ticksInGround == 0)
            roll = (roll + 20) % 360;

        super.onUpdate();
    }

    @Override
    public void onHitEntity(MovingObjectPosition movingobjectposition) {
        AbilityHelper.onLeftClickEntity(returnStack, (EntityPlayer)shootingEntity, movingobjectposition.entityHit, (ToolCore)returnStack.getItem());
        //super.onHitEntity(movingobjectposition);
    }

    @Override
    protected double getGravity() {
        return 0.1;
    }

    @Override
    protected double getSlowdown() {
        return 0.02;
    }
}
