package by.bsuir.playgame.ViewModel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;
import by.bsuir.playgame.R;

public class RegistratioViewModel extends AndroidViewModel {

    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> Password1Error = new MutableLiveData<>();
    private final MutableLiveData<String> Password2Error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();

    ModelFirebaseAuth firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;


    public RegistratioViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new ModelFirebaseAuth();
        firebaseDatabase = new ModelFirebaseDatabase();
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPassword1Error() {
        return Password1Error;
    }

    public LiveData<String> getPassword2Error() {
        return Password2Error;
    }

    public LiveData<Boolean> isSuccessful() {
        return isSuccessful;
    }


    public void Register(String email, String password1, String password2) {
        if (TextUtils.isEmpty(email)) {
            emailError.setValue(getApplication().getString(R.string.Enter_your_Email));
            return;
        } else if (TextUtils.isEmpty(password1)) {
            Password1Error.setValue(getApplication().getString(R.string.Enter_your_Password));
            return;
        } else if (TextUtils.isEmpty(password2)) {
            Password2Error.setValue(getApplication().getString(R.string.Confirm_your_Password));
            return;
        } else if (!password1.equals(password2)) {
            Password2Error.setValue(getApplication().getString(R.string.error_password));
            return;
        } else if (password1.length() < 4) {
            Password1Error.setValue(getApplication().getString(R.string.short_password));
            return;
        } else if (!isValidEmail(email)) {
            emailError.setValue(getApplication().getString(R.string.Invalid_email));
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> values = new HashMap<>();
                values.put("Gravatar", false);
                values.put("userName", firebaseAuth.getUIDUser());
                values.put("Image", "https://firebasestorage.googleapis.com/v0/b/playgamekl.appspot.com/o/image%2F1606652344403.png?alt=media&token=e5c51ce4-3939-490e-9d4f-fc5ddeef127e");
                firebaseDatabase.updateChild("Users/" + firebaseAuth.getUIDUser(), values);
                isSuccessful.setValue(true);
            } else {
                isSuccessful.setValue(false);
            }
        });
    }

    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
