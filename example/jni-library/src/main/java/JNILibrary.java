public class JNILibrary {
    static {
        System.load(System.getProperty("user.dir") + "/src/main/resources/" + System.mapLibraryName("jnilibrary"));
    }

    public static native int callLibrary(int i);

    public static void main(String[] args) {
        System.out.println(callLibrary(200));
    }
}
