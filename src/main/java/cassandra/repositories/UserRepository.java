package cassandra.repositories;

import cassandra.CassandraScriptGeneratorFromEntities;
import cassandra.ClusterService;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

import model.User;

public class UserRepository {


    private Mapper<User> mapper;
    private Session session;
    private static final String TABLE = "users";
    ;
    ClusterService clusterService = new ClusterService();


    public UserRepository() {
        createTable(clusterService.getMappingManager().getSession());
        this.mapper = clusterService.getMappingManager().mapper(User.class);
        this.session = clusterService.getMappingManager().getSession();

    }

    private void createTable(Session session) {
        session.execute(CassandraScriptGeneratorFromEntities.convertEntityToSchema(User.class, session.getLoggedKeyspace()).toString());
    }

    public User save(User user) {
        mapper.save(user);
        return user;
    }

    public List<User> findAll() {
        final ResultSet result = session.execute(select().all().from(TABLE));
        return mapper.map(result).all();
    }

    public User addScore(String objectid, String userid) {
        final ResultSet result = session.execute(select().all().from(TABLE).where(eq("objectid", objectid)).and(eq("userid", userid)).allowFiltering());
        return mapper.map(result).one();
    }

    public List<User> findAll2(String id) {
        final ResultSet result = session.execute(select().all().from(TABLE).where(eq("userid", id)).allowFiltering());
        return mapper.map(result).all();
    }

    public void delete(String id) {
        mapper.delete(id);
    }

    public User find(String id) {
        return mapper.get(id);
    }
}
