package tconstruct.util;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolMaterial;

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
