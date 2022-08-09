/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jorge Martins
 */

@ApplicationPath("api")
public class AppConfig extends Application{
    
    private Set<Object> singletons = new HashSet<Object>();

   public AppConfig() {
      singletons.add(new Server());
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }   
    
    
    
}
