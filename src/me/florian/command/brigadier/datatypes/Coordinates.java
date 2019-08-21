package me.florian.command.brigadier.datatypes;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public abstract class Coordinates {

    public abstract Location toLocation(CommandSender source) throws CommandSyntaxException;

}
