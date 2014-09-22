package tconstruct.util;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.IPattern;
import tconstruct.library.util.IToolPart;
import tconstruct.smeltery.TinkerSmeltery;

import java.util.LinkedList;
import java.util.List;

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
                ToolMaterial mat = scanMaterial(tag);
                if(mat != null) {
                    TConstructRegistry.addtoolMaterial(tag.getInteger("Id"), mat);
                    TConstruct.logger.info("IMC: Added material " + mat.materialName);
                }
            }
            else if(type.equals("addPartBuilderMaterial"))
            {
                if(!message.isNBTMessage())
                {
                    logInvalidMessage(message);
                    continue;
                }
             //   PatternBuilder.instance.registerMaterial();
            }
            else if(type.equals("addPartCastingMaterial"))
            {
                if(!message.isNBTMessage())
                {
                    logInvalidMessage(message);
                    continue;
                }

                NBTTagCompound tag = message.getNBTValue();

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
            }
        }
    }

    private static void logInvalidMessage(FMLInterModComms.IMCMessage message)
    {
        TConstruct.logger.error(String.format("Received invalid IMC '%s' from %s. Not a NBT Message.", message.key, message.getSender()));
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
            shoddy = tag.getFloat("Jagged");

        if(tag.hasKey("localizationString"))
            return new ToolMaterial(name, tag.getString("localizationString"), hlvl, durability, speed, attack, handle, reinforced, shoddy, style, color);
        else
            return new ToolMaterial(name, hlvl, durability, speed, attack, handle, reinforced, shoddy, style, color);
    }
}
