package mods.tinker.tconstruct.client.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
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
		String cloakURL = cloaks.get(event.entityPlayer.username);
//		System.out.println(cloakURL);
		if(cloakURL != null){
			event.entityPlayer.cloakUrl = cloakURL;
			event.renderCape = true;
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
			int linetracker = 1;
			while((str = br.readLine()) != null){
				if(str.contains(":")){
					String nick = str.substring(0, str.indexOf(":"));
					String link = str.substring(str.indexOf(":") + 1);
					cloaks.put(nick, link);
					Minecraft.getMinecraft().renderEngine.obtainImageData(link, new ImageBufferDownload());
				}else{
					System.err.println("[TinkersConstruct] [skins.txt] Syntax error on line " + linetracker + ": " + str);
				}
				linetracker++;
			}

			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
