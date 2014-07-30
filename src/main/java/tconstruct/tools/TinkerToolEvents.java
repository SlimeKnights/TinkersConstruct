package tconstruct.tools;

import mantle.blocks.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tconstruct.TConstruct;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.event.PartBuilderEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class TinkerToolEvents
{
    @SubscribeEvent
    public void onCrafting (ItemCraftedEvent event)
    {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote)
        {
            if (item == Item.getItemFromBlock(TinkerTools.toolStationWood))
            {
                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.materialManual)
                {
                    stats.materialManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkerTools.manualBook, 1, 1));
                }
            }
        }
    }
    
    @SubscribeEvent
    public void craftTool (ToolCraftEvent.NormalTool event)
    {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        if (PHConstruct.denyMattock && event.tool == TinkerTools.mattock)
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

        if (event.tool == TinkerTools.shortbow)
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

        if (event.tool == TinkerTools.arrow)
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

        if (event.tool == TinkerTools.battlesign)
        {
            int modifiers = toolTag.getInteger("Modifiers");
            modifiers += 1;
            toolTag.setInteger("Modifiers", modifiers);
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
        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getItemDamage() == 23)
        {
            ItemStack result = craftBowString(event.material);
            if (result != null)
            {
                event.overrideResult(new ItemStack[] { result, null });
            }
        }

        if (event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getItemDamage() == 24)
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
        if (stack != null)
        {
            Block block = BlockUtils.getBlockFromItemStack(stack);
            if (block != null)
            {
                if (block.isLeaves(null, 0, 0, 0))
                    return true;
            }
        }
        return false;
    }
    


    @SubscribeEvent
    public void onAttack (LivingAttackEvent event)
    {
        //System.out.println("Damage: "+event.ammount);
        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            //Cutlass
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == TinkerTools.battlesign && player.isUsingItem())
            {
                DamageSource source = event.source;
                if (!source.isUnblockable() && !source.isMagicDamage() && !source.isExplosion())
                {
                    if (source instanceof EntityDamageSourceIndirect)
                    {
                        if (TConstruct.random.nextInt(3) == 0)
                        {
                            Entity attacker = source.getEntity();
                            Entity projectile = ((EntityDamageSourceIndirect) source).getSourceOfDamage();
                            projectile.motionX *= -1;
                            projectile.motionZ *= -1;
                            projectile.setDead();
                            event.setCanceled(true);

                            if (projectile.getClass() == EntityArrow.class && !player.worldObj.isRemote)
                            {
                                EntityArrow reflection = null;
                                if (attacker instanceof EntityLivingBase)
                                    reflection = new EntityArrow(player.worldObj, (EntityLivingBase) attacker, 0);
                                else
                                    reflection = new EntityArrow(player.worldObj, player, 0);

                                Vec3 look = player.getLookVec();
                                reflection.posX = projectile.posX;
                                reflection.posY = projectile.posY;
                                reflection.posZ = projectile.posZ;
                                reflection.motionX = (projectile.motionX + (look.xCoord * 8)) / 6;
                                reflection.motionY = (projectile.motionY + (look.yCoord * 8)) / 6;
                                reflection.motionZ = (projectile.motionZ + (look.zCoord * 8)) / 6;
                                reflection.damage = ((EntityArrow) projectile).damage;
                                player.worldObj.spawnEntityInWorld(reflection);
                            }
                        }
                    }
                    else
                    {
                        Entity attacker = source.getEntity();
                        if(attacker != null)
                        {
                            attacker.attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount);
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

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
                        if (stack.getItem() == TinkerTools.cleaver)
                            beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10)
                        {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, enemy.getSkeletonType()));
                        }
                    }
                }
                if (enemy.getSkeletonType() == 1 && TConstruct.random.nextInt(Math.max(1, 5 - event.lootingLevel)) == 0)
                {
                    ItemHelper.addDrops(event, new ItemStack(TinkerTools.materials, 1, 8));
                }
            }

            if (event.entityLiving.getClass() == EntityZombie.class)
            {
                if (event.source.damageType.equals("player"))
                {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();

                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                    {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack != null && stack.hasTagCompound() && stack.getItem() == TinkerTools.cleaver)
                            beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 10)
                        {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                        }
                    }

                    if (stack != null && stack.hasTagCompound() && stack.getItem() == TinkerTools.cleaver && TConstruct.random.nextInt(100) < 10)
                    {
                        ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 2));
                    }

                }
            }

            if (event.entityLiving.getClass() == EntityCreeper.class)
            {
                if (event.source.damageType.equals("player"))
                {
                    EntityPlayer player = (EntityPlayer) event.source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                    {
                        int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                        if (stack.getItem() == TinkerTools.cleaver)
                            beheading += 2;
                        if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 5)
                        {
                            ItemHelper.addDrops(event, new ItemStack(Items.skull, 1, 4));
                        }
                    }
                }
            }
        }

        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (PHConstruct.dropPlayerHeads)
            {
                ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                NBTTagCompound nametag = new NBTTagCompound();
                nametag.setString("SkullOwner", player.getDisplayName());
                dropStack.setTagCompound(nametag);
                ItemHelper.addDrops(event, dropStack);
            }

            else if (event.source.damageType.equals("player"))
            {
                EntityPlayer source = (EntityPlayer) event.source.getEntity();
                ItemStack stack = source.getCurrentEquippedItem();
                if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof ToolCore)
                {
                    int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                    if (stack.getItem() == TinkerTools.cleaver)
                        beheading += 2;
                    if (beheading > 0 && TConstruct.random.nextInt(100) < beheading * 50)
                    {
                        ItemStack dropStack = new ItemStack(Items.skull, 1, 3);
                        NBTTagCompound nametag = new NBTTagCompound();
                        nametag.setString("SkullOwner", player.getDisplayName());
                        dropStack.setTagCompound(nametag);
                        ItemHelper.addDrops(event, dropStack);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void registerOre (OreRegisterEvent evt)
    {

        if (evt.Name == "crystalQuartz")
        {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 2);
        }

        else if (evt.Name == "crystalCertusQuartz")
        {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 24);
        }
    }
}
