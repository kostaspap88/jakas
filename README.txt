JA.K.A.S.

A JA.va K A.nonymity S.erver [or S.ervlet :-) ]


This projects creates a server that receives several http requests that contain geo-location.
If they are close enough, then they are grouped together and their location is obfuscated.

See this:


                *  John
        
      * Mary


                          * Jim


Now see this:

    ------------------------------
   |           *  John            |
   |                              |
   |  * Mary                      |
   |              * CENTRAL       |  
   |                POSITION      |  
   |                      * Jim   |
   |                              |
    -----------------------------
Everybody will be grouped and his position will become the CENTRAL POSITION, offering him k-anonymity (and since
there are 3 people, we achive 3-1=2 anonymity for everyone)

The implementation idea is based on the Georgia Tech paper by B.Gedik and L.Liu. 
We developed a Java servlet that works as a simple K-Anonymity server based on the 
message perturbation engine presented.

The project has educational purposes only, it does not claim compliance to the original paper,
it is currently in development and comes with no warranties.



REFERENCES:
Bugra Gedik, Ling Liu. Protecting Location Privacy with Personalized k-Anonymity:
Architecture and Algorithms. IEEE Transactions on Mobile Computing, Vol. 7, January 2008.