package com.example.jsondbHandling;


import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;
import static java.util.Collections.reverseOrder;
import java.util.stream.Collectors;


import org.json.simple.JSONObject;

@RestController
public class jsonDbHandling {

    @Value("${app.filedir}")
    String path;

    @Value("${app.filename}")
    String name;


    @RequestMapping("/")
    public String FileCreation(){
        System.out.println(path);
        System.out.println(name);
        System.out.println(path+name);
        File f = new File(path+name);

        String response="File for DataBase Operations Is Ready!";
        if(!f.exists()){
            try {
                f.createNewFile();
                response="File for DataBase Operations Is Ready!";
            }catch (IOException x){
                    x.printStackTrace();
                    response="0ops..Something went wrong";
            }
        }
        return response;
    }

    @RequestMapping(value="/create",consumes = "application/json")
    public String create(@RequestBody employee body) {
        File f = new File(path+name);

        String response=null;
        JSONObject obj1=new JSONObject();
        JSONArray Array1=new JSONArray();
        obj1.put("id", body.getId());
        obj1.put("fullName", body.getFullName());
        obj1.put("age", body.getAge());
        obj1.put("salary", body.getSalary());
        Array1.add(obj1);
        JSONObject obj2=new JSONObject();
        obj2.put(body.getId(),Array1);
        if(f.exists()){
            f.setWritable(true);
            try {
                JSONParser parser = new JSONParser();
                if(f.length()!=0){
                Object existingRecords = parser.parse(new FileReader(f));
                    JSONObject jsonObject = (JSONObject) existingRecords;
                    jsonObject.put(body.getId(),Array1);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                    writer.append(jsonObject.toJSONString());
                    writer.flush();
                    writer.close();
                    response="New Data Inserted Successfully!";
                }else {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                    writer.append(obj2.toJSONString());
                    writer.flush();
                    writer.close();
                    response = "New Data Inserted Successfully!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                response="0ops..Something went wrong";
            }
        }else{
            FileCreation();
            response="Something went wrong, Please try again to insert the data";
        }

        return response;
    }

    @RequestMapping(value="/delete")
    public String delete(@RequestParam("id") String id){
        File f = new File(path+name);

        String response="No Record Found to delete";
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader(f));
            JSONObject jsonObject = (JSONObject) obj;
            List<String> parameterKeys = new ArrayList<String>(jsonObject.keySet());
            for (String str : parameterKeys) {
                if(id.equalsIgnoreCase(str)){
                    response=jsonObject.get(str).toString()+" ===> Record deleted";
                    jsonObject.remove(str);
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.append(jsonObject.toJSONString());
            writer.flush();
            writer.close();

        }
        catch(Exception x){
            x.printStackTrace();
            response="0ops..Something went wrong";
        }

        return response;
    }

    @RequestMapping(value="/update",consumes = "application/json")
    public String update(@RequestParam("id") String id,@RequestBody JSONObject js){
        File f = new File(path+name);

        String response=null;
        JSONArray jr=new JSONArray();


        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader(f));
            JSONObject jsonObject = (JSONObject) obj;
            List<String> parameterKeys = new ArrayList<String>(jsonObject.keySet());
            for (String str : parameterKeys) {
                if(id.equalsIgnoreCase(str)){
                    js.put("id",str);
                    jr.add(js);
                    jsonObject.replace(str,jr);
                    response=jsonObject.get(str).toString()+" ===> Record Updated";
            }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.append(jsonObject.toJSONString());
            writer.flush();
            writer.close();
        }catch (Exception x){
            x.printStackTrace();
            response="0ops.. Something went wrong!";

        }

        return response;
    }


    @RequestMapping(value="/filterByAge",consumes = "application/json")
    public String filterByAge(@RequestBody JSONObject js){
        String response="";
        File f = new File(path+name);
        String operator= js.get("operator").toString();
        String value=js.get("value").toString();
        String sort=js.get("sort").toString();
        JSONParser parser = new JSONParser();
        LinkedHashMap<String,Integer> searchedResults=new LinkedHashMap<String, Integer>();
        List<Map.Entry<String, Integer>> sortedMap = null;

        try{
            Object obj = parser.parse(new FileReader(f));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject childObject=null;
            List<String> parameterKeys = new ArrayList<String>(jsonObject.keySet());
            for (String str : parameterKeys) {
            if(jsonObject.get(str) instanceof JSONObject){
                childObject=(JSONObject) jsonObject.get(str);
            }else if(jsonObject.get(str) instanceof JSONArray){
                JSONArray array1=(JSONArray) jsonObject.get(str);
                childObject=(JSONObject) array1.get(0);
            }
                switch (operator){
                    case "lt":
                        if(Integer.parseInt(childObject.get("age").toString())< Integer.parseInt(value)){
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                        break;
                    case "lte":
                        if(Integer.parseInt(childObject.get("age").toString())<= Integer.parseInt(value)) {
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                            break;
                    case "gt":
                        if(Integer.parseInt(childObject.get("age").toString())> Integer.parseInt(value)) {
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                            break;
                    case "gte":
                        if(Integer.parseInt(childObject.get("age").toString())>= Integer.parseInt(value)) {
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                            break;
                    case "eq":
                        if(Integer.parseInt(childObject.get("age").toString())==Integer.parseInt(value)) {
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                            break;
                    case "ne":
                        if(Integer.parseInt(childObject.get("age").toString())!= Integer.parseInt(value)) {
                            searchedResults.put(str,Integer.parseInt(childObject.get("age").toString()));
                        }
                            break;

                    default:
                        break;
                }

            switch (sort){
                case "asc":
                    sortedMap =
                            searchedResults.entrySet()
                                    .stream()
                                    .sorted(Map.Entry.comparingByValue())
                                    .collect(Collectors.toList());
                    break;
                case "desc":
                    sortedMap =
                            searchedResults.entrySet()
                                    .stream()
                                    .sorted(reverseOrder(Map.Entry.comparingByValue()))
                                    .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
            }
            
            System.out.println( "Sorted===>"+sortedMap);
            Object obj2 = parser.parse(new FileReader(f));
            JSONObject jsonObject2 = (JSONObject) obj2;
            List<String> parameterKeys2 = new ArrayList<String>(jsonObject2.keySet());

                for(int k=0;k<sortedMap.size();k++){
                    for (String str2 : parameterKeys2) {
                    if(str2.equalsIgnoreCase(sortedMap.get(k).getKey())){
                        response+=jsonObject2.get(str2).toString();
                        response+="\n";
                        System.out.println( "res===>"+response);
                    }
                }
            }
        }catch (Exception x){
            x.printStackTrace();
            response="0ops.. Something went wrong";
        }
        return response;
    }

}
