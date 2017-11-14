package cliente;

import java.awt.Event;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;

public class ThreadRecepcion implements Runnable {

    private final Principal main;
    private String mensaje;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private final Socket cliente;
    private final String separador;
    private String[] trama;
    private String ack;
    private ActionMap l;
    //Inicializar chatServer y configurar GUI
    public ThreadRecepcion(Socket cliente, Principal main) {
        this.cliente = cliente;
        this.main = main;
        this.separador = Pattern.quote("/");

    }

    
    public void mostrarMensaje(String mensaje) {
        main.areaTexto.append(mensaje);
    }

    public void run() {
        try {
            entrada = new ObjectInputStream(cliente.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ThreadRecepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
        do { //procesa los mensajes enviados desde el servidor
            try {//leer el mensaje y mostrarlo 

                
                mensaje = (String) String.valueOf(entrada.readObject()); //leer nuevo mensaje

                
                if (mensaje.equals("ACK")) {
                    main.mostrarMensaje("-------------------------------------Recepcion-------------------------------------");
                    main.mostrarMensaje(mensaje);
                } else {

                    String[] trama = mensaje.split(separador);
                    
                     if (trama[2].equals("tincho")){
                 
                    mensaje = "Host S =>tinchotincho";
                    main.mostrarMensaje("-------------------------------------Recepcion-------------------------------------");
                    main.mostrarMensaje(mensaje);
                    trama[2] = "tinchotincho";
                    Thread.sleep(1000);
                    JOptionPane.showMessageDialog(this.main, "Mensaje Duplicado", "NACK", JOptionPane.INFORMATION_MESSAGE);
                   
                }else{
                    
                         
                    
                    main.mostrarMensaje("-------------------------------------Recepcion-------------------------------------");
                    main.mostrarMensaje(mensaje);
                    main.mostrarMensaje("Tamaño del mensaje: " + trama[1]);
                    main.mostrarMensaje("Mensaje: " + trama[2]);
                    main.mostrarMensaje("IP Origen: " + trama[3]);
                    main.mostrarMensaje("IP Destinatario: " + trama[4]);

                    if ((trama[4].equals(main.getNombreCliente())) && trama[3].equals(main.getNombreServidor())) {
                       
                        ack = "ACK";
                        main.campoTexto.setText(ack);
                        JOptionPane.showMessageDialog(this.main, "ENVIO ACK", "Se va enviar ACK", JOptionPane.INFORMATION_MESSAGE);
                     
                        ThreadEnvio.getInstance(cliente, main).enviarDatos(ack);
                        
                       
                    } else {
                        Thread.sleep(1000);
                        JOptionPane.showMessageDialog(this.main, "IP conexion Fail", "Se va enviar NACK", JOptionPane.INFORMATION_MESSAGE);
                    }
                        
                        /*
                        try {
                            
                            salida = new ObjectOutputStream(cliente.getOutputStream());
                            mensaje = "ACK";
                            salida.writeObject(mensaje);
                            salida.flush(); //flush salida a cliente
                            
                            
                        } //Fin try
                        catch (IOException ioException) {
                            main.mostrarMensaje("Error escribiendo Mensaje");
                        } //Fin catch  */
                    }

                }

                
               
                /*
                if (mensaje.equals("Host S => albana")){
                 mensaje = "Host S =>alanab";
                
                }
                if (mensaje.equals("Host S => red")){
                 mensaje = "Host S =>";   
                }    
                main.mostrarMensaje(mensaje);
                
                if (mensaje.equals("Host S =>tinchotincho")){
                    Thread.sleep(1500);
                    JOptionPane.showMessageDialog(this.main, "Mensaje duplicado", "Alerta", JOptionPane.INFORMATION_MESSAGE);
                }
                if (mensaje.equals("Host S =>alanab")){
                    Thread.sleep(1500);
                    JOptionPane.showMessageDialog(this.main, "Mensaje desordenado", "Alerta", JOptionPane.INFORMATION_MESSAGE);
                }*/
            } //fin try
            
            catch (SocketException ex) {
            } catch (EOFException eofException) {
                main.mostrarMensaje("Fin de la conexion");
                break;
            } //fin catch
            catch (IOException ex) {
                Logger.getLogger(ThreadRecepcion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException classNotFoundException) {
                main.mostrarMensaje("Objeto desconocido");
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadRecepcion.class.getName()).log(Level.SEVERE, null, ex);
            }    //fin catch

        } while (!mensaje.equals("Cliente>>> TERMINATE")); //Ejecuta hasta que el server escriba TERMINATE

        try {
            entrada.close(); //cierra entrada Stream
            cliente.close(); //cierra Socket
        } //Fin try
        catch (IOException ioException) {
            ioException.printStackTrace();
        } //fin catch

        main.mostrarMensaje("Fin de la conexion");
        System.exit(0);
    }


}
