package cassandra;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import model.User;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.mapping.MappingManager;
import javax.ejb.Startup;

/**
 *
 * @author b1r
 */
@Startup
@Singleton
public class ClusterService {

    public static final String KEYSPACE_REGEX = "^[a-z_]*[^-]$";

    // mandatory environment settings
    public static final String CASSANDRA_CONTACTPOINTS = "CASSANDRA_CONTACTPOINTS";
    public static final String CASSANDRA_KEYSPACE = "CASSANDRA_KEYSPACE";
    public static final String CASSANDRA_PORT = "CASSANDRA_PORT";
    // optional environment settings
    public static final String CASSANDRA_REPLICATION_FACTOR = "CASSANDRA_REPLICATION_FACTOR";
    public static final String CASSANDRA_REPLICATION_CLASS = "CASSANDRA_REPLICATION_CLASS";




    private String repFactor="3";


    private String repClass="SimpleStrategy";


    private  final String contactPoint="localhost";

    private String keySpace="deneme";

    private int port=9042;

    private Cluster cluster;
    private Session session;
    private MappingManager manager;

    @PostConstruct
    private void init() {
        try {
            cluster = initCluster();
            session = initArchiveSession();
            manager = new MappingManager(session);
        } catch (ArchiveException e) {

            e.printStackTrace();
        }
    }

    @PreDestroy
    private void tearDown() {
        // close session and cluster object
        if (session != null) {
            session.close();
        }
        if (cluster != null) {
            cluster.close();
        }
    }

    public Session getSession() {
        if (session == null) {
            init();
        }
        return session;
    }
    public MappingManager getMappingManager() {
        if (manager == null) {
            init();
        }
        return manager;
    }

    /**
     * @throws ArchiveException
     */
    private Session initArchiveSession() throws ArchiveException {

        if (!isValidKeyspaceName(keySpace)) {
            throw new ArchiveException(ArchiveException.INVALID_KEYSPACE, "keyspace '" + keySpace + "' name invalid.");
        }

        // try to open keySpace

        try {
            session = cluster.connect(keySpace);
        } catch (InvalidQueryException e) {

            // create keyspace...
            session = createKeySpace(keySpace);
        }
        if (session != null) {

        }
        return session;
    }

    /**
     * @return Cassandra Cluster instacne
     * @throws ArchiveException
     */
    protected Cluster initCluster() throws ArchiveException {



       cluster = Cluster.builder().addContactPoint("localhost")
                .withPort(9042).build();
        //cluster = Cluster.builder().addContactPoint(contactPoint).build();
        cluster.init();



        return cluster;

    }

    /**
     * Test if the keyspace name is valid.
     *
     * @param keySpace
     * @return
     */
    public boolean isValidKeyspaceName(String keySpace) {
        if (keySpace == null || keySpace.isEmpty()) {
            return false;
        }

        return keySpace.matches(KEYSPACE_REGEX);

    }


    protected Session createKeySpace(String keySpace) throws ArchiveException {

        Session session = cluster.connect();

        String statement = "CREATE KEYSPACE IF NOT EXISTS " + keySpace + " WITH replication = {'class': '" + repClass
                + "', 'replication_factor': " + repFactor + "};";

        session.execute(statement);

        // try to connect again to keyspace...
        session = cluster.connect(keySpace);
        if (session != null) {

            // now create table schemas
            //createTableSchema(session);
        }

        return session;

    }


    protected void createTableSchema(Session session) {

        session.execute(CassandraScriptGeneratorFromEntities.convertEntityToSchema(User.class, session.getLoggedKeyspace()).toString());
    }

}