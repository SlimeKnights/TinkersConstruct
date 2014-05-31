package tconstruct.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class MetalSlime extends BlueSlime
{

    public MetalSlime(World world)
    {
        super(world);
    }
    
    @Override
    protected void initializeSlime()
    {
        this.slimeJumpDelay = this.rand.nextInt(30) + 20;
        this.setSlimeSize(3);
    }

    public boolean attackEntityFrom (DamageSource damageSource, int damage)
    {
        if (!damageSource.isExplosion() && damageSource.isProjectile())
            return false;
        return super.attackEntityFrom(damageSource, damage);
    }

    private float getMaxHealthForSize ()
    {
        int i = this.getSlimeSize();
        if (i == 1)
            return 20;
        return (float) Math.min(i * i + 20, 100);
    }

    public int getTotalArmorValue ()
    {
        return super.getTotalArmorValue() + 12;
    }

    public void setDead ()
    {
        this.isDead = true;
    }

    @Override
    protected void dropFewItems (boolean par1, int par2)
    {
    	/*int j = this.getDropItemId();

    	if (j > 0)
    	{
    		int k = rand.nextInt(3) + rand.nextInt(this.getSlimeSize());

    		if (par2 > 0)
    		{
    			k += this.rand.nextInt(par2 + 1);
    		}

    		for (int l = 0; l < k; ++l)
    		{
    			this.dropItem(j, 1);
    		}
    	}*/
    }

    protected void updateEntityActionState ()
    {
        this.despawnEntity();
        EntityPlayer entityplayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);

        if (entityplayer != null)
        {
            this.faceEntity(entityplayer, 10.0F, 20.0F);
        }
        else if (this.onGround && this.slimeJumpDelay == 1)
        {
            this.rotationYaw = this.rotationYaw + rand.nextFloat() * 180 - 90;
            if (rotationYaw > 360)
                rotationYaw -= 360;
            if (rotationYaw < 0)
                rotationYaw += 360;
        }

        if (this.onGround && this.slimeJumpDelay-- <= 0)
        {
            this.slimeJumpDelay = this.getJumpDelay();

            if (entityplayer != null)
            {
                this.slimeJumpDelay /= 3;
            }

            this.isJumping = true;

            if (this.makesSoundOnJump())
            {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
            this.moveForward = (float) (1 * this.getSlimeSize());
        }
        else
        {
            this.isJumping = false;

            if (this.onGround)
            {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }
}
