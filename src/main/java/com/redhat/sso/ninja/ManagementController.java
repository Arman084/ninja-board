package com.redhat.sso.ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.redhat.sso.ninja.chart.Chart2Json;
import com.redhat.sso.ninja.chart.DataSet2;
import com.redhat.sso.ninja.utils.Json;
import com.redhat.sso.ninja.utils.MapBuilder;

@Path("/")
public class ManagementController {
  private static final Logger log=Logger.getLogger(ManagementController.class);
  /**
   * 
   * config/load
   * users/register
   * users/get?id=?
   * users/list
   * users/delete?id=?
   * leaderboard/{max}
   * scorecards/list
   * @throws IOException 
   * @throws JsonMappingException 
   * @throws JsonGenerationException 
   * 
   */
  
  public static void main(String[] asd) throws JsonGenerationException, JsonMappingException, IOException{
//    System.out.println(new ManagementController().register(null,null,null,"[{\"displayName\": \"Mat Allen\",\"username\": \"mallen\",\"trelloId\":\"mallen2\",\"githubId\":\"matallen\"}]"));
    System.out.println(new ManagementController().getScorecards().getEntity());
    
//    System.out.println(new ManagementController().toNextLevel("BLUE", 7).toString());
  }
  
  @GET
  @Path("/config/get")
  public Response configGet(@Context HttpServletRequest request,@Context HttpServletResponse response,@Context ServletContext servletContext) throws JsonGenerationException, JsonMappingException, IOException{
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(Config.get())).build();
  }
  
  @POST
  @Path("/config/save")
  public Response configSave(@Context HttpServletRequest request,@Context HttpServletResponse response,@Context ServletContext servletContext) throws JsonGenerationException, JsonMappingException, IOException{
    System.out.println("Saving config");
    Config newConfig=Json.newObjectMapper(true).readValue(request.getInputStream(), Config.class);
    
    System.out.println("New Config = "+Json.newObjectMapper(true).writeValueAsString(newConfig));
//    System.out.println("heartbeat.intervalInSeconds="+newConfig.getOptions().get("heartbeat.intervalInSeconds"));
//    System.out.println("Saving...");
    newConfig.save();
    System.out.println("Saved");
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(Config.get())).build();
  }
  
//  private Tuple<Integer,String> getBaseLevel(){
//    String[] thresholds=Config.get().getOptions().get("thresholds").split(",");
//    String[] result=thresholds[0].split(":");
//    return new Tuple<Integer, String>(Integer.parseInt(result[0]), result[1]);
//  }
  
//  private Map<String, Integer>  getLevels1(){
//    Map<String, Integer> levels=new TreeMap<String, Integer>();
//    for(String levelValueAndText:Config.get().getOptions().get("thresholds").split(",")){
//      String[] level=levelValueAndText.split(":");
//      levels.put(level[1], Integer.valueOf(level[0]));
//    }
//    Map<String, Integer>  result=new TreeMap<String, Integer> ();
//    for(Entry<String, Integer> e:MapUtilities.sortByValue(levels))
//      result.put(e.getKey(), e.getValue());
//    return result;
//  }
//  
//  private Map<Integer, String>  getLevels2(){
//    Map<Integer, String> levels=new TreeMap<Integer, String>();
//    for(String levelValueAndText:Config.get().getOptions().get("thresholds").split(",")){
//      String[] level=levelValueAndText.split(":");
//      levels.put(Integer.valueOf(level[0]), level[1]);
//    }
//    Map<Integer, String>  result=new TreeMap<Integer, String> ();
//    for(Entry<Integer, String> e:MapUtilities.sortByKey(levels))
//      result.put(e.getKey(), e.getValue());
//    return result;
//  }
  
//  public Tuple<Integer,String> getLevel(String name){
//    for(Tuple<Integer, String> level:getLevels()){
//      if (name.equals(level.getRight())) return level;
//    }
//    return null;
//  }
//  public Tuple<Integer,String> getNextLevel(String name){
//    boolean next=false;
//    for(Tuple<Integer, String> level:getLevels()){
//      if (next) return level;
//      if (name.equals(level.getRight())){
//        next=true;
//      }
//    }
//    return the top level;
//  }
//  public List<Tuple<Integer,String>> getLevels(){
//    List<Tuple<Integer,String>> levels=new LinkedList<Tuple<Integer,String>>();
//    for(String levelValueAndText:Config.get().getOptions().get("thresholds").split(",")){
//      String[] level=levelValueAndText.split(":");
//      levels.add(new Tuple<Integer,String>(Integer.valueOf(level[0]), level[1]));
//    }
//    return levels;
//  }
//  private Tuple<Integer,String> toNextLevel(String currentLevel, Integer currentTotal){
//    List<Tuple<Integer,String>> levels=getLevels();
//    Tuple<Integer,String> currentLevel2=null;
//    Tuple<Integer,String> nextLevel=null;
//    for(Tuple<Integer,String> level:levels){
//      if (null!=currentLevel2){ //ie we found their current level
//        nextLevel=level;
//        break;
//      }
//      if (currentLevel.equals(level.getRight()))
//        currentLevel2=level;
//    }
//    return new Tuple<Integer,String>(nextLevel.getLeft()-currentTotal, nextLevel.getRight());
//  }
  
  
  static LevelsUtil levelsUtil=null;
  public LevelsUtil getLevelsUtil(){
    if (null==levelsUtil) levelsUtil=new LevelsUtil(Config.get().getOptions().get("thresholds"));
    return levelsUtil;
  }
  
  @POST
  @Path("/users/register")
  public Response register(
      @Context HttpServletRequest request 
      ,@Context HttpServletResponse response
      ,@Context ServletContext servletContext
      ,String raw
      ){
    try{
      log.debug("/register called");
//      String raw=IOUtils.toString(request.getInputStream());
      mjson.Json x=mjson.Json.read(raw);
      
      Database2 db=Database2.get();
      for (mjson.Json user:x.asJsonList()){
        String username=user.at("username").asString();
        
        if (db.getUsers().containsKey(username)){
          db.getUsers().remove(username); // remove so we can overwrite the user details
        }
        
        Map<String, String> userInfo=new HashMap<String, String>();
        for(Entry<String, Object> e:user.asMap().entrySet()){
          userInfo.put(e.getKey(), (String)e.getValue());
        }
        
        userInfo.put("level", getLevelsUtil().getBaseLevel().getRight());
        userInfo.put("levelChanged", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));// nextLevel.getRight());
        
//        if (!db.getUsers().containsKey(username)){
          db.getUsers().put(username, userInfo);
          log.debug("Registered user: "+Json.newObjectMapper(true).writeValueAsString(userInfo));
//        }else{
//          log.warn("Ignoring user ["+username+"] - it's already registered");
////          return Response.status(500).entity("{\"status\":\"ERROR\",\"message\":\"Username in use already\"}").build();        
//        }
      }
      
      db.save();
    }catch(IOException e){
      e.printStackTrace();
    }
    
    return Response.status(200).entity("{\"status\":\"DONE\"}").build();
  }
  
  @GET
  @Path("/points/{user}/{pool}/{increment}")
  public Response incrementPool(
      @Context HttpServletRequest request 
      ,@Context HttpServletResponse response
      ,@Context ServletContext servletContext
      ,@PathParam("user") String user
      ,@PathParam("pool") String pool
      ,@PathParam("increment") String increment
      ){
    try{
      Database2.get().increment(pool, user, Integer.valueOf(increment)).save();
    }catch(Exception e){
      return Response.status(500).entity("{\"status\":\"ERROR\",\"message\":\""+e.getMessage()+"\"}").build();  
    }
    return Response.status(200).entity("{\"status\":\"DONE\"}").build();
  }
  
  @GET
  @Path("/database/get")
  public Response getDatabase() throws JsonGenerationException, JsonMappingException, IOException{
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(Database2.get())).build();
  }
  
//  public static void main(String[] asd) throws JsonGenerationException, JsonMappingException, IOException{
//    System.out.println(new ManagementController().getLeaderboard2(3).getEntity());
////    System.out.println(new ManagementController().getList().getEntity());
//  }
  
  @GET
  @Path("/scorecard/{user}")
  public Response getScorecard(@PathParam("user") String user) throws JsonGenerationException, JsonMappingException, IOException{
    Database2 db=Database2.get();
    
    Map<String, Integer> scorecard=db.getScoreCards().get(user);
    Map<String, String> userInfo=db.getUsers().get(user);
    
    Map<String, Object> data=new HashMap<String, Object>();
    data.put("userId", user);
    data.put("displayName", userInfo.get("displayName"));
    data.putAll(scorecard);
//    String name=userInfo.containsKey("displayName")?userInfo.get("displayName"):user;
    
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(data)).build();
  }

  
  @POST
  @Path("/scorecard/{user}")
  public Response saveScorecard(
      @Context HttpServletRequest request 
      ,@Context HttpServletResponse response
      ,@Context ServletContext servletContext
      ,@PathParam("user") String user) throws JsonGenerationException, JsonMappingException, IOException{
    
    String payload=IOUtils.toString(request.getInputStream());
    System.out.println("Saving "+ payload);
    
//    mjson.Json x=mjson.Json.read(payload);
//    String userId=x.at("userId").asString();
//    String displayName=x.at("displayName").asString();
    
    Database2 db=Database2.get();
    
//    for (mjson.Json y:x.asJsonList()){
//      System.out.println(y.asString());
//    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map = objectMapper.readValue(payload, new TypeReference<HashMap<String,Object>>(){});
    String username=(String)map.get("userId");
    
    Map<String, String> userInfo=db.getUsers().get(username);
    Map<String, Integer> scorecard=db.getScoreCards().get(username);
    
    for(String k:map.keySet()){
      if (!k.equals("userId")){
        if (k.equals("displayName")){
          log.debug("Setting 'userInfo.displayName' to "+(String)map.get(k));
          userInfo.put("displayName", (String)map.get(k));
        }else{
          log.debug("Setting 'scorecard."+k+"' to "+(String)map.get(k));
          scorecard.put(k, Integer.parseInt((String)map.get(k)));
        }
      }
    }
    
    
//    if (displayName!=null) userInfo.put("displayName", displayName);
    
    db.save();
    
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString("OK")).build();
  }
  
  
  @GET
  @Path("/scorecards")
  public Response getScorecards() throws JsonGenerationException, JsonMappingException, IOException{
    Database2 db=Database2.get();
    List<Map<String, Object>> data=new ArrayList<Map<String,Object>>();
    
    Set<String> fields=new HashSet<String>();
    
    for(Entry<String, Map<String, Integer>> e:db.getScoreCards().entrySet()){
      Map<String, Object> row=new HashMap<String, Object>();
      Map<String,String> userInfo=db.getUsers().get(e.getKey());
      row.put("id", e.getKey());
      
      String name=userInfo.containsKey("displayName")?userInfo.get("displayName"):e.getKey();
      
      name="<div class='link' title='Edit' onclick='edit2(\""+e.getKey()+"\");' data-toggle='modal' data-target='#exampleModal'>"+name+"</div>";
      
      row.put("name", name);
      int total=0;
      for(Entry<String, Integer> s:e.getValue().entrySet()){
        row.put(s.getKey().replaceAll("\\.", " "), s.getValue());
        total+=s.getValue();
        fields.add(s.getKey().replaceAll("\\.", " "));
      }
      row.put("total", total);
      row.put("level", userInfo.get("level"));
      
      
      
//      Tuple<Integer,String> shouldBeLevel=getLevelsUtil().getLevelGivenPoints(total);
//      if (!shouldBeLevel.equals(userInfo.get("level")))
//        row.put("pointsToNextLevel", "PENDING");
      
      // points to next level
      Integer pointsToNextLevel=getLevelsUtil().getNextLevel(userInfo.get("level")).getLeft()-total;
      if (pointsToNextLevel<0) pointsToNextLevel=0;
      row.put("pointsToNextLevel", pointsToNextLevel);
      data.add(row);
    }
    
    // fill in the missing points fields with zero's
    for(Map<String, Object> e:data){
      for (String field:fields){
        if (!e.containsKey(field)){
          e.put(field, 0);
        }
      }
    }
    
    Map<String,Object> wrapper=new HashMap<String, Object>();
    List<Map<String,String>> columns=new ArrayList<Map<String, String>>();
//    columns.add(Config.get().new MapBuilder<String,String>().put("title","ID").put("data", "id").build());
    columns.add(new MapBuilder<String,String>().put("title","Name").put("data", "name").build());
    for(String field:fields)
      columns.add(new MapBuilder<String,String>().put("title",field).put("data", field).build());  
    columns.add(new MapBuilder<String,String>().put("title","Total").put("data", "total").build());
    
    columns.add(new MapBuilder<String,String>().put("title","Ninja Belt").put("data", "level").build());
    columns.add(new MapBuilder<String,String>().put("title","Points to next level").put("data", "pointsToNextLevel").build());
    
    wrapper.put("columns", columns);
    wrapper.put("data", data);
    
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(wrapper)).build();
  }
  
  @GET
  @Path("/leaderboard/{max}")
  public Response getLeaderboard2(@PathParam("max") Integer max) throws JsonGenerationException, JsonMappingException, IOException{
    Database2 db=Database2.get();
    Map<String, Map<String, Integer>> leaderboard=db.getLeaderboard();
    Map<String, Integer> totals=new HashMap<String, Integer>();
    for(Entry<String, Map<String, Integer>> e:leaderboard.entrySet()){
      Integer t=0;
      for(Entry<String, Integer> e2:e.getValue().entrySet()){
        t+=e2.getValue();
      }
      e.getValue().put("total", t);
      totals.put(e.getKey(), t);
    }
    
    //reorder
    List<Entry<String, Integer>> list=new LinkedList<Map.Entry<String, Integer>>(totals.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
      public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
          return (o2.getValue()).compareTo(o1.getValue());
      }
    });
    HashMap<String, Integer> sortedTotals=new LinkedHashMap<String, Integer>();
    for (Entry<String, Integer> e:list) {
      sortedTotals.put(e.getKey(), e.getValue());
    }
    
    Chart2Json c=new Chart2Json();
    c.setDatasets(new ArrayList<DataSet2>());
    int count=0;
    for(Entry<String, Integer> e:sortedTotals.entrySet()){
      Map<String, String> userInfo=db.getUsers().get(e.getKey());
      
      c.getLabels().add(null!=userInfo && userInfo.containsKey("displayName")?userInfo.get("displayName"):e.getKey());
      
      if (c.getDatasets().size()<=0) c.getDatasets().add(new DataSet2());
      c.getDatasets().get(0).getData().add(e.getValue());
      c.getDatasets().get(0).setBorderWidth(1);
      
      // TODO: set this to the color of the belt
      if ("blue".equalsIgnoreCase(userInfo.get("level"))){
        c.getDatasets().get(0).getBackgroundColor().add("rgba(0,0,163,0.8)");
      }else if ("grey".equalsIgnoreCase(userInfo.get("level"))){
        c.getDatasets().get(0).getBackgroundColor().add("rgba(130,130,130,0.8)");
      }else if ("red".equalsIgnoreCase(userInfo.get("level"))){
        c.getDatasets().get(0).getBackgroundColor().add("rgba(163,0,0,0.8)");
      }else if ("black".equalsIgnoreCase(userInfo.get("level"))){
        c.getDatasets().get(0).getBackgroundColor().add("rgba(0,0,0,0.8)");
      }
      
      
      
      c.getDatasets().get(0).getBorderColor().add("rgba(130,0,0,0.8)");
      
      count=count+1;
      if (count>=max) break;
    }
    return Response.status(200).entity(Json.newObjectMapper(true).writeValueAsString(c)).build();
  }
  
  @GET
  @Path("/script/{name}")
  public Response getScript(@PathParam("name") String scriptName) throws JsonGenerationException, JsonMappingException, IOException{
    String path="scripts/"+scriptName;
    InputStream is=this.getClass().getClassLoader().getResourceAsStream(path);
    
    StringBuilder sb = new StringBuilder();
    String inputLine;
    BufferedReader br=new BufferedReader(new InputStreamReader(is));
    while ((inputLine = br.readLine()) != null){
      sb.append(inputLine);
      sb.append('\n');
    }
    
    return Response.status(200).header("Content-Type", "text/application").entity(sb.toString()).build();
  }
  
}