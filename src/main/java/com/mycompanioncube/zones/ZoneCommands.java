package com.mycompanioncube.zones;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.WorldProviderSurface;

/**
 * Implements all the command line commands for zones.
 * 
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class ZoneCommands implements ICommand {

	private List<String> aliases;

	public ZoneCommands() {
		this.aliases = new ArrayList<String>();
		this.aliases.add("zone");
		this.aliases.add("z");
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	/**
	 * Implement access control here... currently everyone is allowed always
	 */
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}

	@Override
	public String getCommandName() {
		return "zone";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "[Zone] zone create|add|addxyz|delete|list";
	}

	@Override
	public boolean isUsernameIndex(String[] arg0, int arg1) {
		return false;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender arg0, String[] arg1) throws CommandException {
		if (arg1.length == 0) {
			arg0.addChatMessage(new TextComponentString("[Zone] Please specify command."));
		} else if (arg1[0].toLowerCase().equals("addxyz")) {
			commandAddXYZ(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("add")) {
			commandAdd(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("addy")) {
			commandAdd(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("remove")) {
			commandRemove(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("create")) {
			commandCreate(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("protect")) {
			commandProtect(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("unprotect")) {
			commandUnprotect(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("setcreator")) {
			commandSetCreator(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("village")) {
			commandVillage(arg0);
		} else if (arg1[0].toLowerCase().equals("mark")) {
			commandMark(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("delete")) {
			commandDelete(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("rename")) {
			commandRename(arg0, arg1);
		} else if (arg1[0].toLowerCase().equals("save")) {
			Zones.instance.save();
			arg0.addChatMessage(new TextComponentString("[Zone] Saved"));
		} else if (arg1[0].toLowerCase().equals("list")) {
			commandList(arg0, arg1);
		}
	}

	/**
	 * Returns a list of all known zones to the user, or all the bounding boxes
	 * of a zone if a zone name is given (i.e "list My Zone")
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandList(ICommandSender arg0, String[] arg1) {
		String a = getRestOfString(arg1, 1);

		if (a.isEmpty()) {
			for (Zone z : Zones.instance.getZoneManager().getZones()) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone: " + z.getName()
						+ (z.getZoneCreator() == null ? "" : " (" + z.getZoneCreator() + ")")
						+ (z.isProtected() ? " (protected)" : "")));
			}
		} else {
			Zone z = Zones.instance.getZoneByName(a);
			if (z == null) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone + \"" + a + "\" not found."));
				return;
			}
			arg0.addChatMessage(
					new TextComponentString("[Zone] Zone: " + z.getName() + (z.isProtected() ? " (protected)" : "")));
			for (ZoneBoundingBox b : z.getBoxes()) {
				arg0.addChatMessage(new TextComponentString("  * " + b.toString()));
			}
		}
	}

	/**
	 * Deletes a zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandDelete(ICommandSender arg0, String[] arg1) {
		String a = getRestOfString(arg1, 1);

		Zone z = Zones.instance.getZoneByName(a);
		if (z == null) {
			arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" not found."));
		} else {
			Zones.instance.getZoneManager().delete(z);
			arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" deleted."));
		}
	}

	/**
	 * Renames a zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandRename(ICommandSender arg0, String[] arg1) {
		String a = getRestOfString(arg1, 1);
		String[] split = a.split(",");

		if (split.length != 2) {
			arg0.addChatMessage(new TextComponentString("[Zone] Input error."));
			return;
		}
		Zone z = Zones.instance.getZoneByName(split[0]);		
		if (z == null) {
			arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + split[0] + "\" not found."));
		} else {
			if (split[1].trim().isEmpty()) 
				arg0.addChatMessage(new TextComponentString("[Zone] New zone name invalid"));
			else {
				z.setName(split[1]);
				arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + split[0] + "\" renamed to \"" + split[1] + "\""));
			}
		}
	}
	
	/**
	 * Marks the outer edges of all bounding boxes of a zone with whatever item
	 * is in the players hand
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandMark(ICommandSender arg0, String[] arg1) {
		if (arg1.length > 0) {
			String a = getRestOfString(arg1, 1);
			Zone z = Zones.instance.getZoneByName(a);
			if (z == null) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" not found."));
			} else {
				EntityPlayerMP p = ((EntityPlayerMP) arg0);

				ItemStack item = p.getHeldItemMainhand();
				Block blockFromItem = Block.getBlockFromItem(item.getItem());

				item.stackSize--;
				for (ZoneBoundingBox b : z.getBoxes()) {
					p.worldObj.setBlockState(new BlockPos(b.eX, b.eY, b.eZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.eX, b.sY, b.eZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.sX, b.sY, b.eZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.sX, b.eY, b.eZ), blockFromItem.getDefaultState());

					p.worldObj.setBlockState(new BlockPos(b.eX, b.eY, b.sZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.eX, b.sY, b.sZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.sX, b.sY, b.sZ), blockFromItem.getDefaultState());
					p.worldObj.setBlockState(new BlockPos(b.sX, b.eY, b.sZ), blockFromItem.getDefaultState());
				}

				arg0.addChatMessage(new TextComponentString("[Zone] Zone " + a + " marked."));
			}
		}
	}

	/**
	 * Creates a (large) zone around a village. Not very good, don't use :)
	 * 
	 * @param arg0
	 */
	private void commandVillage(ICommandSender arg0) {
		System.out.println("Villages: ");
		// arg0.getEntityWorld().
		for (Object v : arg0.getEntityWorld().villageCollectionObj.getVillageList()) {
			Village va = (Village) v;
			System.out.println(va.getVillageRadius());			

			// if (arg1.length > 1) {
			BlockPos center = va.getCenter();
			int villageRadius = va.getVillageRadius()+1;

			EntityPlayerMP p = ((EntityPlayerMP) arg0);
			ItemStack item = p.getHeldItemMainhand();
			Block blockFromItem = Block.REGISTRY.getObject(new ResourceLocation("minecraft", "air"));
			if (item != null) {
				blockFromItem = Block.getBlockFromItem(item.getItem());	
			}
			

			System.out.println(va.getCenter());
			Zone z1 = new Zone("Village " + va.getNumVillagers(), arg0.getName());
			Zones.instance.addZone(z1);

			if (blockFromItem != null) {
				for (int rad = 0; rad <= villageRadius; rad++) {
					for (int i = 0; i < 360; i = i + 1) {
						int x = (int) ((Math.sin(i / Math.PI) * rad) + ((center.getX())));
						int z = (int) ((Math.cos(i / Math.PI) * rad) + ((center.getZ())));
						BlockPos y = arg0.getEntityWorld().getTopSolidOrLiquidBlock(new BlockPos(x, 1, z));

						z1.addBox(new ZoneBoundingBox(x / 16, (y.getY() - 1) / 16, z / 16));
						z1.addBox(new ZoneBoundingBox(x / 16, y.getY() / 16, z / 16));
						z1.addBox(new ZoneBoundingBox(x / 16, (y.getY() + 1) / 16, z / 16));
					}
				}
				for (int i = 0; i < 3600; i = i + 1) {
					// int x = (int) ((Math.sin(i / Math.PI) * villageRadius) +
					// ((center.getX())));
					// int z = (int) ((Math.cos(i / Math.PI) * villageRadius) +
					// ((center.getZ())));
					// BlockPos y =
					// arg0.getEntityWorld().getTopSolidOrLiquidBlock(new
					// BlockPos(x, 1, z));
					// if (!arg0.getEntityWorld().getBlock(x, y.getY() - 1,
					// z).getUnlocalizedName().equals(blockFromItem.getUnlocalizedName()))
					// {
					// arg0.getEntityWorld().setBlock(x, y, z, blockFromItem);
					// }

				}
			}
		}
	}

	/**
	 * Unprotects a given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandUnprotect(ICommandSender arg0, String[] arg1) {
		if (arg1.length > 0) {
			String a = getRestOfString(arg1, 1);
			Zone z = Zones.instance.getZoneByName(a);
			if (z == null) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" not found."));
			} else {
				z.setProtected(false);
				arg0.addChatMessage(new TextComponentString("[Zone] Zone " + a + " unprotected."));
			}
		}
	}

	/**
	 * Sets the name of the creator for a given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandSetCreator(ICommandSender arg0, String[] arg1) {
		if (arg1.length > 0) {
			String a = getRestOfString(arg1, 2);
			Zone z = Zones.instance.getZoneByName(a);
			if (z == null) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" not found."));
			} else {
				z.setZoneCreator(arg1[1]);
				arg0.addChatMessage(new TextComponentString("[Zone] Zone " + a + " now created by " + arg1[1] + "."));
			}
		}
	}

	/**
	 * Protects a given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandProtect(ICommandSender arg0, String[] arg1) {
		if (arg1.length > 0) {
			String a = getRestOfString(arg1, 1);
			Zone z = Zones.instance.getZoneByName(a);
			if (z == null) {
				arg0.addChatMessage(new TextComponentString("[Zone] Zone \"" + a + "\" not found."));
			} else {
				z.setProtected(true);
				arg0.addChatMessage(new TextComponentString("[Zone] Zone " + a + " protected."));

			}
		}
	}

	/**
	 * Creates a new zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandCreate(ICommandSender arg0, String[] arg1) {
		if (arg1.length > 0) {
			String a = getRestOfString(arg1, 1);
			arg0.addChatMessage(new TextComponentString("[Zone] Creating zone " + a));
			Zones.instance.addZone(new Zone(a, arg0.getName()));

		}
	}

	/**
	 * Removes a bounding box from a given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandRemove(ICommandSender arg0, String[] arg1) {
		String a = getRestOfString(arg1, 1);

		Zone z = Zones.instance.getZoneByName(a);

		if (z == null) {
			arg0.addChatMessage(new TextComponentString("[Zone] Could not find zone \"" + a + "\""));
		} else {
			EntityPlayer p = ((EntityPlayer) arg0);
			if (z.removeBox(new ZoneBoundingBox(p.chunkCoordX, p.chunkCoordY, p.chunkCoordZ))) {
				arg0.addChatMessage(new TextComponentString("[Zone] Removed"));
			} else {
				arg0.addChatMessage(new TextComponentString("[Zone] Could not find box "
						+ new ZoneBoundingBox(p.chunkCoordX, p.chunkCoordY, p.chunkCoordZ)));
			}
		}
	}

	/**
	 * Adds the chunk a player is in to the given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandAdd(ICommandSender arg0, String[] arg1) {
		if (!(arg0.getEntityWorld().provider instanceof WorldProviderSurface)) {
			arg0.addChatMessage(new TextComponentString("[Zone] Non-surface dimensions disallowed."));
			return;
		}

		int s = 1;
		int h1 = 0;
		int h2 = 0;

		if (arg1[0].toLowerCase().equals("addy")) {
			s = 3;
			try {
				h1 = Integer.valueOf(arg1[1]);
				h2 = Integer.valueOf(arg1[2]);
			} catch (NumberFormatException e) {
				arg0.addChatMessage(new TextComponentString("[Zone] Failed to parse height"));
				return;
			}
		}

		String a = getRestOfString(arg1, s);

		Zone z = Zones.instance.getZoneByName(a);

		if (z == null) {
			arg0.addChatMessage(new TextComponentString("[Zone] Could not find zone \"" + a + "\""));
		} else {
			EntityPlayer p = ((EntityPlayer) arg0);
			ZoneBoundingBox zB;
			if (arg1[0].toLowerCase().equals("addy")) {
				zB = new ZoneBoundingBox(p.chunkCoordX, h1, h2, p.chunkCoordZ);
			} else {
				zB = new ZoneBoundingBox(p.chunkCoordX, p.chunkCoordY, p.chunkCoordZ);
			}

			z.addBox(zB);

			arg0.addChatMessage(new TextComponentString("[Zone] Added " + zB + " to " + a));
		}
	}

	/**
	 * Adds a bounding box from two given points to a given zone
	 * 
	 * @param arg0
	 * @param arg1
	 */
	private void commandAddXYZ(ICommandSender arg0, String[] arg1) {
		if (!(arg0.getEntityWorld().provider instanceof WorldProviderSurface)) {
			arg0.addChatMessage(new TextComponentString("[Zone] Non-surface dimensions disallowed."));
			return;
		}

		int x1 = Integer.parseInt(arg1[1]);
		int y1 = Integer.parseInt(arg1[2]);
		int z1 = Integer.parseInt(arg1[3]);
		int x2 = Integer.parseInt(arg1[4]);
		int y2 = Integer.parseInt(arg1[5]);
		int z2 = Integer.parseInt(arg1[6]);
		String a = getRestOfString(arg1, 7);

		Zone z = Zones.instance.getZoneByName(a);
		if (z == null) {
			arg0.addChatMessage(new TextComponentString("[Zone] Could not find zone \"" + a + "\""));
		} else {
			ZoneBoundingBox zB = new ZoneBoundingBox(x1, y1, z1, x2, y2, z2);
			z.addBox(zB);
			arg0.addChatMessage(new TextComponentString("[Zone] Added " + zB + " to " + a));
		}
	}

	private String getRestOfString(String[] arg1, int s) {
		String a = "";
		for (int i = s; i < arg1.length; i++) {
			a = a.concat(arg1[i] + " ");
		}
		a = a.trim();
		return a;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}
}