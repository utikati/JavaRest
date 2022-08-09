/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Martins
 */
@XmlRootElement(name = "item")
public class Item {
 
 
 private int id;
 private String descricao;
 private Calendar dataHoraFecho;
 private String dataString;


 private float precoMinimo;
 private float precoAtual=0;
 private int numeroOfertas = 0;
 private boolean terminado = false;
 private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH); //a pattern deste simpledate
 private SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
 
 public Item(){ }
 
 public Item(String descricao, Calendar dataHoraFecho, float precoMinimo){
     this.descricao = String.copyValueOf(descricao.toCharArray());
     this.dataHoraFecho = dataHoraFecho;
     this.precoMinimo = precoMinimo;
     this.dataString = sdf.format(dataHoraFecho.getTime());
 }
 
@XmlAttribute    
public int getId() {
        return id;
}
 
 /*
 Nesta função verifyDisponibility não inseri logo o preço porque a uso para verificar as datas e caso esse seja o trigger da condição aproveito
 para alterar logo o estado de terminado para verdadeiro usando assim esta função como uma especie de ponto de verificação
 */
 public boolean verifyDisponibility(){ //verificar se o objecto está disponivel
     
     if(dataHoraFecho.compareTo(Calendar.getInstance())<=0 || terminado){
         this.terminado = true; //pode o terminado não ter sido o trigger para entrar nesta condição 
         return false; //ou seja não está disponivel
     }return true; //significa que está disponivel 
 }
 
 public boolean verifyPrice (float newPrice){
     if(this.precoAtual==0){
         if(newPrice >= precoMinimo){
             return true;
         }return false;   
     }
     if(this.precoAtual < newPrice)
         return true;
     return false;
     
 }
    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
        this.dataHoraFecho = Calendar.getInstance(); //inicar a variavel 
     try {
         this.dataHoraFecho.setTime(sdf.parse(this.dataString));
     } catch (ParseException ex) {
         Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
     }
    }
    
    public void setId(int id){
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String des){
        this.descricao = des;
    }
    

    public Calendar getDataHoraFecho() {
        return dataHoraFecho;
    }

    public void setDataHoraFecho(Calendar dataHoraFecho) {
        this.dataHoraFecho = dataHoraFecho;
    }

    public float getPrecoMinimo() {
        return precoMinimo;
    }
    
    public void setPrecoMinimo(float precoMinimo){
        this.precoMinimo = precoMinimo;
    }

    public float getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(float precoAtual) {
        this.precoAtual = precoAtual;
        
    }
    
    public void setNumeroOfertas(int numeroOfertas){
        this.numeroOfertas = numeroOfertas; 
    }

    public int getNumeroOfertas() {
        return numeroOfertas;
    }


    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }
    
    public String filterShowList(){
        if(terminado){
            return "ID: "+this.id + " Descrição: "+this.descricao+" Estado: terminado";
        }else{
            return "ID: "+this.id + " Descrição: "+this.descricao+" Estado: disponível";
        }
    }
    
    public String toString(){ //para que o objeto demonstre os dados realizei umas condições no toString
        
        if(terminado){
            if(precoAtual==0){
                return "ID: "+this.id + " Descrição: "+this.descricao+" Numero de Ofertas: "+numeroOfertas+" preço inicial: "+precoMinimo+" preco Atual: Indisponível Estado: terminado | Data limite: "+dataString;
            }else{
                return "ID: "+this.id + " Descrição: "+this.descricao+" Numero de Ofertas: "+numeroOfertas+"  preço inicial: "+precoMinimo+" preco Atual: "+precoAtual+ "Estado: terminado | Data limite: "+dataString;
            }
        }
        else{
           if(precoAtual==0){
               return "ID: "+this.id + " Descrição: "+this.descricao+" Numero de Ofertas: "+numeroOfertas+" preço inicial: "+precoMinimo+" preco Atual: Indisponível Estado: disponivel | Data limite: "+dataString;
           }else{
               return "ID: "+this.id + " Descrição: "+this.descricao+" Numero de Ofertas: "+numeroOfertas+" preço inicial: "+precoMinimo+" preco Atual: "+precoAtual+ " Estado: disponivel | Data limite: "+dataString;
           }
        }
    }
   
    
}
