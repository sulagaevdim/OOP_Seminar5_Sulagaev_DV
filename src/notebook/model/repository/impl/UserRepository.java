package notebook.model.repository.impl;


import notebook.util.DBConnector;
import notebook.util.UserValidator;
import notebook.util.mapper.impl.UserMapper;
import notebook.model.User;
import notebook.model.repository.GBRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserRepository implements GBRepository {
    private final UserMapper mapper;


    public UserRepository() {
        this.mapper = new UserMapper();

    }

    @Override
    public List<User> findAll() {
        List<String> lines = readAll();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        return users;
    }
    public void userValid(User user){
        UserValidator validator = new UserValidator();
        user.setFirstName(validator.isNameValid(user.getFirstName()));
        user.setLastName(validator.isNameValid(user.getLastName()));
    }
    @Override
    public User create(User user) {
        userValid(user);
        List<User> users = findAll();
        long max = 0L;
        for (User u : users) {
            long id = u.getId();
            if (max < id){
                max = id;
            }
        }
        long next = max + 1;
        user.setId(next);
        users.add(user);
        write(users);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        User editUser = users.stream()
                .filter(u -> u.getId()
                        .equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        if (!update.getFirstName().isEmpty()) editUser.setFirstName(update.getFirstName());
        if (!update.getLastName().isEmpty()) editUser.setLastName(update.getLastName());
        if (!update.getPhone().isEmpty()) editUser.setPhone(update.getPhone());
        write(users);
        return Optional.of(update);
    }
    public boolean delete(Long userId) {
        List<User> users = findAll();
        User editUser = users.stream()
                .filter(u -> u.getId()
                        .equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        users.remove(userId.intValue() - 1);
        if ((userId.intValue() - 1) < users.size()) {
            System.out.println((userId.intValue()) + " " + (users.size()));
            for (int i = userId.intValue() - 1; i < users.size(); i++) {
                System.out.println("ЫЫЫ");
                users.get(i).changeIdAfterDeletion();
            }
        }
        write(users);
        return false;
    }
//
//    @Override
//    public boolean delete(Long id) {
//        return false;
//    }

    private void write(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u: users) {
            lines.add(mapper.toInput(u));
        }
        saveAll(lines);
    }
    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(DBConnector.DB_PATH);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
            }
            while (line != null) {
                // считываем остальные строки в цикле
                line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void saveAll(List<String> data) {
        try (FileWriter writer = new FileWriter(DBConnector.DB_PATH, false)) {
            for (String line : data) {
                // запись всей строки
                writer.write(line);
                // запись по символам
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public User createUser() {
        String firstName = prompt("Имя: ");
        String lastName = prompt("Фамилия: ");
        String phone = prompt("Номер телефона: ");
        return new User(firstName, lastName, phone);
    }
    public String prompt(String message) {
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        return in.nextLine();
    }
}
