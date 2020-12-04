package by.bsuir.playgame.Enum;

public enum StatusGame {
    PLACEMENT_START("Placement Start"),
    START_GAME("Start Game"),
    FINISH_GUEST("Finish guest"),
    FINISH_HOST("Finish host"),
    WAITING_SECOND_PLAYER("Waiting second player"),
    GUEST("guest"),
    HOST("host");

    private final String name;

    StatusGame(String status) {
        this.name = status;
    }

    public String getName() {
        return name;
    }
}
