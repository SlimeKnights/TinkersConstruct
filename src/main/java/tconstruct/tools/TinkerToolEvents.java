package tconstruct.tools;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import java.util.List;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.oredict.*;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import tconstruct.TConstruct;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.event.*;
import tconstruct.library.tools.*;
import tconstruct.util.ItemHelper;
import tconstruct.util.config.PHConstruct;

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
    public void buildTool (ToolBuildEvent event)
    {
        // check if the handle is a bone
        if (event.handleStack.getItem() == Items.bone)
        {
            event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 5); // bone tool rod
            return;
        }

        // check if the handle is a stick
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");
        for (ItemStack stick : sticks)
            if (OreDictionary.itemMatches(stick, event.handleStack, false))
            {
                event.handleStack = new ItemStack(TinkerTools.toolRod, 1, 0); // wooden tool rod
                return;
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

        // bonus modifiers
        handlePaper(toolTag, event.tool);
        handleThaumium(toolTag, event.tool);

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

    private void handlePaper (NBTTagCompound toolTag, ToolCore tool)
    {
        int modifiers = toolTag.getInteger("Modifiers");
        if (toolTag.getInteger("Head") == TinkerTools.MaterialID.Paper)
            modifiers++;
        if (toolTag.getInteger("Handle") == TinkerTools.MaterialID.Paper)
            modifiers++;
        if (toolTag.getInteger("Accessory") == TinkerTools.MaterialID.Paper)
            modifiers++;
        if (toolTag.getInteger("Extra") == TinkerTools.MaterialID.Paper)
            modifiers++;

        // 2 part tools gain 2 modifiers for the head
        if (tool.getPartAmount() == 2 && toolTag.getInteger("Head") == TinkerTools.MaterialID.Paper)
            modifiers++;

        toolTag.setInteger("Modifiers", modifiers);
    }

    private void handleThaumium (NBTTagCompound toolTag, ToolCore tool)
    {
        // count thaumic parts
        int thaum = 0;
        if (toolTag.getInteger("Head") == TinkerTools.MaterialID.Thaumium)
            thaum++;
        if (toolTag.getInteger("Handle") == TinkerTools.MaterialID.Thaumium)
            thaum++;
        if (toolTag.getInteger("Accessory") == TinkerTools.MaterialID.Thaumium)
            thaum++;
        if (toolTag.getInteger("Extra") == TinkerTools.MaterialID.Thaumium)
            thaum++;

        // each part gives 0.5 modifiers, rounded up
        int bonusModifiers = (int) Math.ceil((double) thaum / 2d);

        // 2-part tools get 1 modifier per part
        if (tool.getPartAmount() == 2)
            bonusModifiers = thaum;

        int modifiers = toolTag.getInteger("Modifiers");
        modifiers += bonusModifiers;
        toolTag.setInteger("Modifiers", modifiers);
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
        FletchingMaterial mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchingMaterial.class);
        // maybe it's a leaf fletchling
        if (mat == null)
            mat = (FletchingMaterial) TConstructRegistry.getCustomMaterial(stack, FletchlingLeafMaterial.class);
        if (mat != null)
            return mat.craftingItem.copy();
        return null;
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
                        if (attacker != null)
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
                EntityZombie enemy = (EntityZombie) event.entityLiving;

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
                EntityCreeper enemy = (EntityCreeper) event.entityLiving;

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

        if (evt.Name.equals("crystalQuartz"))
        {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 2);
        }

        else if (evt.Name.equals("crystalCertusQuartz"))
        {
            TinkerTools.modAttack.addStackToMatchList(evt.Ore, 24);
        }
    }
}
