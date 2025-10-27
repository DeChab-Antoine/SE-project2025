package main.bench;

enum Profile {
    CAMPAGNE_4G("CAMPAGNE_4G", 5_000_000, 30_000_000),
    WIFI_MAISON("WIFI_MAISON", 50_000_000, 5_000_000),
    FIBRE_BUREAU("FIBRE_BUREAU", 100_000_000, 1_000_000),
    FIBRE_DOMESTIQUE("FIBRE_DOMESTIQUE", 1_000_000_000, 200_000),
    SATELLITE("SATELLITE", 25_000_000, 150_000_000);

    final String label;
    final int bps;
    final int latencyNs;

    Profile(String label, int bps, int latencyNs) {
        this.label = label;
        this.bps = bps;
        this.latencyNs = latencyNs;
    }
}

