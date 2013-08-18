package tconstruct.client.event;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.HashMap;

import javax.swing.ImageIcon;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventCloakRender {

	private final String serverLocation = "https://raw.github.com/mDiyo/TinkersConstruct/16working/capes.txt";
	private final int timeout = 1000;
	
	private HashMap<String, String> cloaks = new HashMap<String, String>();
	
	public EventCloakRender(){
		buildCloakURLDatabase();
	}
	
	@ForgeSubscribe
	public void onPreRenderSpecials(RenderPlayerEvent.Specials.Pre event){
		if(event.entityPlayer instanceof AbstractClientPlayer){
			AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)event.entityPlayer;
			
			if (abstractClientPlayer.func_110310_o().field_110560_d == null) {
				String cloakURL = cloaks.get(event.entityPlayer.username);
				
				if(cloakURL == null){
					return;
				}
				
				abstractClientPlayer.func_110310_o().field_110560_d = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
				abstractClientPlayer.func_110310_o().field_110559_g = false;
				try {
					abstractClientPlayer.func_110310_o().field_110560_d.getGraphics().drawImage(new ImageIcon(new URL(cloakURL)).getImage(), 0, 0, null);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				event.renderCape = true;
			}
		}
	}
	
	public void buildCloakURLDatabase(){
		URL url;
		try {
			url = new URL(serverLocation);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			InputStream io = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(io));
			
			String str;
			while((str = br.readLine()) != null){
				if(str.contains(":")){
					String nick = str.substring(0, str.indexOf(":"));
					String link = str.substring(str.indexOf(":") + 1);
					System.out.println(nick + " " + link);
					cloaks.put(nick, link);
				}
			}
			
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
