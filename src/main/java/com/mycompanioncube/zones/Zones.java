package com.mycompanioncube.zones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Zones.MODID, version = Zones.VERSION, name = Zones.NAME, acceptableRemoteVersions = "*")
public class Zones {
	public static final String MODID = "Zones";
	public static final String NAME = "Zones";
	public static final String VERSION = "0.4.0";

	protected Map<EntityPlayer, Zone> playerMap = new HashMap<EntityPlayer, Zone>();
	protected ZoneManager zoneManager = new ZoneManager();
	private String message = "";

	@Instance(Zones.MODID)
	public static Zones instance;

	public static SimpleNetworkWrapper net;

	public class ZoneEventHandler {
		@SubscribeEvent
		public void onLivingUpdateEvent(LivingUpdateEvent event) {
			if (event.entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) event.entity;

				Zone z = zoneManager.getZone(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
				if (z != playerMap.get(player)) {
					if (z == null) {
						player.addChatMessage(new ChatComponentText("You've left " + playerMap.get(player).getName()));
						Zones.net.sendTo(new AreaChangeMessage(""), player);
					} else {
						if (!z.hasPlayerVisitedZone(player.getUniqueID().toString())) {
							player.addChatMessage(new ChatComponentText("You have discovered " + z.getName() + (z.getZoneCreator() == null ? "" : ", founded by " + z.getZoneCreator()) + (z.isProtected() ? ". You feel safe here" : ".")));
							z.setZoneVisitedByPlayer(player.getUniqueID().toString());
						} else {
							player.addChatMessage(new ChatComponentText("You have entered " + z.getName() + (z.isProtected() ? ". You feel safe here" : ".")));
						}
						Zones.net.sendTo(new AreaChangeMessage(z.getName()), player);
					}
				}
				playerMap.put(player, z);
			}
		}

		@SubscribeEvent
		public void onLivingSpawnEvent(LivingSpawnEvent event) {
			if (!(event.world.provider instanceof WorldProviderSurface))
				return;

			Zone z = zoneManager.getZone((int) event.x, (int) event.y, (int) event.z);

			if (z != null && z.isProtected()) {
				event.setResult(Result.DENY);
				return;
			}
		}

		@SubscribeEvent
		public void worldEvent(WorldEvent.Save event) {
			if (event.world.provider instanceof WorldProviderSurface)
				save();
		}
	}

	public void load() {
		System.out.println("Loading zones");
		GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT);

		Gson gson = gsonBuilder.create();

		File file = new File("zones.json");
		if (!file.exists()) {
			zoneManager = new ZoneManager();
			System.out.println("Zones.json not found");
			return;
		}

		JsonReader jsonReader = null;
		try {
			jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))));

			zoneManager = gson.fromJson(jsonReader, ZoneManager.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (jsonReader != null) {
				try {
					jsonReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (Zone z : zoneManager.getZones()) {
			System.out.println("Loaded zone: " + z.getName());
		}
	}

	public void save() {
		//System.out.println("Saving zones");
		GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT);

		Gson gson = gsonBuilder.create();

		try {
			FileOutputStream out = new FileOutputStream("zones.json");
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
			writer.setIndent("    ");

			gson.toJson(zoneManager, ZoneManager.class, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		net = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		net.registerMessage(AreaChangeMessage.class, AreaChangeMessage.class, 0, Side.CLIENT);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new ZoneCommands());
		load();
		save();
		MinecraftForge.EVENT_BUS.register(new ZoneEventHandler());
		FMLCommonHandler.instance().bus().register(new ZoneEventHandler());
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new GuiZoneMessage(Minecraft.getMinecraft()));
	}

	public void addZone(Zone buildZone) {
		zoneManager.add(buildZone);
	}

	public Zone getZoneByName(String a) {
		return zoneManager.getZoneByName(a);
	}

	public ZoneManager getZoneManager() {
		return zoneManager;
	}

	public void setMessage(String text) {
		this.message = text;
	}

	public String getMessage() {
		return this.message;
	}
}
