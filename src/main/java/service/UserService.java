package service;

import model.User;
import cassandra.repositories.UserRepository;
import org.json.simple.JSONObject;
import service.impl.UserServiceImpl;

import java.util.List;

public class UserService  implements UserServiceImpl {
    JSONParseService jsonParseService=new JSONParseService();
    @Override
    public List<User> getUsers() {
        UserRepository userRepository = new UserRepository();
        return userRepository.findAll();
    }

    @Override
    public void delete(String id) {
        UserRepository userRepository = new UserRepository();
        userRepository.delete(id);


    }

    @Override
    public User addDataBase(String key) {
        // elle ekleme örnegi
        UserRepository userRepository = new UserRepository();
        User user = new User();
        user.setEmail(key);
        user.setName("asda123123123");
        user.setSurname("asdsadas213123");
        user.setUserName("asdasdasd2131231");
        userRepository.save(user);
        return user;
    }

    @Override
    public User addDataBaseWithJSON(String value) {
        // json veri alıp parse edildikten sonra veri tabanına kayıt
        /*
        JSON ÖRNEGİ
        {
	"username":"Json username4",
	"email":"easdsa@gmail.com",
	"name":"salih",
	"surname":"tutuncu"
}
         */
            JSONObject jsonObject=jsonParseService.fromJSON(value);
            UserRepository userRepository = new UserRepository();
            User user = new User();
            user.setEmail(jsonObject.get("email").toString());
            user.setName(jsonObject.get("name").toString());
            user.setSurname(jsonObject.get("surname").toString());
            user.setUserName(jsonObject.get("username").toString());
            userRepository.save(user);
            return user;
        }



}
