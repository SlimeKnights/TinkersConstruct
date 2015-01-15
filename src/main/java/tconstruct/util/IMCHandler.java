package tconstruct.util;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.IPattern;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class IMCHandler {
    private IMCHandler() {}

    public static void processIMC(List<FMLInterModComms.IMCMessage> messages)
    {
        for(FMLInterModComms.IMCMessage message : messages)
        {
            String type = message.key;
            if(type == null || type.isEmpty())
                continue;

            // process materials added from mods
            if(type.equals("addMaterial"))
            {
                if(!message.isNBTMessage())
                {
                    logInvalidMessage(message);
                    continue;
                }

                NBTTagCompound tag = message.getNBTValue();
                int id = tag.getInteger("Id");
                ToolMaterial mat = scanMaterial(tag);
                if(mat != null) {
                    TConstructRegistry.addtoolMaterial(id, mat);
                    TConstructRegistry.addDefaultToolPartMaterial(id);
                    TConstruct.logger.info("IMC: Added material " + mat.materialName);

                    // bow stats
                    if(tag.hasKey("Bow_DrawSpeed") && tag.hasKey("Bow_ProjectileSpeed"))
                    {
                        int drawspeed = tag.getInteger("Bow_DrawSpeed");
                        float flightspeed = tag.getFloat("Bow_ProjectileSpeed");

                        TConstructRegistry.addBowMaterial(id, drawspeed, flightspeed);
                        TConstruct.logger.info("IMC: Added Bow stats for material " + mat.materialName);
                    }
                    // arrow stats
                    if(tag.hasKey("Projectile_Mass") && tag.hasKey("Projectile_Fragility"))
                    {
                        float mass = tag.getFloat("Projectile_Mass");
                        float breakchance = tag.getFloat("Projectile_Fragility");

                        TConstructRegistry.addArrowMaterial(id, mass, breakchance);
                        TConstruct.logger.info("IMC: Added Projectile stats for material " + mat.materialName);
                    }
                }
            }
            else if(type.equals("addPartBuilderMaterial"))
            {
                if(!message.isNBTMessage())
                {
                    logInvalidMessage(message);
                    continue;
                }
                NBTTagCompound tag = message.getNBTValue();

                if(!checkRequiredTags("PartBuilder", tag, "MaterialId", "Item", "Value"))
                    continue;

                int matID = tag.getInteger("MaterialId");
                int value = tag.getInteger("Value");

                if(TConstructRegistry.getMaterial(matID) == null)
                {
                    TConstruct.logger.error("PartBuilder IMC: Unknown Material ID " + matID);
                    continue;
                }

                ItemStack item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));
                ItemStack shard = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Shard")); // optional
                ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, matID);

                // default shard if none present. Has to exist because old code.
                if(shard == null)
                {
                    TConstructRegistry.addDefaultShardMaterial(matID);
                    shard = new ItemStack(TinkerTools.toolShard, 1, matID);
                }

                // register the material
                PatternBuilder.instance.registerFullMaterial(item, value, TConstructRegistry.getMaterial(matID).materialName, shard, rod, matID);

                List<Item> addItems = new LinkedList<Item>();
                List<Integer> addMetas = new LinkedList<Integer>();
                List<ItemStack> addOUtputs = new LinkedList<ItemStack>();

                // add mappings for everything that has stone tool mappings
               for(Map.Entry<List, ItemStack> mappingEntry : TConstructRegistry.patternPartMapping.entrySet())
                {
                    List mapping = mappingEntry.getKey();
                    // only stone mappings
                    if((Integer)mapping.get(2) != TinkerTools.MaterialID.Stone)
                        continue;

                    // only if the output is a dynamic part
                    if(!(mappingEntry.getValue().getItem() instanceof DynamicToolPart))
                        continue;

                    Item woodPattern = (Item) mapping.get(0);
                    Integer meta = (Integer) mapping.get(1);

                    ItemStack output = mappingEntry.getValue().copy();
                    output.setItemDamage(matID);

                    // save data, concurrent modification exception and i'm lazy
                    addItems.add(woodPattern);
                    addMetas.add(meta);
                    addOUtputs.add(output);
                }

                // add a part mapping for it
                for(int i = 0; i < addItems.size(); i++)
                    TConstructRegistry.addPartMapping(addItems.get(i), addMetas.get(i), matID, addOUtputs.get(i));


                TConstruct.logger.info("PartBuilder IMC: Added Part builder ampping for " + TConstructRegistry.getMaterial(matID).materialName);
            }
            else if(type.equals("addPartCastingMaterial"))
            {
                if(!message.isNBTMessage())
                {
                    logInvalidMessage(message);
                    continue;
                }

                NBTTagCompound tag = message.getNBTValue();

                if(!checkRequiredTags("Casting", tag, "MaterialId", "FluidName"))
                    continue;

                if(!tag.hasKey("MaterialId"))
                {
                    TConstruct.logger.error("Casting IMC: Not material ID for the result present");
                    continue;
                }

                int matID = tag.getInteger("MaterialId");
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(tag);
                if(liquid == null) {
                    TConstruct.logger.error("Casting IMC: No fluid found");
                    continue;
                }

                // we add the toolpart to all smeltery recipies that use iron and create a toolpart
                List<CastingRecipe> newRecipies = new LinkedList<CastingRecipe>();
                for(CastingRecipe recipe : TConstructRegistry.getTableCasting().getCastingRecipes())
                {
                    if(recipe.castingMetal.getFluid() != TinkerSmeltery.moltenIronFluid)
                        continue;
                    if(recipe.cast == null || !(recipe.cast.getItem() instanceof IPattern))
                        continue;
                    if(!(recipe.getResult().getItem() instanceof DynamicToolPart)) // has to be dynamic toolpart to support automatic addition
                        continue;

                    newRecipies.add(recipe);
                }

                // has to be done separately so we have all checks and no concurrent modification exception
                for(CastingRecipe recipe : newRecipies)
                {
                    ItemStack output = recipe.getResult().copy();
                    output.setItemDamage(matID);

                    FluidStack liquid2 = new FluidStack(liquid, recipe.castingMetal.amount);

                    // ok, this recipe creates a toolpart and uses iron for it. add a new one for the IMC stuff!
                    TConstructRegistry.getTableCasting().addCastingRecipe(output, liquid2, recipe.cast, recipe.consumeCast, recipe.coolTime);
                }

                TConstruct.logger.info("Casting IMC: Added fluid " + tag.getString("FluidName") + " to part casting");
            }
            else if(type.equals("addMaterialItem")) {
                if(!message.isNBTMessage()) {
                    logInvalidMessage(message);
                    continue;
                }

                NBTTagCompound tag = message.getNBTValue();

                if (!checkRequiredTags("Material Item", tag, "MaterialId", "Value", "Item"))
                    continue;

                int id = tag.getInteger("MaterialId");
                int value = tag.getInteger("Value");
                ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));

                if(stack == null) {
                    TConstruct.logger.error("Material Item IMC: Item for Material %d is null", id);
                    continue;
                }

                if(TConstructRegistry.getMaterial(id) == null) {
                    TConstruct.logger.error("Material Item IMC: Material with ID %d does not exist", id);
                    continue;
                }


                ToolMaterial mat = TConstructRegistry.getMaterial(id);

                // we already have the material registered in there
                if(PatternBuilder.instance.materialSets.containsKey(mat.materialName))
                {
                    PatternBuilder.instance.registerMaterial(stack, value, mat.materialName);
                }
                else {
                    TConstructRegistry.addDefaultShardMaterial(id);
                    ItemStack shard = new ItemStack(TinkerTools.toolShard, 1, id);
                    ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, id);

                    // register the material
                    PatternBuilder.instance.registerFullMaterial(stack, value, TConstructRegistry.getMaterial(id).materialName, shard, rod, id);
                }
            }
            else if(type.equals("addSmelteryMelting")) {
                if (!message.isNBTMessage()) {
                    logInvalidMessage(message);
                    continue;
                }
                NBTTagCompound tag = message.getNBTValue();

                if (!checkRequiredTags("Smeltery", tag, "FluidName", "Temperature", "Item", "Block"))
                    continue;

                FluidStack liquid = FluidStack.loadFluidStackFromNBT(tag);
                if(liquid == null) {
                    TConstruct.logger.error("Smeltery IMC: No fluid found");
                    continue;
                }
                if(liquid.amount <= 0) {
                    TConstruct.logger.error("Smeltery IMC: Liquid has to have an amount greater than zero");
                    continue;
                }

                ItemStack item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Item"));
                ItemStack block = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Block"));
                int temperature = tag.getInteger("Temperature");

                Smeltery.addMelting(item, Block.getBlockFromItem(block.getItem()), block.getItemDamage(), temperature, liquid);
                TConstruct.logger.info("Smeltery IMC: Added melting: " + item.getDisplayName() + " to " + liquid.amount + "mb " + liquid.getLocalizedName());
            }
            else if(type.equals("addSmelteryFuel")) {
                if (!message.isNBTMessage()) {
                    logInvalidMessage(message);
                    continue;
                }
                NBTTagCompound tag = message.getNBTValue();

                if (!checkRequiredTags("Smeltery", tag, "FluidName", "Temperature", "Duration"))
                    continue;

                FluidStack liquid = FluidStack.loadFluidStackFromNBT(tag);
                if(liquid == null) {
                    TConstruct.logger.error("Smeltery IMC: No fluid found");
                    continue;
                }

                int temperature = tag.getInteger("Temperature");
                int duration = tag.getInteger("Duration");

                Smeltery.addSmelteryFuel(liquid.getFluid(), temperature, duration);

                TConstruct.logger.info("Smeltery IMC: Added fuel: " + liquid.getLocalizedName() + " (" + temperature + ", " + duration + ")");
            } else if (type.equals("addFluxBattery")) {
                if (!message.isItemStackMessage()) {
                    logInvalidMessage(message, "ItemStack");
                    continue;
                }
                ItemStack battery = message.getItemStackValue();
                battery.stackSize = 1; // avoid getting a stack size of 0 or larger than 1

                if(!(battery.getItem() instanceof IEnergyContainerItem)) {
                    TConstruct.logger.error("Flux Battery IMC: ItemStack is no instance of IEnergyContainerItem");
                }

                if (TinkerTools.modFlux != null) {
                    TinkerTools.modFlux.batteries.add(battery);
                }
            }
        }
    }

    private static boolean checkRequiredTags(String prefix, NBTTagCompound tag, String... tags)
    {
        boolean ok = true;
        for(String t : tags)
            if(!tag.hasKey(t))
            {
                TConstruct.logger.error(String.format("%s IMC: Missing required NBT Tag %s", prefix, t));
                ok = false; // don't abort, report all missing tags
            }

        return ok;
    }

    private static void logInvalidMessage(FMLInterModComms.IMCMessage message)
    {
        logInvalidMessage(message, "NBT");
    }

    private static void logInvalidMessage(FMLInterModComms.IMCMessage message, String type)
    {
        TConstruct.logger.error(String.format("Received invalid IMC '%s' from %s. Not a %s Message.", message.key, message.getSender(), type));
    }

    private static ToolMaterial scanMaterial(NBTTagCompound tag)
    {
        if(!tag.hasKey("Name")) {
            TConstruct.logger.error("Material IMC: Material has no name");
            return null;
        }
        String name = tag.getString("Name");

        if(!tag.hasKey("Id")) {
            TConstruct.logger.error("Material IMC: Materials need a unique id. " + name);
            return null;
        }
        else if(!tag.hasKey("Durability")) {
            TConstruct.logger.error("Material IMC: Materials need a durability. " + name);
            return null;
        }
        else if(!tag.hasKey("MiningSpeed")) {
            TConstruct.logger.error("Material IMC: Materials need a mining speed. " + name);
            return null;
        }
        else if(tag.hasKey("Stonebound") && tag.hasKey("Jagged")) {
            TConstruct.logger.error("Material IMC: Materials can only be Stonebound or Jagged. " + name);
            return null;
        }

        int hlvl = tag.getInteger("HarvestLevel");
        int durability = tag.getInteger("Durability");
        int speed = tag.getInteger("MiningSpeed");
        int attack = tag.getInteger("Attack");
        float handle = tag.getFloat("HandleModifier");
        int reinforced = tag.getInteger("Reinforced");
        float shoddy = tag.getFloat("Stonebound");
        String style = tag.getString("Style");
        int color = tag.getInteger("Color");

        if(tag.hasKey("Jagged"))
            shoddy = -tag.getFloat("Jagged");

        if(tag.hasKey("localizationString"))
            return new ToolMaterial(name, tag.getString("localizationString"), hlvl, durability, speed, attack, handle, reinforced, shoddy, style, color);
        else
            return new ToolMaterial(name, hlvl, durability, speed, attack, handle, reinforced, shoddy, style, color);
    }
}
