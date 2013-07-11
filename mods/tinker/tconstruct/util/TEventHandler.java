package mods.tinker.tconstruct.util;

import java.util.ArrayList;
import java.util.Random;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.crystal.TheftValueTracker;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.crafting.Smeltery;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.library.event.PartBuilderEvent;
import mods.tinker.tconstruct.library.event.ToolCraftEvent;
import mods.tinker.tconstruct.library.tools.ArrowMaterial;
import mods.tinker.tconstruct.library.tools.BowMaterial;
import mods.tinker.tconstruct.library.tools.BowstringMaterial;
import mods.tinker.tconstruct.library.tools.FletchingMaterial;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.util.ValueCoordTuple;
import mods.tinker.tconstruct.modifiers.ModAttack;
import mods.tinker.tconstruct.util.player.TPlayerStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class TEventHandler
{
    Random random = new Random();

    /* Crafting */
    @ForgeSubscribe
    public void craftTool (ToolCraftEvent.NormalTool event)
    {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        int thaum = 0;
        if (toolTag.getInteger("Head") == 31)
            thaum++;
        if (toolTag.getInteger("Handle") == 31)
            thaum++;
        if (toolTag.getInteger("Accessory") == 31)
            thaum++;
        if (toolTag.getInteger("Extra") == 31)
            thaum++;

        if ((thaum >= 3) || (!toolTag.hasKey("Accessory") && thaum >= 2))
        {
            int modifiers = toolTag.getInteger("Modifiers");
            modifiers += 2;
            toolTag.setInteger("Modifiers", modifiers);
        }
        else if (thaum >= 1)
        {
            int modifiers = toolTag.getInteger("Modifiers");
            modifiers += 1;
            toolTag.setInteger("Modifiers", modifiers);
        }

        if (event.tool == TContent.shortbow)
        {
            BowMaterial top = TConstructRegistry.getBowMaterial(toolTag.getInteger("Head"));
            BowMaterial bottom = TConstructRegistry.getBowMaterial(toolTag.getInteger("Accessory"));
            BowstringMaterial string = (BowstringMaterial) TConstructRegistry.getCustomMaterial(toolTag.getInteger("Handle"), BowstringMaterial.class);

            if (top != null && bottom != null && string != null)
            {
                if (toolTag.getInteger("Handle") == 1)
                {
                    int modifiers = toolTag.getInteger("Modifiers");
                    modifiers += 1;
                    toolTag.setInteger("Modifiers", modifiers);
                }

                int durability = (int) ((top.durability + bottom.durability) / 2 * string.durabilityModifier);
                toolTag.setInteger("TotalDurability", durability);
                toolTag.setInteger("BaseDurability", durability);

                int drawSpeed = (int) ((top.drawspeed + bottom.drawspeed) / 2 * string.drawspeedModifier);
                toolTag.setInteger("DrawSpeed", drawSpeed);
                toolTag.setInteger("BaseDrawSpeed", drawSpeed);

                float flightSpeed = (top.flightSpeedMax + bottom.flightSpeedMax) / 2f * string.flightSpeedModifier;
                toolTag.setFloat("FlightSpeed", flightSpeed);
            }
        }
        
        if (event.tool == TContent.arrow)
        {
            ArrowMaterial head = TConstructRegistry.getArrowMaterial(toolTag.getInteger("Head"));
            ArrowMaterial shaft = TConstructRegistry.getArrowMaterial(toolTag.getInteger("Handle"));
            FletchingMaterial fletching = (FletchingMaterial) TConstructRegistry.getCustomMaterial(toolTag.getInteger("Accessory"), FletchingMaterial.class);

            if (head != null && shaft != null && fletching != null)
            {
                float mass = head.mass / 5f + shaft.mass + fletching.mass;
                float shatter = (head.breakChance + shaft.breakChance + fletching.breakChance) / 4f;
                float accuracy = (head.accuracy + shaft.accuracy + fletching.accuracy) / 3;
                
                ItemStack arrow = new ItemStack(event.tool, 4);
                toolTag.setInteger("TotalDurability", 0);
                toolTag.setFloat("Mass", mass);
                toolTag.setFloat("BreakChance", shatter);
                toolTag.setFloat("Accuracy", accuracy);
                arrow.setTagCompound(event.toolTag);
                event.overrideResult(arrow);
            }
        }
    }

    @ForgeSubscribe
    public void craftPart (PartBuilderEvent.NormalPart event)
    {
        if (event.pattern.getItem() == TContent.woodPattern && event.pattern.getItemDamage() == 23)
        {
            ItemStack result = craftBowString(event.material);
            if (result != null)
            {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }
        
        if (event.pattern.getItem() == TContent.woodPattern && event.pattern.getItemDamage() == 24)
        {
            ItemStack result = craftFletching(event.material);
            if (result != null)
            {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }
    }
    
    public static ItemStack craftBowString(ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;
        
        BowstringMaterial mat = (BowstringMaterial) TConstructRegistry.getCustomMaterial(stack, BowstringMaterial.class);
        if (mat != null)
            return mat.craftingItem.copy();
        return null;
    }
    
    public static ItemStack craftFletching(ItemStack stack)
    {        
        FletchingMaterial mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchingMaterial.class);
        if (mat != null)
            return mat.craftingItem.copy();
        return null;
    }

    /* Interact */

    /*@ForgeSubscribe
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
    }*/

    /* Damage */
    /*@ForgeSubscribe
    public void onHurt (LivingHurtEvent event)
    {*/
    /*if (event.entityLiving instanceof EntityPlayer)
    {
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack stack = player.getItemInUse();
        if (stack != null && stack.getItem() == TContent.cutlass)
        {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 3*20, 1));
        }
    }*/
    /*if (event.source instanceof EntityDamageSource && event.source.damageType.equals("explosion.player") && ((EntityDamageSource) event.source).getEntity() instanceof NitroCreeper)
    {
        if (event.entityLiving.worldObj.difficultySetting == 3)
            event.ammount /= 2.3;
        else
            event.ammount /= 1.5;
    }*/
    //}

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

        if (event.entityLiving instanceof EntityWither && random.nextInt(5) == 0)
        {
            ItemStack dropStack = new ItemStack(TContent.heartCanister, 1, 1);
            EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
            entityitem.delayBeforeCanPickup = 10;
            event.drops.add(entityitem);
        }

        //if (event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
        //{
        if (!event.entityLiving.isChild())
        {
            if (event.entityLiving.getClass() == EntityCow.class)
            {
                int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

                for (int iter = 0; iter < amount; ++iter)
                {
                    addDrops(event, new ItemStack(Item.leather, 1));
                }
            }

            else if (event.entityLiving.getClass() == EntityChicken.class)
            {
                int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

                for (int iter = 0; iter < amount; ++iter)
                {
                    addDrops(event, new ItemStack(Item.feather, 1));
                }
            }
        }

        if (event.recentlyHit)
        {
            if (event.entityLiving.getClass() == EntitySkeleton.class)
            {
                EntitySkeleton enemy = (EntitySkeleton) event.entityLiving;

                if (event.source.damageType.equals("player"))
                {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                    {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TContent.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 10)
                        {
                            addDrops(event, new ItemStack(Item.skull.itemID, 1, enemy.getSkeletonType()));
                        }
                    }
                }
                if (enemy.getSkeletonType() == 1 && random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0)
                {
                    addDrops(event, new ItemStack(TContent.materials, 1, 8));
                }
            }

            if (event.entityLiving.getClass() == EntityZombie.class)
            {
                EntityZombie enemy = (EntityZombie) event.entityLiving;

                if (event.source.damageType.equals("player"))
                {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();

                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                    {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TContent.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 10)
                        {
                            addDrops(event, new ItemStack(Item.skull.itemID, 1, 2));
                        }
                    }
                    /*if (stack.getItem() == TContent.breakerBlade && random.nextInt(100) < 10) //Swap out for real beheading
                    {
                        addDrops(event, new ItemStack(Item.skull.itemID, 1, 2));
                    }*/
                }
            }

            if (event.entityLiving.getClass() == EntityCreeper.class)
            {
                EntityCreeper enemy = (EntityCreeper) event.entityLiving;

                if (event.source.damageType.equals("player"))
                {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                    {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TContent.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 5)
                        {
                            addDrops(event, new ItemStack(Item.skull.itemID, 1, 4));
                        }
                    }
                }
            }
        }

        if (event.entityLiving.getClass() == EntityGhast.class)
        {
            if (PHConstruct.uhcGhastDrops)
            {
                for (EntityItem o : event.drops)
                {
                    if (o.getEntityItem().itemID == Item.ghastTear.itemID)
                    {
                        o.setEntityItemStack(new ItemStack(Item.ingotGold, 1));
                    }
                }
            }
            else
            {
                addDrops(event, new ItemStack(Item.ghastTear, 1));
            }
        }
        //}

        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (PHConstruct.dropPlayerHeads)
            {
                ItemStack dropStack = new ItemStack(Item.skull.itemID, 1, 3);
                NBTTagCompound nametag = new NBTTagCompound();
                nametag.setString("SkullOwner", player.username);
                addDrops(event, dropStack);
            }

            else if (event.source.damageType.equals("player"))
            {
                EntityPlayer source = (EntityPlayer) event.source.getEntity();
                ItemStack stack = source.getCurrentEquippedItem();
                if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                {
                    int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                    if (stack.getItem() == TContent.cleaver)
                        beheading += 2;
                    if (beheading > 0 && random.nextInt(100) < beheading * 50)
                    {
                        ItemStack dropStack = new ItemStack(Item.skull.itemID, 1, 3);
                        NBTTagCompound nametag = new NBTTagCompound();
                        nametag.setString("SkullOwner", player.username);
                        addDrops(event, dropStack);
                    }
                }
            }

            /*if (!player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
                stats.armor.dropItems();
                stats.knapsack.dropItems();
            }*/
        }
    }

    @ForgeSubscribe
    public void onLivingDeath (LivingDeathEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            if (!player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
                stats.armor.dropItems();
                stats.knapsack.dropItems();
            }
        }
    }

    void addDrops (LivingDropsEvent event, ItemStack dropStack)
    {
        EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
        entityitem.delayBeforeCanPickup = 10;
        event.drops.add(entityitem);
    }

    /*@ForgeSubscribe
    public void onLivingSpawn (EntityJoinWorldEvent event)
    {
    	if (event.entity instanceof EntityXPOrb)
    	{
    		System.out.println("Entity: " + event.entity);
    	}
    }*/

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

    /* Chunks */
    @ForgeSubscribe
    public void chunkDataLoad (ChunkDataEvent.Load event)
    {
        Chunk chunk = event.getChunk();
        int worldID = chunk.worldObj.provider.dimensionId;
        ValueCoordTuple coord = new ValueCoordTuple(worldID, chunk.xPosition, chunk.zPosition);
        TheftValueTracker.crystallinity.put(coord, event.getData().getInteger("TConstruct.Crystallinity"));
    }

    @ForgeSubscribe
    public void chunkDataSave (ChunkDataEvent.Save event)
    {
        Chunk chunk = event.getChunk();
        int worldID = chunk.worldObj.provider.dimensionId;
        ValueCoordTuple coord = new ValueCoordTuple(worldID, chunk.xPosition, chunk.zPosition);
        if (TheftValueTracker.crystallinity.containsKey(coord))
        {
            int crystal = TheftValueTracker.crystallinity.get(coord);
            event.getData().setInteger("TConstruct.Crystallinity", crystal);
            if (!event.getChunk().isChunkLoaded)
            {
                TheftValueTracker.crystallinity.remove(worldID);
            }
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
