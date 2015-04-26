# minecraft-zones
A Minecraft mod that allows for creation of named zones (client UI overlay) and (optionally) protection from mob spawning in the zones

## Instructions
To create a new zone:
> /zone create Name of my zone

To add the current chunk (16,16,16 size area as defined by Minecraft):
> /zone add Name of my zone

To add the current chunk with a specific height:
> /zone addy lowY highY Name of my zone

To add a specified area:
> /zone addxyz x1 y1 z1 x2 y2 z2 Name of my zone

To list all zones:
> /zone list

To remove the area your are in from a zone:
> /zone remove Name of my zone

To delete zone:
> /zone delete Name of my zone
