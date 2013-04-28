package mods.tinker.tconstruct.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.util.network.TGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TControls extends TKeyHandler
{
	//static KeyBinding grabKey = new KeyBinding("key.grab", 29);
	//static KeyBinding stiltsKey = new KeyBinding("key.stilts", 46);
    static KeyBinding armorKey = new KeyBinding("key.armor", 23);
	static KeyBinding jumpKey;
	static Minecraft mc;
	boolean jumping;
	boolean doubleJump = true;
	boolean climbing = false;
	boolean onGround = false;
	boolean onStilts = false;
	
	boolean armorGuiOpen = false;
	//boolean onStilts = false;

	public TControls()
	{
		super(new KeyBinding[] { armorKey }, new boolean[] { false }, getVanillaKeyBindings(), new boolean[] { false });
		//System.out.println("Controls registered");
	}

	private static KeyBinding[] getVanillaKeyBindings ()
	{
		mc = Minecraft.getMinecraft();
		jumpKey = mc.gameSettings.keyBindJump;
		return new KeyBinding[] { jumpKey };
	}

	@Override
	public String getLabel ()
	{
		return null;
	}

	@Override
	public void keyDown (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (tickEnd && mc.theWorld != null)
		{
		    if (kb == armorKey && !armorGuiOpen) //Extended Armor
		    {
		        //mc.thePlayer.openGui(TConstruct.instance, TGuiHandler.armor, mc.theWorld, (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ);
		        openArmorGui(mc.thePlayer.username);
		    }
			/*if (kb == jumpKey) //Double jump
			{
				if (jumping && !doubleJump)
				{
					//if (player == null)
						//player = mc.thePlayer;

					mc.thePlayer.motionY = 0.42D;
					mc.thePlayer.fallDistance = 0;

					if (mc.thePlayer.isPotionActive(Potion.jump))
					{
						mc.thePlayer.motionY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
					}

					doubleJump = true;
					resetFallDamage(mc.thePlayer.username);
				}

				if (!jumping)
					jumping = mc.thePlayer.isAirBorne;
			}*/
		}
		/*else if (kb == stiltsKey) //Stilts
		{
			float size = 1.8F;
			if (!onStilts)
				size = 0.8F;
			TConstruct.playerTracker.updateSize(mc.thePlayer.username, size);
			onStilts = !onStilts;
			//updateServer(mc.thePlayer.username, (byte) 11);
			if (onStilts)
			{
				onStilts = false;
			}
			else
			{
				onStilts = true;
			}
		}*/
	}

	@Override
	public void keyUp (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
	{
		//landOnGround();
	}

	@Override
	public EnumSet<TickType> ticks ()
	{
		return EnumSet.of(TickType.CLIENT);
	}

	public void landOnGround ()
	{
		doubleJump = false;
		jumping = false;
	}
	
	public void resetControls()
	{
		doubleJump = false;
		jumping = false;
		climbing = false;
		onGround = false;
		onStilts = false;
	}

	void resetFallDamage (String name)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try
		{
			outputStream.writeByte(10);
			outputStream.writeUTF(name);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		updateServer(bos);
	}
	
	void updateSize (String name, float size)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try
		{
			outputStream.writeByte(11);
			outputStream.writeUTF(name);
			outputStream.writeFloat(size);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		updateServer(bos);
	}
	
	void openArmorGui(String username)
	{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(3);
            outputStream.writeUTF(username);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateServer(bos);
	}
	
	void updateServer(ByteArrayOutputStream bos)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "TConstruct";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToServer(packet);
	}
}
