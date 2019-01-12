package tiled;

public class TiledToGameMakerConverter {

    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Invalid number of arguments: " + args.length + ". Expecting 1.");
            System.exit(1);
        }
        String mapfname = args[0];
    }
}
