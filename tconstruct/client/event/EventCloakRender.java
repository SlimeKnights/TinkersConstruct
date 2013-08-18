package tconstruct.client.event;

import java.awt.image.BufferedImage;
import java.net.*;

import javax.swing.ImageIcon;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventCloakRender {

	@ForgeSubscribe
	public void onPreRenderSpecials(RenderPlayerEvent.Specials.Pre event){
		if(event.entityPlayer instanceof AbstractClientPlayer){
			AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)event.entityPlayer;
			abstractClientPlayer.func_110310_o().field_110559_g = false;
			abstractClientPlayer.func_110310_o().field_110560_d = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
			try {
				abstractClientPlayer.func_110310_o().field_110560_d.getGraphics().drawImage(new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/48633261/cape.png")).getImage(), 0, 0, null);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			event.renderCape = true;
		}
	}
	
}
