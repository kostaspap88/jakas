package AnonymityServer;

import javax.servlet.http.HttpServletResponse;

/** This class defines an Anonymity message, i.e. a message that has lat and lon which are NOT anonymous
 * and does NOT fullfill time requirements. Anonymity messages always come from  the incoming Queue.
 * @author airwizard
 *
 */
public class AnonymityMessage {

int id;
double lati;
double longi;
int time;
String anonymitypolicy;
HttpServletResponse response;

	public AnonymityMessage(int identity,String latitude, String longitude,
			String timeStamp, String anonymityPolicy, HttpServletResponse resp) {
		id=identity;
	    lati = Double.parseDouble(latitude);
		longi=Double.parseDouble(longitude);;
		time=Integer.parseInt(timeStamp);
		anonymitypolicy=anonymityPolicy;
		response=resp;
	}
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	public int getID(){
		return id;
	}
	
	public double getLatitude(){
		return lati;
	}
	public double getLongitude(){
		return longi;
	}
	public int getTimeStamp(){
		return time;
	}
	public String getAnonymityPolicy(){
		return anonymitypolicy;
	}

}
