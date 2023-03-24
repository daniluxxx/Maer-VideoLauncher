package launcher.model;

public enum MaerResolutions {

    //регионы
    PERM ("1280 640", "Perm"),
    TYUMEN ("1088 1280", "Tyumen"),


    //moscow
    MOSCOW_VARSHAVKA ("1248 672", "Varshavka"),
    MOSCOW_KHANOI ("816 1408", "Khanoi"),
    MOSCOW_RIVIERA_192 ("192 416", "RivieraVertical"),
    MOSCOW_RIVIERA_1312 ("1312 288", "RivieraHorizontal"),
    MOSCOW_RIVIERA_640 ("640 288", "RivieraHorizontal"),
    MOSCOW_RIVIERA_1952 ("1952 288", "RivieraHorizontal"),
    MOSCOW_RIVIERA_768 ("768 416", "RivieraVertical"),
    MOSCOW_OKEANIA_4864 ("4864 144", "Okeania"),
    MOSCOW_OKEANIA_608 ("608 704", "Okeania"),
    MOSCOW_OKEANIA_544 ("544 384", "Okeania"),
    MOSCOW_KHOROSHO_704 ("704 464", "Khorosho"),
    MOSCOW_KHOROSHO_672 ("672 184", "Khorosho"),
    MOSCOW_SHOKOLAD ("1056 608", "Shokolad"),
    MOSCOW_SALUT ("960 2016", "Salut"),
    MOSCOW_LENINGRADKA ("448 1024", "Leningradka"),
    MOSCOW_KASHIRKA_1344 ("1344 1152", "Kashirka"),
    MOSCOW_KASHIRKA_1888 ("1888 896", "Kashirka"),
    MOSCOW_KASHIRKA_2016 ("2016 896", "Kashirka"),
    MOSCOW_KASHIRKA_3904 ("3904 896", "Kashirka"),
    MOSCOW_VELOZAVODSKAYA_1696 ("1696 960", "Velozavodskaya"),
    MOSCOW_VELOZAVODSKAYA_1312 ("1312 960", "Velozavodskaya"),
    MOSCOW_VELOZAVODSKAYA_3008 ("3008 960", "Velozavodskaya"),
    MOSCOW_VOLGOGRADKA_2304 ("2304 1600", "Volgogradka"),
    MOSCOW_TULSKAYA ("720 760", "Tulskaya"),
    MOSCOW_IBIS ("4200 600", "Ibis"),
    MOSCOW_KONSTRUKTOR ("960 288", "Konstruktor");


    private String resolution;
    private String address;

    MaerResolutions(String resolution, String address) {
        this.resolution = resolution;
        this.address = address;
    }

    public String getResolution() {
        return resolution;
    }
    public String getAddress() {
        return address;
    }
}
