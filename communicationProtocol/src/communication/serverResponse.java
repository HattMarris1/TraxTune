package communication;

import java.io.Serializable;

/**
 * Created by Matthew on 24/04/2017.
 */
public class serverResponse implements Serializable  {
    public boolean success;
    public String description;

    public serverResponse(boolean success, String description) {
        this.success = success;
        this.description = description;
    }


}
