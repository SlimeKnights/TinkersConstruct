package tconstruct.weaponry;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.event.PartBuilderEvent;
import tconstruct.library.event.SmelteryCastEvent;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.library.tools.DualMaterialToolPart;
import tconstruct.library.weaponry.ArrowShaftMaterial;
import tconstruct.library.weaponry.BowBaseAmmo;
import tconstruct.library.weaponry.ProjectileWeapon;
import tconstruct.library.weaponry.IAmmo;
import tconstruct.weaponry.weapons.Crossbow;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.*;
import tconstruct.weaponry.weapons.LongBow;
import tconstruct.weaponry.weapons.ShortBow;

public class WeaponryHandler {
    @SubscribeEvent
    public void onCrafting (PlayerEvent.ItemCraftedEvent event)
    {
        Item item = event.crafting.getItem();
        if (!event.player.worldObj.isRemote)
        {
            if (item == Item.getItemFromBlock(TinkerTools.toolStationWood))
            {
                if (!PHConstruct.beginnerBook)
                {
                    return;
                }

                TPlayerStats stats = TPlayerStats.get(event.player);
                if (!stats.weaponryManual)
                {
                    stats.weaponryManual = true;
                    AbilityHelper.spawnItemAtPlayer(event.player, new ItemStack(TinkerTools.manualBook, 1, 4));
                }
            }
        }
    }

    // Provides ammo-items with the necessary NBT
    @SubscribeEvent
    public void onAmmoCrafted(ToolCraftEvent.NormalTool event)
    {
        if(!(event.tool instanceof IAmmo))
            return;

        NBTTagCompound tags = event.toolTag.getCompoundTag("InfiTool");

        // calculate its stats
        if(event.tool instanceof ArrowAmmo)
        {
            // arrows work like this:
            // the head is responsible for the damage, but also adds weight
            // the shaft defines how fragile the arrow is, and also adds to the weight a bit. But mostly the fragility.
            // the fletching defines the accuracy of the arrow and adds a bit breakchance. Mostly there for the durability modifier because availability

            // Shortbows work better with lighter arrows
            // while Longbows require a bit heavier arrows, the lighter the arrow the more impact the accuracy has otherwise

            // summa sumarum: heavier arrows fall faster (less range) but accuracy has less impact

            // the materials
            ToolMaterial head = TConstructRegistry.getMaterial(tags.getInteger("Head"));
            ArrowMaterial arrow = TConstructRegistry.getArrowMaterial(tags.getInteger("Head"));
            ArrowShaftMaterial shaft = (ArrowShaftMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Handle"), ArrowShaftMaterial.class);
            FletchingMaterial fletching = (FletchingMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Accessory"), FletchingMaterial.class);

            // todo: fix leaf fletching
            if(fletching == null)
                fletching = (FletchingMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Accessory"), FletchlingLeafMaterial.class);

            // invalid materials
            if(arrow == null || shaft == null || fletching == null)
            {
                event.setResult(Event.Result.DENY);
                return;
            }

            int durability = (int)((float)head.durability() * shaft.durabilityModifier * fletching.durabilityModifier);
            float weight = arrow.mass + shaft.weight;
            float accuracy = fletching.accuracy;
            float breakChance = shaft.fragility * arrow.breakChance + fletching.breakChance;

            setAmmoData(tags, durability, weight, breakChance, accuracy, head.shoddy(), head.reinforced());

            // Blaze shafts give fiery
            if(tags.getInteger("Handle") == 3)
            {
                tags.setInteger("Fiery", 5);
            }
            // arrows get only 2 modifiers (so one less) by default
            int mods = Math.max(0, tags.getInteger("Modifiers")-1);
            tags.setInteger("Modifiers", mods);
        }
        else if(event.tool instanceof BoltAmmo)
        {
            // bolts work like ammos, but have more weight as they have 2 main materials
            // Crossbows work better with heavier bolts

            // the materials
            ToolMaterial headMat = TConstructRegistry.getMaterial(tags.getInteger("Head"));
            ToolMaterial coreMat = TConstructRegistry.getMaterial(tags.getInteger("Handle"));
            ArrowMaterial head = TConstructRegistry.getArrowMaterial(tags.getInteger("Head"));
            ArrowMaterial core = TConstructRegistry.getArrowMaterial(tags.getInteger("Handle"));
            FletchingMaterial fletching = (FletchingMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Accessory"), FletchingMaterial.class);

            // todo: fix leaf fletching
            if(fletching == null)
                fletching = (FletchingMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Accessory"), FletchlingLeafMaterial.class);

            // invalid materials
            if(head == null || core == null || fletching == null)
            {
                event.setResult(Event.Result.DENY);
                return;
            }

            int durability = (int)((float)headMat.durability() * coreMat.handleDurability() * fletching.durabilityModifier);
            float weight = head.mass + core.mass*1.5f;
            float accuracy = (100f + fletching.accuracy)/2f;
            float breakChance = (fletching.breakChance*2 + 0.15f * core.breakChance) * head.breakChance/2f;
            float shoddy = (headMat.shoddy() + coreMat.shoddy())/2f;
            int reinforced = Math.max(headMat.reinforced(), coreMat.reinforced());

            setAmmoData(tags, durability, weight, breakChance, accuracy, shoddy, reinforced);

            int mods = Math.max(0, tags.getInteger("Modifiers")-1);
            tags.setInteger("Modifiers", mods);
        }

        // now that durability has been handled...
        // fill the ammo full and at the same time provide the missing NBT tag
        IAmmo ammoItem = (IAmmo) event.tool;
        tags.setInteger("Ammo", ammoItem.getMaxAmmo(tags));
    }

    @SubscribeEvent
    public void onProjectileWeaponCrafted(ToolCraftEvent.NormalTool event)
    {
        if(!(event.tool instanceof ProjectileWeapon))
            return;

        NBTTagCompound tags = event.toolTag.getCompoundTag("InfiTool");
        ProjectileWeapon weapon = (ProjectileWeapon)event.tool;

        int drawSpeed = 0;
        float flightSpeed = 0;

        BowMaterial top;
        BowMaterial bottom;
        BowstringMaterial string;

        boolean enchanted = false;

        if(event.tool instanceof BowBaseAmmo) {
            top = TConstructRegistry.getBowMaterial(tags.getInteger("Head"));
            bottom = TConstructRegistry.getBowMaterial(tags.getInteger("Accessory"));
            string = (BowstringMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Handle"), BowstringMaterial.class);

            // some materials seem to be incompatible
            if(top == null || bottom == null || string == null)
            {
                event.setResult(Event.Result.DENY);
                return;
            }

            drawSpeed = (int) ((top.drawspeed + bottom.drawspeed) / 2f * string.drawspeedModifier);
            flightSpeed = (top.flightSpeedMax + bottom.flightSpeedMax)/2 * string.flightSpeedModifier;

            // SHORT bows have a SHORTER windup because they're SHORT. hahahaha.... get it?
            if(event.tool instanceof ShortBow) {
                drawSpeed *= 0.9;
            }
            // longbows have LONGER drawspeed. LOGIC!
            if(event.tool instanceof LongBow) {
                drawSpeed *= 1.8f;
                flightSpeed *= 1.5f;
            }

            // enchanted bowstring
            enchanted = tags.getInteger("Handle") == 1;
        }
        else if(event.tool instanceof Crossbow)
        {
            top = TConstructRegistry.getBowMaterial(tags.getInteger("Head"));
            string = (BowstringMaterial) TConstructRegistry.getCustomMaterial(tags.getInteger("Accessory"), BowstringMaterial.class);

            // some materials seem to be incompatible
            if(top == null || string == null)
            {
                event.setResult(Event.Result.DENY);
                return;
            }

            drawSpeed = (int) ((float)top.drawspeed * string.drawspeedModifier);
            flightSpeed = (top.flightSpeedMax * string.flightSpeedModifier);

            // crossbows are more efficient the higher the base drawspeed is. So the higher the drawspeed, the more bonus you get
            drawSpeed *= 2.5f;
            drawSpeed -= drawSpeed*0.25f;

            // crossbows are stronk
            flightSpeed *= 1.5;

            // enchanted bowstring
            enchanted = tags.getInteger("Accessory") == 1;
        }
        else
            return;

        // enchanted fabric
        if (enchanted) {
            int modifiers = tags.getInteger("Modifiers");
            modifiers += 1;
            tags.setInteger("Modifiers", modifiers);
        }

        tags.setInteger("DrawSpeed", drawSpeed);
        tags.setInteger("BaseDrawSpeed", drawSpeed); // used to calculate correct speed increase with redstone modifier
        tags.setFloat("FlightSpeed", flightSpeed);
    }


    // arrows use custom materials. But we don't allow the creation of those items
    // we therefore replace the items with their toolpart counterparts here
    @SubscribeEvent
    public void buildArrow(ToolBuildEvent event)
    {
        if(event.headStack == null || event.handleStack == null || event.accessoryStack == null)
            return;

        // are we building an arrow?
        CustomMaterial mat = TConstructRegistry.getCustomMaterial(event.handleStack, ArrowShaftMaterial.class);
        if(mat == null)
            return;
        Item extra = event.extraStack != null ? event.extraStack.getItem() : null;
        ToolCore tool = ToolBuilder.instance.getMatchingRecipe(event.headStack.getItem(), mat.craftingItem.getItem(), event.accessoryStack.getItem(), extra);

        // it's an arrow!
        if(tool == TinkerWeaponry.arrowAmmo)
            event.handleStack = mat.craftingItem.copy();
    }

    // bolts require special treatment because of their dual-material cores
    @SubscribeEvent
    public void buildBolt(ToolBuildEvent event)
    {
        if(event.headStack == null || event.handleStack == null)
            return;

        if(event.headStack.getItem() != TinkerWeaponry.partBolt)
            return;

        // split the bolt into its two parts
        ItemStack bolt1 = event.headStack.copy();
        ItemStack bolt2 = event.headStack;
        ItemStack fletching = event.handleStack;


        // set the correct material on the 2nd part
        DualMaterialToolPart dualPart = (DualMaterialToolPart) bolt2.getItem();
        bolt1.setItemDamage(dualPart.getMaterialID2(bolt1));

        // update the part positions xX
        event.headStack = bolt1;
        event.handleStack = bolt2;
        event.accessoryStack = fletching;
    }

    private void setAmmoData(NBTTagCompound tags, int durability, float weight, float breakChance, float accuracy, float shoddy, int reinforced)
    {
        tags.setInteger("TotalDurability", durability);
        tags.setInteger("BaseDurability", durability);
        tags.setFloat("Mass", weight);
        tags.setFloat("BreakChance", breakChance);
        tags.setFloat("Accuracy", accuracy);
        tags.setFloat("Shoddy", shoddy); // we could actually always set this to 0 since it has zero impact on ammo
        tags.setInteger("Unbreaking", reinforced);
    }

    @SubscribeEvent
    public void weaponryPartCrafted(PartBuilderEvent.NormalPart event)
    {
        if(event.pattern == null)
            return;

        // weaponry part
        if(event.pattern.getItem() == TinkerWeaponry.woodPattern) {
            // crossbow or bow limb
            if (event.pattern.getItemDamage() == 1 || event.pattern.getItemDamage() == 3) {
                // only allow crafting if the material has bow stats
                PatternBuilder.ItemKey key =  PatternBuilder.instance.getItemKey(event.material);
                if(key == null)
                    return;

                PatternBuilder.MaterialSet ms = PatternBuilder.instance.materialSets.get(key.key);
                if(ms == null)
                    return;

                if(TConstructRegistry.getBowMaterial(ms.materialID) == null)
                    event.setResult(Event.Result.DENY);
            }
        }
        // arrow stats, still in tool
        if(event.pattern.getItem() == TinkerTools.woodPattern && event.pattern.getItemDamage() == 25) {
            // only allow crafting if the material has bow stats
            PatternBuilder.ItemKey key =  PatternBuilder.instance.getItemKey(event.material);
            if(key == null)
                return;

            PatternBuilder.MaterialSet ms = PatternBuilder.instance.materialSets.get(key.key);
            if(ms == null)
                return;

            if(TConstructRegistry.getArrowMaterial(ms.materialID) == null)
                event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void weaponryPartCast(SmelteryCastEvent.CastingTable event) {
        if (event.recipe == null || event.recipe.output == null)
            return;

        if (!(event.recipe.output.getItem() instanceof IToolPart))
            return;

        // get material for the output
        int mat = ((IToolPart) event.recipe.output.getItem()).getMaterialID(event.recipe.output);

        // arrowhead
        if (event.recipe.output.getItem() == TinkerWeaponry.arrowhead)
            if (TConstructRegistry.getArrowMaterial(mat) == null)
                event.setResult(Event.Result.DENY);
        // crossbow/bowlimb
        if (event.recipe.output.getItem() == TinkerWeaponry.partBowLimb || event.recipe.output.getItem() == TinkerWeaponry.partCrossbowLimb)
            if (TConstructRegistry.getBowMaterial(mat) == null)
                event.setResult(Event.Result.DENY);
    }
}
