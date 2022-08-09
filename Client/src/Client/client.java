/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Jorge Martins
 */
public class client {
    static Client client;
    static String baseUri = "http://localhost:8080/Ficha10_Servidor/api/leilao";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        client = ClientBuilder.newClient();
        try{
            System.out.println("Connectado..");
            boolean flag = true;
            while(flag){
                System.out.println("Opções\n1 - Listar todos Leiloes\n2 - Pesquisar por Item\n3 - Adicionar Item\n4 - Remover Item\n5 - Fazer Oferta a Item\n6 - Sair\n");
                int op = inputInt("Insira a sua opção", new Scanner(System.in));
                switch(op){
                    case 1: getTodosItens(); break;
                    case 2: int id = inputInt("Insira o id do produto", new Scanner(System.in));
                    try{
                            Item i = getItem(Integer.toString(id));
                            if(i!=null){
                                System.out.println(i);
                            }else{
                                System.out.println("O item com id que inseriu não existe");
                            }
                    }catch(javax.ws.rs.NotFoundException e){
                             System.out.println("Id não encontrado");
                    }
                            break;
                    case 3: String des = inputString("Insira a descrição", new Scanner(System.in));
                            Calendar calendar = inputData(new Scanner(System.in));
                            boolean flg = true;
                            float priceMin = 1; //apenas para não dar erro em baixo
                            do{
                            priceMin = inputFloat("Insira o preço a iniciar licitação", new Scanner(System.in));
                                if(priceMin <= 0){
                                    System.out.println("Preço minimo não pode ser menor ou igual a 0");
                                }else{
                                    flg = false;
                                }
                            }while(flg);
                            criaItem(new Item(des, calendar, priceMin));
                            break;
                    case 4: int remoId = inputInt("Insira o id do Item a remover", new Scanner(System.in));
                            removeItem(Integer.toString(remoId));
                            break;  
                    case 5: int idOffer = inputInt("Insira o id do Item a realizar oferta", new Scanner(System.in));
                            try{
                                Item itemOffer = getItem(Integer.toString(idOffer));
                                if(itemOffer!=null){
                                    float oferta = inputFloat("Insira o valor da sua oferta", new Scanner(System.in));
                                    if(itemOffer.verifyDisponibility()){
                                        if(itemOffer.verifyPrice(oferta)){
                                            itemOffer.setPrecoAtual(oferta);
                                            ofertaItem(itemOffer);
                                        }else{
                                            System.out.println("Oferta menor que o valor do Item neste momento");
                                        }

                                    }else{
                                        System.out.println("Produto não disponivel");
                                    }

                                }else{
                                    System.out.println("O item com o id que inseriu não existe");
                                }
                            }catch(javax.ws.rs.NotFoundException e){
                               System.out.println("O item com o id que inseriu não existe");
                            }
                            break;
                    case 6: flag = false; System.out.println("Adeus user");  break;
                    default:
                        System.out.println("#Error -> Não é opção.."); break;
                }
            }
            }catch(Exception e){
                e.printStackTrace();
                System.out.print("Sem conexão");
            }
        client.close();
    }
    
    private static Calendar inputData(Scanner aTeclado) {
        boolean Date=true, Hour = true;
        String date= "";
        String hour="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH); //a pattern deste simpledate
	SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                while(Date){
                    date = inputString("Insira a data com formato >>(yyyy-MM-dd)<< ex -> 2022-06-21", aTeclado); 
                    if(validateDate(date)){
                        Date=false;
                        System.out.println("Data inserida com sucesso");
                    }else{
                        System.out.println("Formato não reconhecido volte a inserir por favor");
                    }
                }
                while(Hour){
                    hour = inputString("Insira a hora com formato >>(HH:mm)<< 00H a 23H e minutos de 0 a 29", aTeclado); 
                    if(validateHour(hour)){
                        Hour=false;
                        System.out.println("Hora inserida com sucesso");
                    }else{
                        System.out.println("Formato não reconhecido volte a inserir por favor");
                    }
		}
                Calendar cal = Calendar.getInstance(); //inicar a variavel
        try {
            cal.setTime(sdf.parse(date+" "+hour));
        } catch (ParseException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return cal;
}
    
    private static String inputString(String aTexto, Scanner aTeclado) { //para inserir de forma segura
		System.out.println(aTexto);
		return aTeclado.nextLine();
	}
    
    private static int inputInt(String aTexto, Scanner aTeclado) { 
		while(true){
			try{
				return (Integer.parseInt(inputString(aTexto, aTeclado))); //assim já asseguro ser inteiro..
			}catch(NumberFormatException nfe){
				System.out.println("Não é um inteiro ou maior que 9 unidades, insira novamente");
			}
		}
	}
    private static float inputFloat(String aTexto, Scanner aTeclado) { //funçao de inserção para float
		while(true){
			try{
				return (Float.parseFloat(inputString(aTexto, aTeclado))); //assim já asseguro ser float..
			}catch(NumberFormatException nfe){
				System.out.println("Não é numérico");
			}
		}
    }
    
    public static boolean validateHour(String hora){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setLenient(false); //caso não seja como o formato esperado isto vai forçar o parseExceptiom
		try{
			sdf.parse(hora);
		}catch(ParseException e){
			return false;
		}
		return true;
    }
    
    public static boolean validateDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false); //caso não seja como o formato esperado isto vai forçar o parseExceptiom
		try{
			sdf.parse(date);
		}catch(ParseException e){
			return false;
		}
		return true;
    }
    
    //METODOS REST --------------------------------------------------------------------------------------------------------
    
    public static Item getItem(String id){
        Item i = client.target(baseUri)
                                .path(id)
                                .request()
                                .accept(MediaType.APPLICATION_JSON)
                                .get(Item.class);
        
    return i;
    }
    
    public static void getTodosItens(){
        Vector<Item> itens = new Vector<>();
        itens.clear();
        itens = client.target(baseUri)
                                .request()
                                .accept(MediaType.APPLICATION_JSON)
                                .get(new GenericType<Vector<Item>>(){}); 
        if(itens.size()>0){
            
            for(Item c: itens){
                System.out.println(c.filterShowList());
            }
        }else{
            System.out.println("Não existem dados para listar");
        }
    }
    
    public static void criaItem(Item i){
        Response resp = client.target(baseUri)
                              .request()
                              .post(Entity.json(i));
        
        if(resp.getStatus()== 201){
            System.out.println("Item criado com sucesso");
        }else{
            System.out.println("Ocorreu um erro na criação do Item");
        }
        
        resp.close();
    }
    
    public static void ofertaItem(Item i){
        
        Response resp = client.target(baseUri)
                              .path(Integer.toString(i.getId()))
                              .request()
                              .put(Entity.json(i));
        
        
        
        if(resp.getStatus()==200){
            System.out.println("Oferta realizada com sucesso");
        }else{
            System.out.println("Ocorreu um erro a realizar a sua oferta, tente novamente mais tarde");
        }
        
        resp.close();
    }
    
    public static void removeItem(String id){
        Response resp = client.target(baseUri)
                              .path("/"+id)
                              .request()
                              .delete();
        
        if(resp.getStatus()==200){
            System.out.println("Item apagado com sucesso ");
        }else{
            System.out.println("Ocorreu um erro ao apagar o Item com o id que forneceu");
        }
        
        resp.close();
    }
    
    
}
    

