package service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONParseService {

    public JSONObject fromJSON(String value){
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(value);
            System.out.println(json.get("id"));
            return json;
        } catch (ParseException exc) {
            exc.printStackTrace();
            return null;
        }


    }
}
