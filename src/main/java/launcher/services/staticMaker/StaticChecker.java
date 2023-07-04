package launcher.services.staticMaker;

public final class StaticChecker {
    private static StaticChecker staticChecker;
    private boolean isStatic = false;
    private StaticChecker() {}

    public static StaticChecker getStaticChecker() {
        if (staticChecker == null) {
            staticChecker = new StaticChecker();
            staticChecker.isStatic = false;
        }
        return staticChecker;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
}
