package me.florian.command.brigadier.datatypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.florian.command.brigadier.BrigadierCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class WorldCoordinates extends Coordinates {

    private static class CoordinateParseResult {
        final double coordinate;
        final boolean relative;

        private static double parseDouble(String input) {
            if (input.length() == 0) {
                return 0d;
            }

            return Double.parseDouble(input);
        }

        static CoordinateParseResult parse(String input) {
            if (input.startsWith("~")) {
                return new CoordinateParseResult(parseDouble(input.substring(1)), true);
            } else {
                return new CoordinateParseResult(parseDouble(input), false);
            }
        }

        private CoordinateParseResult(double coordinate, boolean isRelative) {
            this.coordinate = coordinate;
            this.relative = isRelative;
        }
    }

    private double x, y, z;
    private boolean xRelative, yRelative, zRelative;

    public WorldCoordinates(StringReader reader) throws CommandSyntaxException {
        readCoordinate(reader, x -> this.x = x, f -> this.xRelative = f);
        reader.skipWhitespace();
        readCoordinate(reader, y -> this.y = y, f -> this.yRelative = f);
        reader.skipWhitespace();
        readCoordinate(reader, z -> this.z = z, f -> this.zRelative = f);

        System.out.println(this);
    }

    private void readCoordinate(StringReader reader, DoubleConsumer coordinateSetter, Consumer<Boolean> flagSetter) throws CommandSyntaxException {
        if (reader.peek() == '~') {
            reader.skip();

            flagSetter.accept(true);

            if (StringReader.isAllowedNumber(reader.peek())) {
                double read = reader.readDouble();
                coordinateSetter.accept(read);

                System.out.println("Relative coordinate: ~" + read);
            } else {
                coordinateSetter.accept(0d);
                System.out.println("Relative coordinate: ~0");
            }
        } else {
            double read = reader.readDouble();
            coordinateSetter.accept(read);

            System.out.println("Absolute coordinate: " + read);
        }
    }

    @Override
    public Location toLocation(CommandSender source) throws CommandSyntaxException {
        if (xRelative | yRelative | zRelative) {
            if (!(source instanceof LivingEntity)) {
                throw BrigadierCommand.literal("You may not use relative coordinates!");
            }

            Location entityLocation = ((LivingEntity) source).getLocation();
            Location location = new Location(null, Double.NaN, Double.NaN, Double.NaN);

            location.setX(x + (xRelative ? entityLocation.getX() : 0));
            location.setY(y + (yRelative ? entityLocation.getY() : 0));
            location.setZ(z + (zRelative ? entityLocation.getZ() : 0));

            return location;
        }

        return new Location(null, x, y, z);
    }

    @Override
    public String toString() {
        return "WorldCoordinates[" + (xRelative ? "~" : "") + x + ", " + (yRelative ? "~" : "") + y + ", " + (zRelative ? "~" : "") + z + "]";
    }
}
