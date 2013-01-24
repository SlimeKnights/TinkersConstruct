package tinker.tconstruct;

import ic2.api.ElectricItem;
import ic2.api.ICustomElectricItem;
import ic2.api.IElectricItem;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import tinker.tconstruct.tools.ToolCore;
import cpw.mods.fml.client.FMLClientHandler;

public class AbilityHelper
{
	static Minecraft mc;

	/* Blocks */
	public static boolean onBlockChanged (ItemStack stack, World world, int bID, int x, int y, int z, EntityLiving player, Random random)
	{
		if (!stack.hasTagCompound())
			return false;

		int durability = 0;
		NBTTagCompound tags = stack.getTagCompound();

		if (tags.getCompoundTag("InfiTool").hasKey("Unbreaking"))
			durability = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");

		if (random.nextInt(10) < 10 - durability)
		{
			damageTool(stack, 1, tags, player, false);
		}

		return true;
	}

	/* Tool specific */
	public static void damageTool (ItemStack stack, int dam, EntityLiving entity, boolean ignoreCharge)
	{
		NBTTagCompound tags = stack.getTagCompound();
		damageTool(stack, dam, tags, entity, ignoreCharge);
	}

	public static void damageTool (ItemStack stack, int dam, NBTTagCompound tags, EntityLiving entity, boolean ignoreCharge)
	{
		if (ignoreCharge || !damageElectricTool(stack, tags, entity))
		{
			int damage = tags.getCompoundTag("InfiTool").getInteger("Damage");
			int damageTrue = damage + dam;
			//System.out.println("Damaging tool, damageTrue "+damageTrue+", ignoring charge: "+ignoreCharge);
			int maxDamage = tags.getCompoundTag("InfiTool").getInteger("TotalDurability");
			if (damage + dam <= 0)
			{
				return;
			}
			
			else if ((damage + dam) > maxDamage)
			{
				breakTool(stack, tags, entity);
				stack.setItemDamage(0);
			}

			else
			{
				tags.getCompoundTag("InfiTool").setInteger("Damage", damage + dam);
				int toolDamage = damage * 100 / maxDamage + 1;
				//System.out.println("Damage: " + damer);
				int stackDamage = stack.getItemDamage();
				//if (toolDamage >= stackDamage && toolDamage < 100)
					//stack.damageItem(toolDamage - stackDamage, entity);
				stack.setItemDamage(damage * 100 / maxDamage + 1);
			}

			//stack.setItemDamage(1 + (maxDamage - damage) * (stack.getMaxDamage() - 1) / maxDamage);
		}
	}

	public static boolean damageElectricTool (ItemStack stack, NBTTagCompound tags, Entity entity)
	{
		if (!tags.hasKey("charge"))
			return false;

		int charge = tags.getInteger("charge");
		int mineSpeed = tags.getCompoundTag("InfiTool").getInteger("MiningSpeed");
		if (tags.getCompoundTag("InfiTool").hasKey("MiningSpeed2"))
			mineSpeed = (mineSpeed + tags.getCompoundTag("InfiTool").getInteger("MiningSpeed2")) / 2;
		mineSpeed /= 15;

		if (charge < mineSpeed)
		{
			if (charge > 0)
				tags.setInteger("charge", 0);
			return false;
		}
		/*if (entity instanceof EntityPlayer && ElectricItem.use(stack, mineSpeed, (EntityPlayer) entity))
			return true;*/

		charge -= mineSpeed;
		ToolCore tool = (ToolCore) stack.getItem();
		stack.setItemDamage(1 + (tool.getMaxCharge() - charge) * (stack.getMaxDamage() - 1) / tool.getMaxCharge());
		tags.setInteger("charge", charge);
		if (entity instanceof EntityPlayer)
			chargeFromArmor(stack, (EntityPlayer) entity);
		return true;
	}

	static void chargeFromArmor (ItemStack stack, EntityPlayer player)
	{
		boolean inContainer = false;

		for (int armorIter = 0; armorIter < 4; ++armorIter)
		{
			ItemStack armor = player.inventory.armorInventory[armorIter];

			if (armor != null && armor.getItem() instanceof IElectricItem)
			{
				IElectricItem electricArmor = (IElectricItem) armor.getItem();
				ToolCore tool = (ToolCore) stack.getItem();

				if (electricArmor.canProvideEnergy() && electricArmor.getTier() >= ((IElectricItem) stack.getItem()).getTier())
				{
					int chargeAmount = tool.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
					chargeAmount = discharge(armor, chargeAmount, Integer.MAX_VALUE, true, false);

					if (chargeAmount > 0)
					{
						tool.charge(stack, chargeAmount, Integer.MAX_VALUE, true, false);
						inContainer = true;
					}
				}
			}
		}

		if (inContainer)
		{
			player.openContainer.detectAndSendChanges();
		}
	}

	public static int discharge (ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate)
	{
		IElectricItem ielectricitem = (IElectricItem) itemStack.getItem();

		if (ielectricitem instanceof ICustomElectricItem)
		{
			return ((ICustomElectricItem) ielectricitem).discharge(itemStack, amount, tier, ignoreTransferLimit, simulate);
		}
		else if (amount >= 0 && itemStack.stackSize <= 1 && ielectricitem.getTier() <= tier)
		{
			if (amount > ielectricitem.getTransferLimit() && !ignoreTransferLimit)
			{
				amount = ielectricitem.getTransferLimit();
			}

			NBTTagCompound tags = itemStack.getTagCompound();//StackUtil.getOrCreateNbtData(itemStack);
			int charge = tags.getInteger("charge");

			if (amount > charge)
			{
				amount = charge;
			}

			charge -= amount;

			if (!simulate)
			{
				tags.setInteger("charge", charge);
				itemStack.itemID = charge > 0 ? ielectricitem.getChargedItemId() : ielectricitem.getEmptyItemId();

				if (itemStack.getItem() instanceof IElectricItem)
				{
					ielectricitem = (IElectricItem) itemStack.getItem();

					if (itemStack.getMaxDamage() > 2)
					{
						itemStack.setItemDamage(1 + (ielectricitem.getMaxCharge() - charge) * (itemStack.getMaxDamage() - 2) / ielectricitem.getMaxCharge());
					}
					else
					{
						itemStack.setItemDamage(0);
					}
				}
				else
				{
					itemStack.setItemDamage(0);
				}
			}

			return amount;
		}
		else
		{
			return 0;
		}

	}

	public static void breakTool (ItemStack stack, NBTTagCompound tags, Entity player)
	{
		tags.getCompoundTag("InfiTool").setBoolean("Broken", true);
		player.worldObj.playSound(player.posX, player.posY, player.posZ, "random.break", 1f, 1f, true);
	}

	public static void repairTool (ItemStack stack, NBTTagCompound tags)
	{
		tags.getCompoundTag("InfiTool").setBoolean("Broken", false);
		tags.getCompoundTag("InfiTool").setInteger("Damage", 0);
	}

	/* Entities */

	public static void hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player)
	{
		hitEntity(stack, mob, player, 1f);
	}

	public static void hitEntity (ItemStack stack, EntityLiving mob, EntityLiving player, float bonusDamage)
	{
		NBTTagCompound tags = stack.getTagCompound();
		if (!tags.getCompoundTag("InfiTool").getBoolean("Broken"))
		{
			int durability = tags.getCompoundTag("InfiTool").getInteger("Damage");

			float shoddy = tags.getCompoundTag("InfiTool").getFloat("Shoddy");
			float damageModifier = -shoddy * durability / 100f;

			int attack = (int) ((tags.getCompoundTag("InfiTool").getInteger("Attack") + damageModifier) * bonusDamage);

			if (player instanceof EntityPlayer)
				if (mob.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), attack))
				{
					damageTool(stack, 1, tags, player, false);
					if (tags.getCompoundTag("InfiTool").getBoolean("Necrotic"))
						player.heal(1);
				}
			else
				mob.attackEntityFrom(DamageSource.causeMobDamage(player), attack);
			
			if (tags.getCompoundTag("InfiTool").hasKey("Fiery"))
			{
				System.out.println("Fiery: "+tags.getInteger("Fiery"));
				mob.setFire(tags.getCompoundTag("InfiTool").getInteger("Fiery")/5+1);
			}
		}
	}

	public static DamageSource causePiercingDamage (EntityLiving mob)
	{
		return new PiercingEntityDamage("mob", mob);
	}

	public static DamageSource causePlayerPiercingDamage (EntityPlayer player)
	{
		return new PiercingEntityDamage("player", player);
	}

	public static void thrust (ItemStack stack, World world, EntityPlayer player)
	{
		if (mc == null)
			mc = FMLClientHandler.instance().getClient();

		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY)
		{
			mc.playerController.attackEntity(player, mc.objectMouseOver.entityHit);
			mc.playerController.attackEntity(player, mc.objectMouseOver.entityHit);
			mc.playerController.attackEntity(player, mc.objectMouseOver.entityHit);
		}
	}

	public static void knockbackEntity (EntityLiving living, double boost)
	{
		living.motionX *= boost;
		//living.motionY *= boost/2;
		living.motionZ *= boost;
	}

	public static boolean hoeGround (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, Random random)
	{
		if (!player.canPlayerEdit(x, y, z, side, stack))
		{
			return false;
		}
		else
		{
			UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return false;
			}

			if (event.getResult() == Result.ALLOW)
			{
				onBlockChanged(stack, world, 0, x, y, z, player, random);
				return true;
			}

			int bID = world.getBlockId(x, y, z);
			int bIDabove = world.getBlockId(x, y + 1, z);

			if ((side == 0 || bIDabove != 0 || bID != Block.grass.blockID) && bID != Block.dirt.blockID)
			{
				return false;
			}
			else
			{
				Block block = Block.tilledField;
				world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

				if (world.isRemote)
				{
					return true;
				}
				else
				{
					world.setBlockWithNotify(x, y, z, block.blockID);
					onBlockChanged(stack, world, 0, x, y, z, player, random);
					return true;
				}
			}
		}
	}
}
