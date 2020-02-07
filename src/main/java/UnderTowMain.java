import api.UndertowController;


public class UnderTowMain {


    public static void main(final String[] args) {

        UndertowController undertowController =new UndertowController();
        undertowController.start();
    }

}

/*
  Cluster cluster = Cluster.builder().addContactPoint("localhost")
                .withPort(9042).build();
        Session session = cluster.connect("deneme");


        ResultSet results = session.execute("SELECT * FROM users WHERE name='Jones' ALLOW FILTERING");
        for (Row row : results) {
            System.out.format("%s\n", row.getString("name"));
        }
        String s="INSERT INTO users (id,name) VALUES (7,'salih')";
                                        System.out.println(s.equals("INSERT INTO users (id,name) VALUES (7,"+"'"+apiKey+"')"));
                                        session.execute("INSERT INTO users (id,name) VALUES (7,"+"'"+apiKey+"')");
                                         session.close();
 */