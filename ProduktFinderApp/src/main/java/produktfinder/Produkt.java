package produktfinder;

public class Produkt {
    private String name;
    private String bildpfad;
    private String regal;
    private double preis;

    private String kategorie;

    public Produkt(String name, String bildpfad, String regal, double preis, String kategorie) {
        this.name = name;
        this.bildpfad = bildpfad;
        this.regal = regal;
        this.preis = preis;
        this.kategorie = kategorie;
    }

    public String getName() {
        return name;
    }

    public String getBildpfad() {
        return bildpfad;
    }

    public String getRegal() {
        return regal;
    }

    public double getPreis() {
        return preis;
    }

    public String getKategorie() {
        return kategorie;
    }

}
