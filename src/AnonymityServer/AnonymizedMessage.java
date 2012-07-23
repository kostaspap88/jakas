package AnonymityServer;

import javax.servlet.http.HttpServletResponse;

/** This class defines an Anonymized message, i.e. a message that has lat and lon which are anonymous
 * and also fullfils time requirements. Anonymized messages always go to the outgoing Queue.
 * @author airwizard
 *
 */
public class AnonymizedMessage {
	
	int id;
	double lati;
	double longi;
	HttpServletResponse response;
	
	public AnonymizedMessage(int identity,double lat, double lon,HttpServletResponse resp) {
		id=identity;
		lati=lat;
		longi=lon;
		response = resp;
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
}
