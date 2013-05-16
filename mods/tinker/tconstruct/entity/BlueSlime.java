package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlueSlime extends EntityLiving implements IMob
{
	private static final float[] field_100000_e = new float[] { 1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F };
	public float sizeOffset;
	public float sizeFactor;
	public float sizeHeight;

	/** the time between each jump of the slime */
	protected int slimeJumpDelay = 0;

	public BlueSlime(World world)
	{
		super(world);
		this.texture = "/mods/tinker/textures/mob/slimeedible.png";
		int offset = this.rand.nextInt(99);
		if (offset < 49)
			offset = 1;
		else if (offset < 98)
		    offset = 2;
		else
		    offset = 3;
		int size = 1 << offset;
		this.yOffset = 0.0F;
		this.slimeJumpDelay = this.rand.nextInt(120) + 40;
		this.setSlimeSize(size);
		this.jumpMovementFactor = 0.004F * size + 0.01F;
	}

	protected void damageEntity (DamageSource damageSource, int damage)
	{
		//Minecraft.getMinecraft().getLogAgent().logInfo("Damage: "+damage);
		if (damageSource.damageType.equals("arrow"))
			damage = damage / 2;
		super.damageEntity(damageSource, damage);
	}

	/*public boolean attackEntityFrom(DamageSource damageSource, int damage)
	{
		if (damageSource.damageType.equals("arrow") && rand.nextInt(5) == 0)
			return false;
		return super.attackEntityFrom(damageSource, damage);
	}*/

	@Override
	public void initCreature ()
	{
		if (getSlimeSize() == 2 && rand.nextInt(8) == 0)
		{
			EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
			entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
			entityskeleton.initCreature();
			this.worldObj.spawnEntityInWorld(entityskeleton);
			entityskeleton.mountEntity(this);
		}

	}

	@Override
	public double getMountedYOffset ()
	{
		return this.height * 0.3;
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
		
        if (this.getBrightness(1.0F) > 0.9F && rand.nextInt(5) == 0 && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))
        {
            int size = this.getSlimeSize() - 1;
            if (size <= 0)
                this.kill();
            else
                this.setSlimeSize(size);
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

	@Override
	public boolean getCanSpawnHere ()
	{
		return isValidLightLevel() && this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
				&& !this.worldObj.isAnyLiquid(this.boundingBox);
	}

	protected boolean isValidLightLevel ()
	{
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.boundingBox.minY);
		int z = MathHelper.floor_double(this.posZ);
		
		if (y < 60 && rand.nextInt(5) != 0)
		{
		    return false;
		}

		if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32))
		{
			return false;
		}
		else
		{
			int light = this.worldObj.getBlockLightValue(x, y, z);

			if (this.worldObj.isThundering())
			{
				int i1 = this.worldObj.skylightSubtracted;
				this.worldObj.skylightSubtracted = 10;
				light = this.worldObj.getBlockLightValue(x, y, z);
				this.worldObj.skylightSubtracted = i1;
			}

			return light <= this.rand.nextInt(8);
		}
	}

	protected void entityInit ()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte) 1));
	}

	protected void setSlimeSize (int size)
	{
		this.dataWatcher.updateObject(16, new Byte((byte) size));
		this.setSize(0.6F * size, 0.6F * size);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.setEntityHealth(this.getMaxHealth());
		this.experienceValue = size + 2;
	}

	public int getMaxHealth ()
	{
		int i = this.getSlimeSize();
		if (i == 1)
			return 4;
		return (int) Math.min(i * i + 8, 49);
	}

	/**
	 * Returns the size of the slime.
	 */
	public int getSlimeSize ()
	{
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT (NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("Size", this.getSlimeSize() - 1);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT (NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		this.setSlimeSize(par1NBTTagCompound.getInteger("Size") + 1);
	}

	/**
	 * Returns the name of the sound played when the slime jumps.
	 */
	protected String getJumpSound ()
	{
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	/**
	 * Called to update the entity's position/logic.
	 */
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
		int i;

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

	protected void updateEntityActionState ()
	{
		//Minecraft.getMinecraft().getLogAgent().logInfo("Collided with "+entity.getEntityName());
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
				this.slimeJumpDelay /= 12;
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

	protected void func_70808_l ()
	{
		this.sizeOffset *= 0.6F;
	}

	/**
	 * Gets the amount of time the slime needs to wait between jumps.
	 */
	protected int getJumpDelay ()
	{
		return this.rand.nextInt(120) + 40;
	}

	protected BlueSlime createInstance ()
	{
		return new BlueSlime(this.worldObj);
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead ()
	{
		int i = this.getSlimeSize();

		if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0)
		{
			float f = (-0.5F) * (float) i / 4.0F;
			float f1 = (-0.5F) * (float) i / 4.0F;
			BlueSlime entityslime = this.createInstance();
			entityslime.setSlimeSize(i / 2);
			entityslime.setLocationAndAngles(this.posX + (double) f, this.posY + 0.5D, this.posZ + (double) f1, this.rand.nextFloat() * 360.0F, 0.0F);
			this.worldObj.spawnEntityInWorld(entityslime);
		}

		super.setDead();
	}

	protected void dropFewItems (boolean par1, int par2)
	{
		int j = this.getDropItemId();

		if (j > 0 && rand.nextInt(2) == 0)
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
		}
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer (EntityPlayer par1EntityPlayer)
	{
		if (this.canDamagePlayer())
		{
			int i = this.getSlimeSize();

			if (this.canEntityBeSeen(par1EntityPlayer) && this.getDistanceSqToEntity(par1EntityPlayer) < 0.6D * (double) i * 0.6D * (double) i
					&& par1EntityPlayer.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength()))
			{
				this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	/**
	 * Indicates weather the slime is able to damage the player (based upon the slime's size)
	 */
	protected boolean canDamagePlayer ()
	{
		return this.getSlimeSize() > 1;
	}

	/**
	 * Gets the amount of damage dealt to the player when "attacked" by the slime.
	 */
	protected int getAttackStrength ()
	{
		return this.getSlimeSize();
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound ()
	{
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound ()
	{
		return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
	}

	/**
	 * Returns the item ID for the item the mob drops on death.
	 */
	protected int getDropItemId ()
	{
		return TContent.strangeFood.itemID;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume ()
	{
		return Math.min(0.05F * (float) this.getSlimeSize(), 0.3f);
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
	 * use in wolves.
	 */
	public int getVerticalFaceSpeed ()
	{
		return 0;
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
}
