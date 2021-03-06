package it.unibo.virtualrobot;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

public class BoundaryWalk {
    private  final String localHostName    = "localhost";
    private  final int port                = 8090;
    private  final String URL              = "http://"+localHostName+":"+port+"/api/move";

    public BoundaryWalk() { }

    protected boolean sendCmd(String move, int time)  {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            System.out.println( move + " sendCmd "  );
            //String json         = "{\"robotmove\":\"" + move + "\"}";
            String json         = "{\"robotmove\":\"" + move + "\" , \"time\": " + time + "}";
            StringEntity entity = new StringEntity(json);
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(URL))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "application/json")
                    .setEntity(entity)
                    .build();
            CloseableHttpResponse response = httpclient.execute(httppost);
            //System.out.println( "MoveVirtualRobot | sendCmd response= " + response );
            boolean collision = checkCollision(response);
            return collision;
        } catch(Exception e){
            System.out.println("ERROR:" + e.getMessage());
            return true;
        }
    }

    protected boolean checkCollision(CloseableHttpResponse response) throws Exception {
        try{
            //response.getEntity().getContent() is an InputStream
            String jsonStr = EntityUtils.toString( response.getEntity() );
            System.out.println( "MoveVirtualRobot | checkCollision_simple jsonStr= " +  jsonStr );
            //jsonStr = {"endmove":true,"move":"moveForward"}
            JSONObject jsonObj = new JSONObject(jsonStr) ;
            boolean collision = false;
            if( jsonObj.get("endmove") != null ) {
                collision = ! jsonObj.get("endmove").toString().equals("true");
                System.out.println("MoveVirtualRobot | checkCollision_simple collision=" + collision);
            }
            return collision;
        }catch(Exception e){
            System.out.println("MoveVirtualRobot | checkCollision_simple ERROR:" + e.getMessage());
            throw(e);
        }
    }

    public boolean moveForward(int duration)  { return sendCmd("moveForward", duration);  }
    public boolean moveBackward(int duration) { return sendCmd("moveBackward", duration); }
    public boolean moveLeft(int duration)     { return sendCmd("turnLeft", duration);     }
    public boolean moveRight(int duration)    { return sendCmd("turnRight", duration);    }
    public boolean moveStop(int duration)     { return sendCmd("alarm", duration);        }

    // Assuming that the robot is in the HOME position
    public String doBoundaryWalk(){
        StringBuilder moves = new StringBuilder();
        int stepDuration = 600;
        int turnDuration = 100;

        for(int i=0; i<4; i++){
            boolean collision = false;
            // The robot move forward until it detects a collision
            do{
                System.out.println("Robot: No obstacles -> moving forward");
                collision = moveForward(stepDuration);
                moves.append("w");
            }while(!collision);

            System.out.println("Robot: Detected a collision -> turning left");
            moveLeft(turnDuration);
            moves.append("l");
        }

        return moves.toString();
    }

    /*
    MAIN
     */
    public static void main(String[] args) {
        BoundaryWalk app = new BoundaryWalk();

        System.out.println("Main: starting the robot");
        app.doBoundaryWalk();
        System.out.println("Main: end");
    }

}
