import java.io.Serializable;

public class Product implements Serializable
{
    private String ID;
    private String name;
    private String description;
    double cost = 0;

    static private int IDSeed = 1;

    // Fixed-length constants for RandomAccessFile
    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN = 6;
    // 116 chars * 2 bytes/char + 8 bytes for double = 240 bytes per record
    public static final int RECORD_SIZE = (NAME_LEN + DESC_LEN + ID_LEN) * 2 + 8;

    public static void setIDSeed(int IDSeed) {
        Product.IDSeed = IDSeed;
    }

    public static int getIDSeed() {
        return IDSeed;
    }

    public Product(String ID, String name, String description, double cost)
    {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.cost = cost;

        Product.IDSeed++;
    }

    public Product(String name, String description, double cost)
    {
        this.ID = this.genID();
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    private String genID() {
        String newID = "" + IDSeed;
        while(newID.length() < 6)
        {
            newID = "0" + newID;
        }

        IDSeed++;

        return newID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    // Utility method to pad or truncate strings to exact fixed lengths
    public static String padString(String str, int length) {
        if (str == null) {
            str = "";
        }
        if (str.length() > length) {
            return str.substring(0, length);
        }
        return String.format("%-" + length + "s", str);
    }

    // Utility methods returning correctly padded strings for RandomAccessFile
    public String getFixedID() {
        return padString(this.ID, ID_LEN);
    }

    public String getFixedName() {
        return padString(this.name, NAME_LEN);
    }

    public String getFixedDescription() {
        return padString(this.description, DESC_LEN);
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID='" + ID.trim() + '\'' +
                ", name='" + name.trim() + '\'' +
                ", description='" + description.trim() + '\'' +
                ", cost=" + cost +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return cost == product.cost && ID.equals(product.ID) && name.equals(product.name) && description.equals(product.description);
    }

    public String toJSONRecord()
    {
        String retString = "";
        char DQ = '\u0022';
        retString =  "{" + DQ + "ID" + DQ + ":" + DQ + this.ID + DQ + ",";
        retString += DQ + "name" + DQ + ":" + DQ + this.name + DQ + ",";
        retString += " " + DQ + "description"  + DQ + ":" + DQ + this.description + DQ + ",";
        retString += " " + DQ + "cost"  + DQ + ":" + this.cost + "}";

        return retString;
    }

    public String toXMLRecord()
    {
        String retString = "";

        retString = "<Product>" + "<ID>" + this.ID + "</ID>";
        retString += "<name>" + this.name + "</name>";
        retString += "<description>" + this.description + "</description>";
        retString += "<cost>" + this.cost + "</cost></Product>";

        return retString;
    }

    public String toCSVRecord() {
        return this.ID + ", " + this.name + "," + this.description + ", " + cost;
    }
}