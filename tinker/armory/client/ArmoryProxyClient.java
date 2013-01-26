package tinker.armory.client;

import java.io.File;

import tinker.armory.ArmoryProxyCommon;
import tinker.armory.content.ArmorStandEntity;
import tinker.armory.content.ToolrackLogic;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ArmoryProxyClient extends ArmoryProxyCommon
{
	@Override
	public void registerRenderer()
	{
		RenderingRegistry.registerBlockHandler(new RenderToolrack());
		ClientRegistry.bindTileEntitySpecialRenderer(ToolrackLogic.class, new ToolrackSpecialRender());
		RenderingRegistry.registerEntityRenderingHandler(tinker.armory.content.ArmorStandEntity.class, new RenderArmorStandEntity(new ArmorStandModelStatue(), 0));
	}
	
	@Override
	public File getMinecraftDir()
	{
		return Minecraft.getMinecraftDir();
	}
}
