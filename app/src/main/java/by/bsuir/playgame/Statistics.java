package by.bsuir.playgame;

public class Statistics {

    private final String p1Name;
    private final String p2Name;
    private final String nameRoom;
    private final boolean status;
    private final int p1Ship;
    private final int p2Ship;

    public Statistics(String p1Name, String p2Name, String nameRoom, boolean status, int p1Ship, int p2Ship) {
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.nameRoom = nameRoom;
        this.status = status;
        this.p1Ship = p1Ship;
        this.p2Ship = p2Ship;
    }

    public String getP1Name() {
        return p1Name;
    }

    public boolean isStatus() {
        return status;
    }

    public String getP2Name() {
        return p2Name;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public int getP1Ship() {
        return p1Ship;
    }

    public int getP2Ship() {
        return p2Ship;
    }

}
