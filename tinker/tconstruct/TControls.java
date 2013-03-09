package tinker.tconstruct;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TControls extends KeyHandler
{
	static KeyBinding grabKey = new KeyBinding("key.grab", 29);
	static KeyBinding jumpKey;
	Minecraft mc;
	EntityClientPlayerMP player;
	boolean jumping;
	boolean doubleJump = true;
	boolean climbing = false;
	boolean onGround = false;

	public TControls()
	{
		super(new KeyBinding[] { Minecraft.getMinecraft().gameSettings.keyBindJump, grabKey }, new boolean[] { false, false });
		mc = Minecraft.getMinecraft();
		jumpKey = mc.gameSettings.keyBindJump;
		System.out.println("Controls registered");
	}
	
	//I will be wanting these later
	/*@Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
		super.tickStart(type, tickData);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    	super.tickEnd(type, tickData);
    }*/

	@Override
	public String getLabel ()
	{
		return null;
	}

	@Override
	public void keyDown (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (kb == jumpKey && tickEnd) //Double jump
		{
			if (jumping && !doubleJump)
			{
				if (player == null)
					player = mc.thePlayer;
				
				player.motionY = 0.42D;
				player.fallDistance = 0;

				if (player.isPotionActive(Potion.jump))
				{
					player.motionY += (double) ((float) (player.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
				}

				doubleJump = true;
				updateServer(mc.thePlayer.username);
			}

			if (!jumping)
				jumping = mc.thePlayer.isAirBorne;
		}
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

	void updateServer (String name)
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

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "TConstruct";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToServer(packet);
	}
}
