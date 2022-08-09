/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.Response;

import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jorge Martins
 */



@Path("leilao")
public class Server {
    
    
    Vector <Item> listItens = new Vector<>();
    private int id = 1; //inicia com o valor 1
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    
    
    @GET
    @Produces({"application/xml", "application/json"})
    public Response obtemTodosItens(){
        synchronized(listItens){
            if(listItens != null){
                verifyAllItens(); //para se sejam verificados todos os itens em relação ao seu estados
                Vector<Item> aux = new Vector <>();
                aux.clear();
                aux.addAll(listItens);
                
                Response.ResponseBuilder builder = Response.ok(aux).status(Response.Status.OK);
                Response response = builder.build();
                return response;
            }
        }
        Response.ResponseBuilder builder = Response.ok(null).status(Response.Status.NO_CONTENT);
        Response response = builder.build();
        
        return response;
        
    }
    
    
     private void verifyAllItens(){ //metodo criado para verificação de toda a lista de itens, nem sempre usado por usar mais recurso
        if(listItens != null){
            Vector <Item> list = new Vector<>();
          synchronized(listItens){
            Iterator ite = listItens.iterator();
            while(ite.hasNext()){
                Item i = (Item)ite.next();
                i.verifyDisponibility();
                list.add(i);
            }
            listItens.clear();
            listItens.addAll(list);
            
        }
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces({"application/xml", "application/json"})
    public Response obtemItem(@PathParam("id") String id){ //não usei o get do array porque existe a opção de remover e altera as posições
        synchronized(listItens){
            int id1 = Integer.parseInt(id);
            if(listItens != null || listItens.size()==0){
                Iterator ite = listItens.iterator();
                while(ite.hasNext()){
                    Item i = (Item)ite.next();
                    i.verifyDisponibility(); //aproveitar para verificar a disponibilidade o item
                    if(i.getId()==id1){
                        Response.ResponseBuilder builder = Response.ok(i).status(Response.Status.OK);
                        Response response = builder.build();
                        return response;
                    }
                } //se o ciclo acabar sem encontrar o id ele retorna nulo na mesma 
            }
        
        }
        Response.ResponseBuilder builder = Response.ok(null).status(Response.Status.NOT_FOUND);
        Response response = builder.build();
        
        return response;   
    }
    
    
   
    @DELETE
    @Path("/{id}")
    public Response removeItem(@PathParam("id") String id){
       synchronized(listItens){
        int id1 = Integer.parseInt(id);
        Item i = obtemItemPriv(id1);
        if(i!=null){
            if(listItens.remove(i)){
                Response.ResponseBuilder builder = Response.ok().status(Response.Status.OK);
                Response response = builder.build();
                return response;
            }
        }
        Response.ResponseBuilder builder = Response.ok().status(Response.Status.NOT_FOUND);
         Response response = builder.build();
         
         return response;
      }
    }
    
    private Item obtemItemPriv(int id){ //não usei o get do array porque existe a opção de remover e altera as posições
        synchronized(listItens){
       
            if(listItens != null || listItens.size()==0){
                Iterator ite = listItens.iterator();
                while(ite.hasNext()){
                    Item i = (Item)ite.next();
                    i.verifyDisponibility(); //aproveitar para verificar a disponibilidade o item
                    if(i.getId()==id){
                        return i;
                    }
                } //se o ciclo acabar sem encontrar o id ele retorna nulo na mesma 
            }
        }
        
        return new Item();   
    }
    
    @PUT
    @Path("/{id}")
    @Consumes({"application/xml", "application/json"})
    public Response oferece(@PathParam("id") String id, Item valor){ //para oferecer primeiro verifica a disponibilidade e em seguida se o preço está disponivel
        synchronized(listItens){
        int id1 = Integer.parseInt(id);
        
        Item i = obtemItemPriv(id1);
        if(i!=null && i.verifyDisponibility()){ //verifica a disponibilidade do item
            if(i.verifyPrice(valor.getPrecoAtual())){
                i.setPrecoAtual(valor.getPrecoAtual());
                i.setNumeroOfertas((i.getNumeroOfertas()+1));
                Response.ResponseBuilder builder = Response.ok().status(Response.Status.OK);
                Response response = builder.build();
                return response;   
            }
        }
        }
        
        Response.ResponseBuilder builder = Response.ok().status(Response.Status.CONFLICT);
        Response response = builder.build();
        
        return response;
        
    }
    
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response adicionaItem(Item novo){
        synchronized(listItens){
        Item newItem = novo;
        newItem.setId(id); // inserir o seu id para depois aumentar e ser sequencial
        if(listItens.add(newItem)){
            id++; //o servidor após inserir com sucesso o item aumenta para que o proximo tenha um id sequencial
            
            Response.ResponseBuilder builder = Response.ok().status(Response.Status.CREATED);
            Response response = builder.build();
            return response;
        }
        }
          Response.ResponseBuilder builder = Response.ok().status(Response.Status.BAD_REQUEST);
          Response response = builder.build();
          
          return response;
          
            
        }
    }
    
