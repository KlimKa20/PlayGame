package by.bsuir.playgame;

public class Statistics {

    private String p1Name;
    private String p2Name;
    private String nameRoom;
    private boolean status;
    private int p1Ship;
    private int p2Ship;

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

    public void setP1Name(String p1Name) {
        this.p1Name = p1Name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getP2Name() {
        return p2Name;
    }

    public void setP2Name(String p2Name) {
        this.p2Name = p2Name;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public int getP1Ship() {
        return p1Ship;
    }

    public void setP1Ship(int p1Ship) {
        this.p1Ship = p1Ship;
    }

    public int getP2Ship() {
        return p2Ship;
    }

    public void setP2Ship(int p2Ship) {
        this.p2Ship = p2Ship;
    }
}
