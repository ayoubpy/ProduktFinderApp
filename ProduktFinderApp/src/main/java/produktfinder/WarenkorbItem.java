package produktfinder;

public class WarenkorbItem {
    private String produktName;
    private double menge;
    private double gesamtpreis;
    private String regal;

    public WarenkorbItem(String produktName, double menge, double gesamtpreis, String regal) {
        this.produktName = produktName;
        this.menge = menge;
        this.gesamtpreis = gesamtpreis;
        this.regal = regal;
    }

    public String getProduktName() { return produktName; }
    public double getMenge() { return menge; }
    public double getGesamtpreis() { return gesamtpreis; }
    public String getRegal() { return regal; }
}
