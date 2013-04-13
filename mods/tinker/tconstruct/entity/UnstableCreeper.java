package mods.tinker.tconstruct.entity;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class UnstableCreeper extends EntityCreeper
{
    protected int fuseTime = 12;
    protected int timeSinceIgnited;
    protected int lastActiveTime;

    public float explosionRadius = 1f;

    public UnstableCreeper(World world)
    {
        super(world);
        this.texture = "/mods/tinker/textures/mob/creeperunstable.png";
    }

    public int getMaxHealth ()
    {
        return 12;
    }

    protected void fall (float distance)
    {
        if (!this.worldObj.isRemote)
        {
            if (distance > 5)
            {
                boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

                if (this.getPowered())
                {
                    this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) (0.75f * (worldObj.difficultySetting - 1)) * 2, false);
                }
                else
                {
                    this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) (0.75f * (worldObj.difficultySetting - 1)), false);
                }

                this.setDead();
            }
            else
                super.fall(distance);
        }
    }

    public void writeEntityToNBT (NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("Fuse", (short) this.fuseTime);
    }

    public void readEntityFromNBT (NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("Fuse"))
        {
            this.fuseTime = par1NBTTagCompound.getShort("Fuse");
        }
    }

    public void onUpdate ()
    {
        if (this.isEntityAlive())
        {
            this.lastActiveTime = this.timeSinceIgnited;
            int i = this.getCreeperState();

            if (i > 0 && this.timeSinceIgnited == 0)
            {
                this.playSound("random.fuse", 1.0F, 0.5F);
            }

            this.timeSinceIgnited += i;

            if (this.timeSinceIgnited < 0)
            {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime)
            {
                this.timeSinceIgnited = this.fuseTime;

                if (!this.worldObj.isRemote)
                {
                    boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");

                    if (this.getPowered())
                    {
                        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) (this.explosionRadius + 1f * (worldObj.difficultySetting - 1)) * 2, flag);
                    }
                    else
                    {
                        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) (this.explosionRadius + 1f * (worldObj.difficultySetting - 1)), flag);
                    }

                    this.setDead();
                }
            }
        }

        super.onUpdate();
    }

    public float getCreeperFlashIntensity (float par1)
    {
        return ((float) this.lastActiveTime + (float) (this.timeSinceIgnited - this.lastActiveTime) * par1) / (float) (this.fuseTime - 2);
    }

    protected void dropFewItems (boolean par1, int par2)
    {
        int j = this.getDropItemId();

        if (j > 0)
        {
            int k = this.rand.nextInt(3) + 3;

            if (par2 > 0)
            {
                k += this.rand.nextInt(par2 + 1);
            }

            for (int l = 0; l < k; ++l)
            {
                this.dropItem(j, 1);
            }
        }
    }
}
