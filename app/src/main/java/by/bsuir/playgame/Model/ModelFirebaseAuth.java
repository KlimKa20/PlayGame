package by.bsuir.playgame.Model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ModelFirebaseAuth {
    private final FirebaseAuth firebaseAuth;

    public ModelFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public String getUIDUser() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getEmail() {
        return firebaseAuth.getCurrentUser().getEmail();
    }


    public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

}
