package mods.tinker.tconstruct.entity;

import java.util.ArrayList;
import java.util.Random;

import mods.tinker.common.fancyitem.FancyEntityItem;
import mods.tinker.tconstruct.ai.CoreAI;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GolemBase extends EntityCreature
{
	public String creator;
	public int maxHealth;
	public int baseAttack;
	public boolean paused;
	float bodyHeight;
	float bodyWidth;
	int movementType;
	
	protected ItemStack[] inventory;
	protected FancyEntityItem coreItem;
	public ArrayList<CoreAI> aiList = new ArrayList<CoreAI>();
	
	public GolemBase(World world)
	{
		super(world);
		setInitialStats();
	}

	@Override
	public int getMaxHealth ()
	{
		return maxHealth;
	}
	
	protected void setInitialStats()
	{
		maxHealth = 20;
		baseAttack = 3;
		paused = false;
		inventory = new ItemStack[0];
	}
	
	/* AI */
	public void addCoreAI(CoreAI ai, boolean flag)
	{
		aiList.add(ai);
		//tier2key.onInit(this, flag);
	}
	
	protected void updateWanderPath ()
	{
		if (!paused)
			super.updateWanderPath();
	}
	
	protected void coreRoutine()
	{
		
	}
	
	protected void guardRoutine()
	{
		
	}
	
	protected void followRoutine()
	{
		
	}
	
	protected void protectRoutine()
	{
		
	}
	
	/* Types */
	public void setTraits()
	{
		
	}
	
	/* Other */
	protected boolean canDespawn ()
	{
		return false;
	}
	
	/* Effects */
	public void sparkle ()
	{
		Random random = worldObj.rand;
		double d = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
		double d1 = (float) posY + random.nextFloat() * (float) bodyHeight;
		double d2 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
		double d3 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
		double d4 = (float) posY + random.nextFloat() * (float) bodyHeight;
		double d5 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
		switch (0)//(state)
		{
		case 0:
			worldObj.spawnParticle("reddust", d, d1, d2, 0.0D, 1.0D, 0.0D);
			worldObj.spawnParticle("reddust", d3, d4, d5, 0.0D, 1.0D, 0.0D);
			break;

		case 1:
			worldObj.spawnParticle("reddust", d, d1, d2, -1D, 0.0D, 1.0D);
			worldObj.spawnParticle("reddust", d3, d4, d5, -1D, 0.0D, 1.0D);
			break;

		case 2:
			worldObj.spawnParticle("reddust", d, d1, d2, 1.0D, 0.0D, 0.0D);
			worldObj.spawnParticle("reddust", d3, d4, d5, 1.0D, 0.0D, 0.0D);
			break;

		case 3:
			worldObj.spawnParticle("reddust", d, d1, d2, 1.0D, 1.0D, 1.0D);
			worldObj.spawnParticle("reddust", d3, d4, d5, 1.0D, 1.0D, 1.0D);
			break;

		case 4:
			worldObj.spawnParticle("reddust", d, d1, d2, 0.46000000000000002D, 0.28999999999999998D, 0.19D);
			worldObj.spawnParticle("reddust", d3, d4, d5, 0.46000000000000002D, 0.28999999999999998D, 0.19D);
			break;
		}
	}

	public void sparkle (double d, double d1, double d2)
	{
		Random random = worldObj.rand;
		double d3 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
		double d4 = (float) posY + random.nextFloat() * (float) bodyHeight;
		double d5 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
		double d6 = (double) ((float) posX + random.nextFloat() * 2.0F) - 1.0D;
		double d7 = (float) posY + random.nextFloat() * (float) bodyHeight;
		double d8 = (double) ((float) posZ + random.nextFloat() * 2.0F) - 1.0D;
		worldObj.spawnParticle("reddust", d3, d4, d5, d, d1, d2);
		worldObj.spawnParticle("reddust", d6, d7, d8, d, d1, d2);
	}

	public void shineRadius (float f, double d, double d1, double d2)
	{
		shineRadius(f, d, d1, d2, 2, "reddust");
	}

	public void shineRadius (float f, double d, double d1, double d2, int i, String s)
	{
		d++;
		Random random = new Random();
		float f1 = 0.7F;
		float f2 = bodyHeight / 5;
		for (int j = 1; j <= 2; j++)
		{
			float f3 = f / (float) j;
			for (double d3 = 0.0D; d3 < 1.5707963D; d3 += 0.10000000000000001D)
			{
				float f4 = (float) ((double) f3 * Math.cos(d3));
				float f5 = (float) ((double) f3 * Math.sin(d3));
				for (int k = 0; k < i; k++)
				{
					worldObj.spawnParticle(s, ((float) posX + f4 + random.nextFloat() * f1) - 0.5F, (float) posY + f2, ((float) posZ + f5 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				}

				for (int l = 0; l < i; l++)
				{
					worldObj.spawnParticle(s, (((float) posX - f4) + random.nextFloat() * f1) - 0.5F, (float) posY + f2, ((float) posZ + f5 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				}

				for (int i1 = 0; i1 < i; i1++)
				{
					worldObj.spawnParticle(s, ((float) posX + f4 + random.nextFloat() * f1) - 0.5F, (float) posY + f2, (((float) posZ - f5) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				}

				for (int j1 = 0; j1 < i; j1++)
				{
					worldObj.spawnParticle(s, (((float) posX - f4) + random.nextFloat() * f1) - 0.5F, (float) posY + f2, (((float) posZ - f5) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				}
			}
		}
	}

	public void shineRadius (float f, double d, double d1, double d2, boolean flag)
	{
		d--;
		Random random = new Random();
		float f1 = 0.7F;
		float f2 = f;
		String s = "reddust";
		float f3 = (float) bodyHeight / 5F;
		for (double d3 = 0.0D; d3 < 1.5707963D; d3 += 0.20000000000000001D)
		{
			float f4 = (float) Math.sin(d3);
			float f5 = (float) Math.cos(d3);
			float f6 = f2 * f4;
			for (double d4 = 0.0D; d4 < 1.5707963D; d4 += 0.20000000000000001D)
			{
				float f7 = (float) Math.sin(d4);
				float f8 = (float) Math.cos(d4);
				float f9 = f2 * f8 * f5;
				float f10 = f2 * f7 * f5;
				worldObj.spawnParticle(s, ((float) posX + f9 + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1, ((float) posZ + f10 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				worldObj.spawnParticle(s, (((float) posX - f9) + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1, ((float) posZ + f10 + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				worldObj.spawnParticle(s, ((float) posX + f9 + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1, (((float) posZ - f10) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
				worldObj.spawnParticle(s, (((float) posX - f9) + random.nextFloat() * f1) - 0.5F, (float) posY + f3 + f6 + random.nextFloat() * f1, (((float) posZ - f10) + random.nextFloat() * f1) - 0.5F, d, d1, d2);
			}
		}
	}
}
