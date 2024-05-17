package notebook.util;

public class UserValidator {
    public String isNameValid (String name){
        if (name.isEmpty()) throw new RuntimeException("Вы не ввели данные!");
        return name.trim().replaceAll(" ", "");
    }
}
