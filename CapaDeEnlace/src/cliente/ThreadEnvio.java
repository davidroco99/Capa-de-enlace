package cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
        
public class ThreadEnvio implements Runnable {
    private final Principal main; 
    private ObjectOutputStream salida;
    private String mensaje;
    private Socket conexion; 
   
    public ThreadEnvio(Socket conexion, final Principal main){
        this.conexion = conexion;
        this.main = main;
        
        //Evento que ocurre al escribir en el campo de texto
        main.campoTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mensaje = event.getActionCommand();
                
                try {
                    enviarDatos(mensaje); //se envia el mensaje
                } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadEnvio.class.getName()).log(Level.SEVERE, null, ex);
                }
                main.campoTexto.setText(""); //borra el texto del enterfield
            } //Fin metodo actionPerformed
        } 
        );//Fin llamada a addActionListener
    } 
    
   //enviar objeto a cliente 
   private void enviarDatos(String mensaje) throws InterruptedException{
      try {
         salida.writeObject("Cliente => " + mensaje);
         salida.flush(); //flush salida a cliente
         Thread.sleep(1500);
         JOptionPane.showMessageDialog(this.main, "Se recibio mensaje con exito", "ACK del Servidor!", JOptionPane.INFORMATION_MESSAGE);
         main.mostrarMensaje("Cliente => " + mensaje);
      } //Fin try
      catch (IOException ioException){ 
         main.mostrarMensaje("Error escribiendo Mensaje");
      } //Fin catch  
      
   } //Fin metodo enviarDatos

   //manipula areaPantalla en el hilo despachador de eventos
    public void mostrarMensaje(String mensaje) {
        main.areaTexto.append(mensaje);
    } 
   
    public void run() {
         try {
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.flush(); 
        } catch (SocketException ex) {
        } catch (IOException ioException) {
          ioException.printStackTrace();
        } catch (NullPointerException ex) {
        }
    }   
   
} 
