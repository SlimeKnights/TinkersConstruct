package mods.tinker.tconstruct.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.crafting.Smeltery;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.modifiers.ModAttack;
import mods.tinker.tconstruct.skill.Skill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class TEventHandler
{
	Random random = new Random();

	/* Interact */

	@ForgeSubscribe
	public void interact (PlayerInteractEvent event)
	{
		if (event.action == Action.RIGHT_CLICK_BLOCK)// && !event.entityPlayer.worldObj.isRemote)
		{
			List<Skill> skills = TConstruct.playerTracker.getPlayerStats(event.entityPlayer.username).skillList;
			if (skills.size() > 0)
			{
				Skill walls = TConstruct.playerTracker.getPlayerStats(event.entityPlayer.username).skillList.get(0);
				walls.rightClickActivate(event.entityPlayer, event.entityPlayer.worldObj);
			}
		}
	}

	/*@ForgeSubscribe
	public void onHurt (LivingHurtEvent event)
	{
	    if (event.source instanceof EntityDamageSource && event.source.damageType.equals("explosion.player") && ((EntityDamageSource) event.source).getEntity() instanceof NitroCreeper)
	    {
	        if (event.entityLiving.worldObj.difficultySetting == 3)
	            event.ammount /= 2.3;
	        else
	            event.ammount /= 1.5;
	    }
	}*/

	/* Drops */
	@ForgeSubscribe
	public void onLivingDrop (LivingDropsEvent event)
	{
		if (random.nextInt(500) == 0 && event.entityLiving instanceof IMob && event.entityLiving.dimension == 0)
		{
			ItemStack dropStack = new ItemStack(TContent.heartCanister, 1, 1);
			EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
			entityitem.delayBeforeCanPickup = 10;
			event.drops.add(entityitem);
		}

		if (!event.entityLiving.isChild())
		{
			if (event.entityLiving.getClass() == EntityCow.class)
			{
				int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

				for (int iter = 0; iter < amount; ++iter)
				{
					ItemStack dropStack = new ItemStack(Item.leather, 1);
					EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
					entityitem.delayBeforeCanPickup = 10;
					event.drops.add(entityitem);
				}
			}

			else if (event.entityLiving.getClass() == EntityChicken.class)
			{
				int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

				for (int iter = 0; iter < amount; ++iter)
				{
					ItemStack dropStack = new ItemStack(Item.feather, 1);
					EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
					entityitem.delayBeforeCanPickup = 10;
					event.drops.add(entityitem);
				}
			}
		}

		if (event.recentlyHit && event.entityLiving.getClass() == EntitySkeleton.class)
		{
			EntitySkeleton skeleton = (EntitySkeleton) event.entityLiving;
			if (skeleton.getSkeletonType() == 1 && random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0)
			{
				ItemStack dropStack = new ItemStack(TContent.materials, 1, 8);
				EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
				entityitem.delayBeforeCanPickup = 10;
				event.drops.add(entityitem);
			}
		}

		else if (event.entityLiving.getClass() == EntityGhast.class)
		{
			ItemStack dropStack = new ItemStack(Item.ghastTear, 1);
			EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
			entityitem.delayBeforeCanPickup = 10;
			event.drops.add(entityitem);
		}
	}

	@ForgeSubscribe
	public void onLivingSpawn (EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityXPOrb)
		{
			System.out.println("Entity: " + event.entity);
		}
	}

	@ForgeSubscribe
	public void onLivingSpawn (LivingSpawnEvent.SpecialSpawn event)
	{
		if (event.entityLiving.getClass() == EntitySpider.class && random.nextInt(100) == 0)
		{
			EntityCreeper creeper = new EntityCreeper(event.entityLiving.worldObj);
			spawnEntity(event.entityLiving.posX, event.entityLiving.posY + 1, event.entityLiving.posZ, creeper, event.entityLiving.worldObj);
			creeper.mountEntity(event.entityLiving);
		}
	}

	public static void spawnEntity (double x, double y, double z, Entity entity, World world)
	{
		if (!world.isRemote)
		{
			entity.setPosition(x, y, z);
			((EntityLiving) entity).initCreature();
			world.spawnEntityInWorld(entity);
		}
	}

	/* Ore Dictionary */
	@ForgeSubscribe
	public void registerOre (OreRegisterEvent evt)
	{
		if (evt.Name == "battery")
			TConstruct.content.modE.batteries.add(evt.Ore);

		else if (evt.Name == "basicCircuit")
			TConstruct.content.modE.circuits.add(evt.Ore);

		else if (evt.Name == "plankWood")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Wood");
		}

		else if (evt.Name == "crystalQuartz")
		{
			ToolBuilder.instance.registerToolMod(new ModAttack("Quartz", new ItemStack[] { evt.Ore }, 11, 2));
		}

		else if (evt.Name == "crystalCerusQuartz")
		{
			ToolBuilder.instance.registerToolMod(new ModAttack("Quartz", new ItemStack[] { evt.Ore }, 11, 24));
		}

		//Ingots
		else if (evt.Name == "ingotCopper")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Copper");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 2));
		}

		else if (evt.Name == "ingotTin")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 5, 175, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 3));
		}

		else if (evt.Name == "ingotBronze")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Bronze");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 4, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 7));
		}

		else if (evt.Name == "ingotCobalt")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Cobalt");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 0, 650, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 5));
		}

		else if (evt.Name == "ingotArdite")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Ardite");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 1, 650, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 6));
		}

		else if (evt.Name == "ingotManyullyn")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Manyullyn");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 2, 750, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 9));
		}

		else if (evt.Name == "ingotNaturalAluminum")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 4));
		}

		else if (evt.Name == "naturalAluminum")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 4));
		}

		else if (evt.Name == "ingotAluminumBrass")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 7, 350, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 8));
		}

		else if (evt.Name == "ingotAlumite")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Alumite");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 8, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 10));
		}

		else if (evt.Name == "ingotSteel")
		{
			PatternBuilder.instance.registerMaterial(evt.Ore, 2, "Steel");
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 9, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 12));
		}

		//Ores
		else if (evt.Name == "oreCopper")
		{
			Smeltery.addMelting(evt.Ore, 550, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 2));
		}

		else if (evt.Name == "oreTin")
		{
			Smeltery.addMelting(evt.Ore, 275, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 3));
		}

		else if (evt.Name == "oreNaturalAluminum")
		{
			Smeltery.addMelting(evt.Ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 4));
		}

		else if (evt.Name == "oreCobalt")
		{
			Smeltery.addMelting(evt.Ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 5));
		}

		else if (evt.Name == "oreArdite")
		{
			Smeltery.addMelting(evt.Ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 6));
		}

		//Blocks
		else if (evt.Name == "blockCopper")
		{
			Smeltery.addMelting(evt.Ore, 550, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 2));
		}

		else if (evt.Name == "blockTin")
		{
			Smeltery.addMelting(evt.Ore, 275, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 3));
		}

		else if (evt.Name == "blockBronze")
		{
			Smeltery.addMelting(evt.Ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 7));
		}

		else if (evt.Name == "blockNaturalAluminum")
		{
			Smeltery.addMelting(evt.Ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 4));
		}

		else if (evt.Name == "blockCobalt")
		{
			Smeltery.addMelting(evt.Ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 5));
		}

		else if (evt.Name == "blockArdite")
		{
			Smeltery.addMelting(evt.Ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 6));
		}

		else if (evt.Name == "blockManyullyn")
		{
			Smeltery.addMelting(evt.Ore, 800, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 9));
		}

		else if (evt.Name == "blockAluminumBrass")
		{
			Smeltery.addMelting(evt.Ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 8));
		}

		else if (evt.Name == "blockAlumite")
		{
			Smeltery.addMelting(evt.Ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 10));
		}

		else if (evt.Name == "blockSteel")
		{
			Smeltery.addMelting(evt.Ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 12));
		}

		//Dust
		else if (evt.Name == "dustCopper")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 2));
		}

		else if (evt.Name == "dustTin")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 5, 175, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 3));
		}

		else if (evt.Name == "dustBronze")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 4, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 7));
		}

		else if (evt.Name == "dustCobalt")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 0, 650, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 5));
		}

		else if (evt.Name == "dustArdite")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 1, 650, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 6));
		}

		else if (evt.Name == "dustManyullyn")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 2, 750, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 9));
		}

		else if (evt.Name == "dustAluminum")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 4));
		}

		else if (evt.Name == "dustNaturalAluminum")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 4));
		}

		else if (evt.Name == "dustAluminumBrass")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 7, 350, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 8));
		}

		else if (evt.Name == "dustAlumite")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 8, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 10));
		}

		else if (evt.Name == "dustSteel")
		{
			Smeltery.addMelting(new ItemStack(evt.Ore.itemID, 2, evt.Ore.getItemDamage()), TContent.metalBlock.blockID, 9, 500, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue, 12));
		}
	}

	public void unfuxOreDictionary () //TODO: This isn't the best
	{
		ArrayList<ItemStack> ores = OreDictionary.getOres("ingotCopper");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Copper");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					2));
		}

		ores = OreDictionary.getOres("ingotTin");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 5, 175, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					3));
		}

		ores = OreDictionary.getOres("ingotBronze");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Bronze");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 4, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					7));
		}

		ores = OreDictionary.getOres("ingotCobalt");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Cobalt");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 0, 650, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					5));
		}

		ores = OreDictionary.getOres("ingotArdite");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Ardite");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 1, 650, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					6));
		}

		ores = OreDictionary.getOres("ingotManyullyn");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Manyullyn");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 2, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					9));
		}

		ores = OreDictionary.getOres("naturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					4));
		}

		ores = OreDictionary.getOres("ingotNaturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					4));
		}

		ores = OreDictionary.getOres("ingotAluminumBrass");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 7, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					8));
		}

		ores = OreDictionary.getOres("ingotAlumite");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Alumite");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 8, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					10));
		}

		ores = OreDictionary.getOres("ingotSteel");
		for (ItemStack ore : ores)
		{
			PatternBuilder.instance.registerMaterial(ore, 2, "Steel");
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 9, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					12));
		}

		ores = OreDictionary.getOres("oreCopper");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 550, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 2));
		}

		ores = OreDictionary.getOres("oreTin");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 275, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 3));
		}

		ores = OreDictionary.getOres("oreNaturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 4));
		}

		ores = OreDictionary.getOres("oreCobalt");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 5));
		}

		ores = OreDictionary.getOres("oreArdite");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 6));
		}

		ores = OreDictionary.getOres("blockCopper");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 550, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 2));
		}

		ores = OreDictionary.getOres("blockTin");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 275, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 3));
		}

		ores = OreDictionary.getOres("blockBronze");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 7));
		}

		ores = OreDictionary.getOres("blockNaturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 4));
		}

		ores = OreDictionary.getOres("blockCobalt");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 5));
		}

		ores = OreDictionary.getOres("blockArdite");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 6));
		}

		ores = OreDictionary.getOres("blockManyullyn");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 800, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 9));
		}

		ores = OreDictionary.getOres("blockAluminumBrass");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 8));
		}

		ores = OreDictionary.getOres("blockAlumite");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 10));
		}

		ores = OreDictionary.getOres("blockSteel");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(ore, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 9, 12));
		}

		ores = OreDictionary.getOres("dustCopper");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					2));
		}

		ores = OreDictionary.getOres("dustTin");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 5, 175, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					3));
		}

		ores = OreDictionary.getOres("dustBronze");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 4, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					7));
		}

		ores = OreDictionary.getOres("dustCobalt");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 0, 650, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					5));
		}

		ores = OreDictionary.getOres("dustArdite");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 1, 650, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					6));
		}

		ores = OreDictionary.getOres("dustManyullyn");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 2, 750, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					9));
		}

		ores = OreDictionary.getOres("dustNaturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					4));
		}

		ores = OreDictionary.getOres("dustAluminumBrass");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 7, 350, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					8));
		}

		ores = OreDictionary.getOres("dustAlumite");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 8, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					10));
		}

		ores = OreDictionary.getOres("dustSteel");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 9, 500, new LiquidStack(TContent.liquidMetalStill.blockID, TConstruct.ingotLiquidValue,
					12));
		}

		ores = OreDictionary.getOres("nuggetIron");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue / 9, 2));
		}

		ores = OreDictionary.getOres("nuggetCopper");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 3, 450, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue / 9, 2));
		}

		ores = OreDictionary.getOres("nuggetTin");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 5, 175, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue / 9, 3));
		}

		ores = OreDictionary.getOres("nuggetNaturalAluminum");
		for (ItemStack ore : ores)
		{
			Smeltery.addMelting(new ItemStack(ore.itemID, 2, ore.getItemDamage()), TContent.metalBlock.blockID, 6, 250, new LiquidStack(TContent.liquidMetalStill.blockID,
					TConstruct.ingotLiquidValue / 9, 4));
		}
	}

	@ForgeSubscribe
	public void bucketFill (FillBucketEvent evt)
	{
		if (evt.current.getItem() == Item.bucketEmpty && evt.target.typeOfHit == EnumMovingObjectType.TILE)
		{
			int hitX = evt.target.blockX;
			int hitY = evt.target.blockY;
			int hitZ = evt.target.blockZ;

			if (evt.entityPlayer != null && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current))
			{
				return;
			}

			int bID = evt.world.getBlockId(hitX, hitY, hitZ);
			int meta = -1;

			if (bID == TContent.liquidMetalStill.blockID)
			{
				LiquidTextureLogic logic = (LiquidTextureLogic) evt.world.getBlockTileEntity(hitX, hitY, hitZ);
				meta = logic.getLiquidType();
			}

			if (meta != -1)
			{
				evt.world.setBlockToAir(hitX, hitY, hitZ); //Set air block
				evt.result = new ItemStack(TContent.buckets, 1, meta);
				evt.setResult(Result.ALLOW);
			}
		}
	}
}
