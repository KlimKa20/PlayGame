package by.bsuir.playgame.Enum;

import by.bsuir.playgame.R;

public enum TypeField {
    SHIP(R.drawable.ic_launcher_background,1), EMPTY(R.drawable._,0), HURT(R.drawable.common_google_signin_btn_icon_dark,2), DESTROY(R.drawable.square_png97,4), LOSE(R.drawable.square_png91,3);

    private final int codeImage;
    private final int codeField;

    TypeField(int codeImage,int codeField) {
        this.codeImage = codeImage;
        this.codeField = codeField;
    }

    public int getCodeImage() {
        return codeImage;
    }

    public int getCodeField() {
        return codeField;
    }


}
