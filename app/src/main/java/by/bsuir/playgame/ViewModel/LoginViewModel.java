package by.bsuir.playgame.ViewModel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.R;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> PasswordError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>();

    ModelFirebaseAuth firebaseAuth;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new ModelFirebaseAuth();
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPasswordError() {
        return PasswordError;
    }

    public LiveData<Boolean> isSuccessful() {
        return isSuccessful;
    }

    public void Login(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailError.setValue(getApplication().getString(R.string.enter_your_email));
            return;
        } else if (TextUtils.isEmpty(password)) {
            PasswordError.setValue(getApplication().getString(R.string.enter_your_password));
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> isSuccessful.setValue(task.isSuccessful()));
    }
}
