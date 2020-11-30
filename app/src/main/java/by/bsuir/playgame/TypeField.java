package by.bsuir.playgame;

public enum TypeField {
    SHIP(R.drawable.ic_launcher_background), EMPTY(R.drawable._), HURT(R.drawable.common_google_signin_btn_icon_dark), DESTROY(R.drawable.square_png97), LOSE(R.drawable.square_png91);
    private int code;

    TypeField(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
