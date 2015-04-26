package com.mycompanioncube.zones;

import com.mycompanioncube.zones.Zones;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/**
 * GUI overlay that shows the zone the player is currently in at the top of the screen. 
 *  
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class GuiZoneMessage extends Gui {
	private Minecraft mc;

	public GuiZoneMessage(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.EXPERIENCE) {
			return;
		}

		ScaledResolution sR = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

		int xPos = 10;
		int yPos = 2;

		yPos += 10;
		String s = Zones.instance.getMessage();

		xPos = (sR.getScaledWidth() - mc.fontRendererObj.getStringWidth(s)) / 2;
		this.mc.fontRendererObj.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRendererObj.drawString(s, xPos, yPos, 15000000);
	}
}