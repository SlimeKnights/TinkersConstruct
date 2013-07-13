package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.TConstruct;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class SlimeClone extends GolemBase implements IEntityAdditionalSpawnData
{
    public float sizeOffset;
    public float sizeFactor;
    public float sizeHeight;
    public String username = "";

    public SlimeClone(World world)
    {
        super(world);
        //this.texture = "/mob/char.png";
    }

    public SlimeClone(World world, String username)
    {
        this(world);
        this.username = username;
    }

    @Override
    public void initCreature ()
    {
        maxHealth = 100;
        health = 100;
        baseAttack = 3;
        paused = false;
    }

    public void onUpdate ()
    {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0 && this.getSlimeSize() > 0)
        {
            this.isDead = true;
        }

        this.sizeFactor += (this.sizeOffset - this.sizeFactor) * 0.5F;
        this.sizeHeight = this.sizeFactor;
        boolean flag = this.onGround;
        super.onUpdate();
        float i;

        if (this.onGround && !flag)
        {
            i = this.getSlimeSize();

            for (int j = 0; j < i * 8; ++j)
            {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float offset = this.rand.nextFloat() * 0.5F + 0.5F;
                float xPos = MathHelper.sin(f) * (float) i * 0.5F * offset;
                float zPos = MathHelper.cos(f) * (float) i * 0.5F * offset;
                TConstruct.proxy.spawnParticle(this.getSlimeParticle(), this.posX + (double) xPos, this.boundingBox.minY, this.posZ + (double) zPos, 0.0D, 0.0D, 0.0D);
            }

            if (this.makesSoundOnLand())
            {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.sizeOffset = -0.5F;
        }
        else if (!this.onGround && flag)
        {
            this.sizeOffset = 1.0F;
        }

        this.func_70808_l();

        if (this.worldObj.isRemote)
        {
            i = this.getSlimeSize();
            this.setSize(0.6F * (float) i, 0.6F * (float) i);
        }
    }

    protected void func_70808_l ()
    {
        this.sizeOffset *= 0.6F;
    }

    protected String getJumpSound ()
    {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    protected void jump ()
    {
        this.motionY = 0.05 * getSlimeSize() + 0.37;

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    protected void fall (float par1)
    {
    }

    protected String getSlimeParticle ()
    {
        return "blueslime";
    }

    public float getSlimeSize ()
    {
        return 1.5f;
    }

    /**
     * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
     */
    protected boolean makesSoundOnJump ()
    {
        return this.getSlimeSize() > 0;
    }

    /**
     * Returns true if the slime makes a sound when it lands after a jump (based upon the slime's size)
     */
    protected boolean makesSoundOnLand ()
    {
        return this.getSlimeSize() > 2;
    }

    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        tags.setString("Username", username);
    }

    public void readEntityFromNBT (NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        username = tags.getString("Username");
    }

    @Override
    public void writeSpawnData (ByteArrayDataOutput data)
    {
        data.writeUTF(username);
    }

    @Override
    public void readSpawnData (ByteArrayDataInput data)
    {
        username = data.readUTF();
        skinUrl = "http://skins.minecraft.net/MinecraftSkins/" + username + ".png";
    }
}
