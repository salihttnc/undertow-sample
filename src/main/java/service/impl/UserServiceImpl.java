package service.impl;

import model.User;

import java.util.List;

public interface UserServiceImpl {
     List<User> getUsers();
     void delete(String id);
     User addDataBase(String id);
     User addDataBaseWithJSON(String value);
}
