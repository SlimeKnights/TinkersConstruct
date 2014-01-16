package tconstruct.util;

import tconstruct.util.player.ArmorExtended;
import mantle.blocks.BlockUtils;
import mantle.world.WorldHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tconstruct.TConstruct;
import tconstruct.achievements.TAchievements;
import tconstruct.blocks.LiquidMetalFinite;
import tconstruct.blocks.TankAirBlock;
import tconstruct.common.TRepo;
import tconstruct.items.tools.FryingPan;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.event.PartBuilderEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.Weapon;
import tconstruct.modifiers.tools.ModAttack;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.player.TPlayerStats;

public class TEventHandler
{
    Random random = new Random();

    /* Crafting */
    @SubscribeEvent
    public void craftTool (ToolCraftEvent.NormalTool event)
    {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        if (PHConstruct.denyMattock && event.tool == TRepo.mattock)
        {
            int head = toolTag.getInteger("Head");
            int handle = toolTag.getInteger("Handle");
            int accessory = toolTag.getInteger("Accessory");

            if (!allowCrafting(head, handle, accessory))
            {
                event.setResult(Result.DENY);
                return;
            }
        }

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

        if (event.tool == TRepo.shortbow)
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

        if (event.tool == TRepo.arrow)
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
                toolTag.setInteger("Unbreaking", 10);
                arrow.setTagCompound(event.toolTag);
                event.overrideResult(arrow);
            }
        }
    }

    private boolean allowCrafting (int head, int handle, int accessory)
    {
        int[] nonMetals = { 0, 1, 3, 4, 5, 6, 7, 8, 9, 17 };
        for (int i = 0; i < nonMetals.length; i++)
        {
            if (head == nonMetals[i] || handle == nonMetals[i] || accessory == nonMetals[i])
                return false;
        }
        return true;
    }

    @SubscribeEvent
    public void craftPart (PartBuilderEvent.NormalPart event)
    {
        if (event.pattern.getItem() == TRepo.woodPattern && event.pattern.getItemDamage() == 23)
        {
            ItemStack result = craftBowString(event.material);
            if (result != null)
            {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }

        if (event.pattern.getItem() == TRepo.woodPattern && event.pattern.getItemDamage() == 24)
        {
            ItemStack result = craftFletching(event.material);
            if (result != null)
            {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }
    }

    public static ItemStack craftBowString (ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;

        BowstringMaterial mat = (BowstringMaterial) TConstructRegistry.getCustomMaterial(stack, BowstringMaterial.class);
        if (mat != null)
            return mat.craftingItem.copy();
        return null;
    }

    public static ItemStack craftFletching (ItemStack stack)
    {
        if (matchesLeaves(stack))
        {
            FletchingMaterial leaves = (FletchingMaterial) TConstructRegistry.getCustomMaterial(new ItemStack(Blocks.leaves), FletchingMaterial.class);
            return leaves.craftingItem.copy();
        }

        FletchingMaterial mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchingMaterial.class);
        if (mat != null)
            return mat.craftingItem.copy();
        return null;
    }

    public static boolean matchesLeaves (ItemStack stack)
    {
        Block block = BlockUtils.getBlockFromItem(stack.getItem());
        if (block != null)
        {
            if (block.isLeaves(null, 0, 0, 0))
                return true;
        }
        return false;
    }

    /* Damage */
    @SubscribeEvent
    public void onHurt (LivingHurtEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            //Cutlass
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == TRepo.cutlass && player.isUsingItem())
            {
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 3 * 20, 1));
            }
        }
    }

    /* Drops */
    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.source.getEntity();
            if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof FryingPan)
            {
                for (int i = 0; i < event.drops.size(); i++)
                {
                    ItemStack is = event.drops.get(i).getEntityItem();
                    if (FurnaceRecipes.smelting().func_151395_a(is) != null && FurnaceRecipes.smelting().func_151395_a(is).getItem() instanceof ItemFood)
                    {
                        NBTTagCompound stackCompound = is.getTagCompound();
                        if (stackCompound == null)
                        {
                            stackCompound = new NBTTagCompound();
                        }
                        stackCompound.setBoolean("frypanKill", true);
                        is.setTagCompound(stackCompound);
                    }
                }
            }
        }

        if (random.nextInt(500) == 0 && event.entityLiving instanceof IMob)
        {
            ItemStack dropStack = new ItemStack(TRepo.heartCanister, 1, 1);
            EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
            entityitem.field_145804_b = 10;
            event.drops.add(entityitem);
        }

        if (event.entityLiving instanceof EntityWither && random.nextInt(5) == 0)
        {
            ItemStack dropStack = new ItemStack(TRepo.heartCanister, 1, 1);
            EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
            entityitem.field_145804_b = 10;
            event.drops.add(entityitem);
        }

        //if (event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
        //{
        if (!event.entityLiving.isChild())
        {
            /*if (event.entityLiving.getClass() == EntityCow.class)
            {
                int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

                for (int iter = 0; iter < amount; ++iter)
                {
                    addDrops(event, new ItemStack(Item.leather, 1));
                }
            }*/

            if (event.entityLiving.getClass() == EntityChicken.class)
            {
                int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;

                for (int iter = 0; iter < amount; ++iter)
                {
                    addDrops(event, new ItemStack(Items.feather, 1));
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
                        if (stack.getItem() == TRepo.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 10)
                        {
                            addDrops(event, new ItemStack(Items.skull, 1, enemy.getSkeletonType()));
                        }
                    }
                }
                if (enemy.getSkeletonType() == 1 && random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0)
                {
                    addDrops(event, new ItemStack(TRepo.materials, 1, 8));
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
                        if (stack.getItem() == TRepo.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 10)
                        {
                            addDrops(event, new ItemStack(Items.skull, 1, 2));
                        }
                    }
                    /*if (stack.getItem() == TRepo.breakerBlade && random.nextInt(100) < 10) //Swap out for real beheading
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
                        if (stack.getItem() == TRepo.cleaver)
                            beheading += 2;
                        if (beheading > 0 && random.nextInt(100) < beheading * 5)
                        {
                            addDrops(event, new ItemStack(Items.skull, 1, 4));
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
                    if (o.getEntityItem().getItem() == Items.ghast_tear)
                    {
                        o.setEntityItemStack(new ItemStack(Items.gold_ingot, 1));
                    }
                }
            }
            else
            {
                addDrops(event, new ItemStack(Items.ghast_tear, 1));
            }
        }
        //}

        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (PHConstruct.dropPlayerHeads)
            {
                ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                NBTTagCompound nametag = new NBTTagCompound();
                nametag.setString("SkullOwner", player.getDisplayName());
                addDrops(event, dropStack);
            }

            else if (event.source.damageType.equals("player"))
            {
                EntityPlayer source = (EntityPlayer) event.source.getEntity();
                ItemStack stack = source.getCurrentEquippedItem();
                if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                {
                    int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                    if (stack.getItem() == TRepo.cleaver)
                        beheading += 2;
                    if (beheading > 0 && random.nextInt(100) < beheading * 50)
                    {
                        ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                        NBTTagCompound nametag = new NBTTagCompound();
                        nametag.setString("SkullOwner", player.getDisplayName());
                        addDrops(event, dropStack);
                    }
                }
            }

            GameRules rules = player.worldObj.getGameRules(); //Player is null if this crashes
            if (rules == null || !rules.getGameRuleBooleanValue("keepInventory"))
            {
                TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
                if (stats != null)
                {
                    stats.armor.dropItems();
                    stats.knapsack.dropItems();
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath (LivingDeathEvent event)
    {
        Entity cause = event.source.getSourceOfDamage();
        if (cause != null && cause instanceof EntityPlayer)
        {
            EntityPlayer murderer = (EntityPlayer) cause;
            ItemStack stack = murderer.getHeldItem();
            if (stack != null && stack.getItem() instanceof Weapon)
            {
                murderer.addStat(TAchievements.achievements.get("tconstruct.enemySlayer"), 1);
            }
        }
    }

    void addDrops (LivingDropsEvent event, ItemStack dropStack)
    {
        EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
        entityitem.field_145804_b = 10;
        event.drops.add(entityitem);
    }

    /*@ForgeSubscribe
    public void onLivingSpawn (EntityJoinWorldEvent event)
    {
    	if (event.entity instanceof EntityXPOrb)
    	{
    		TConstruct.logger.info("Entity: " + event.entity);
    	}
    }*/

    @SubscribeEvent
    public void onLivingSpawn (LivingSpawnEvent.SpecialSpawn event)
    {
        EntityLivingBase living = event.entityLiving;
        if (living.getClass() == EntitySpider.class && random.nextInt(100) == 0)
        {
            EntityCreeper creeper = new EntityCreeper(living.worldObj);
            spawnEntityLiving(living.posX, living.posY + 1, living.posZ, creeper, living.worldObj);
            if (living.riddenByEntity != null)
                creeper.mountEntity(living.riddenByEntity);
            else
                creeper.mountEntity(living);

            EntityXPOrb orb = new EntityXPOrb(living.worldObj, living.posX, living.posY, living.posZ, random.nextInt(20) + 20);
            orb.mountEntity(creeper);
        }
    }

    public static void spawnEntityLiving (double x, double y, double z, EntityLiving entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }

    /* Bonemeal */

    @SubscribeEvent
    public void bonemealEvent (BonemealEvent event)
    {
        if (!event.world.isRemote)
        {
            if (event.block == TRepo.slimeSapling)
            {
                if (TRepo.slimeSapling.boneFertilize(event.world, event.x, event.y, event.z, event.world.rand, event.entityPlayer))
                    event.setResult(Event.Result.ALLOW);
            }
        }
    }

    /* Ore Dictionary */
    @SubscribeEvent
    public void registerOre (OreRegisterEvent evt)
    {

        if (evt.Name == "crystalQuartz")
        {
            TRepo.modAttack.addStackToMatchList(evt.Ore, 2);
        }

        else if (evt.Name == "crystalCerusQuartz")
        {
            TRepo.modAttack.addStackToMatchList(evt.Ore, 24);
        }
    }

    @SubscribeEvent
    public void bucketFill (FillBucketEvent evt)
    {
        if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectType.ENTITY)
        {
            int hitX = evt.target.blockX;
            int hitY = evt.target.blockY;
            int hitZ = evt.target.blockZ;

            if (evt.entityPlayer != null && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current))
            {
                return;
            }

            Block bID = evt.world.func_147439_a(hitX, hitY, hitZ);
            for (int id = 0; id < TRepo.fluidBlocks.length; id++)
            {
                if (bID == TRepo.fluidBlocks[id])
                {
                    if (evt.entityPlayer.capabilities.isCreativeMode)
                    {
                        WorldHelper.setBlockToAir(evt.world,hitX, hitY, hitZ);
                    }
                    else
                    {
                        if (TRepo.fluidBlocks[id] instanceof LiquidMetalFinite)
                        {
                            WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                            /*int quanta = 0;
                            for (int posX = -1; posX <= 1; posX++)
                            {
                                for (int posZ = -1; posZ <= 1; posZ++)
                                {
                                    int localID = evt.world.getBlockId(hitX + posX, hitY, hitZ + posZ);
                                    if (localID == bID)
                                    {
                                        quanta += evt.world.getBlockMetadata(hitX + posX, hitY, hitZ + posZ) + 1;
                                    }
                                }
                            }

                            if (quanta >= 8)
                            {
                                while (quanta > 0)
                                {
                                    for (int posX = -1; posX <= 1; posX++)
                                    {
                                        for (int posZ = -1; posZ <= 1; posZ++)
                                        {
                                            int localID = evt.world.getBlockId(hitX + posX, hitY, hitZ + posZ);
                                            if (localID == bID)
                                            {
                                                quanta -= 1;
                                                int meta = evt.world.getBlockMetadata(hitX + posX, hitY, hitZ + posZ);
                                                if (meta > 0)
                                                    evt.world.setBlockMetadataWithNotify(hitX + posX, hitY, hitZ + posZ, meta - 1, 3);
                                                else
                                                    evt.world.setBlockToAir(hitX + posX, hitY, hitZ + posZ);
                                            }
                                        }
                                    }
                                }
                            }*/
                        }
                        else
                        {
                            WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                        }

                        evt.setResult(Result.ALLOW);
                        evt.result = new ItemStack(TRepo.buckets, 1, id);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void livingUpdate (LivingUpdateEvent event)
    {
    	if(event.entityLiving instanceof EntityPlayer){
    		EntityPlayer player = (EntityPlayer) event.entityLiving;
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getDisplayName());
            
            if(stats != null){
            	ArmorExtended armor = stats.armor;
            	for(int i = 0; i < armor.getSizeInventory(); i++){
            		if(armor.getStackInSlot(i) != null){
            			armor.getStackInSlot(i).getItem().onUpdate(armor.getStackInSlot(i), player.worldObj, player, i, false);
            			armor.getStackInSlot(i).getItem().onArmorTick(player.worldObj, player, armor.getStackInSlot(i));
            		}
            	}
            }
    	}
    }
    
    //Player interact event - prevent breaking of tank air blocks in creative
    @SubscribeEvent
    public void playerInteract (PlayerInteractEvent event)
    {
        if (event.action == Action.LEFT_CLICK_BLOCK)
        {
            Block block = event.entity.worldObj.func_147439_a(event.x, event.y, event.z);
            if (block instanceof TankAirBlock)
            {
                event.setCanceled(true);
            }
        }
    }

}
