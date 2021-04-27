package by.bsuir.playgame.Enum;

public enum ShipType {
//    олличество точек необходимое для расположения корабля
    ONE_SECTION(1),
    TWO_SECTION(2),
    THREE_SECTION(3),
    FOUR_SECTION(4);
    private final int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
