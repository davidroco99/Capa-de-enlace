package servidor;

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
    private String mensajeEntramadoSalida;
    private Socket conexion;
    private String tasaTransferencia;

    public String getMensajeEntramadoSalida() {
        return mensajeEntramadoSalida;
    }

    public void setMensajeEntramadoSalida(String mensajeEntramadoSalida) {
        this.mensajeEntramadoSalida = mensajeEntramadoSalida;
    }

    
    
    public ThreadEnvio(Socket conexion, final Principal main) {
        this.conexion = conexion;
        this.main = main;
        //tasaTransferencia = JOptionPane.showInputDialog(this.main, "La tasa del Cliente es 2000 MB/m, ingrese valor: ", "Tasa de transferencia", JOptionPane.INFORMATION_MESSAGE);

        //Evento que ocurre al escribir en el areaTexto
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
    private void enviarDatos(String mensaje) throws InterruptedException {
        try {
            
            this.setMensajeEntramadoSalida(mensaje+"//"+main.getNombreServidor()+"//"+main.getNombreCliente());
            main.setTamanioMensaje(mensajeEntramadoSalida.length());
            salida.writeObject("Host S => " + String.valueOf(main.getTamanioMensaje()) + mensajeEntramadoSalida);
            salida.flush(); //flush salida a cliente
            main.mostrarMensaje("Host S => " + mensaje);
            main.mostrarMensaje("Host S => La trama total" +  String.valueOf(main.getTamanioMensaje()) + mensajeEntramadoSalida);

            if (mensaje.equals("tincho")) {
                /*
                Thread.sleep(Integer.parseInt(tasaTransferencia));
                JOptionPane.showMessageDialog(this.main, "Se recibio mensaje duplicado", "NACK del Cliente!", JOptionPane.INFORMATION_MESSAGE);
                */
            } else {
                if (mensaje.equals("albana")) {
                    /*
                    Thread.sleep(Integer.parseInt(tasaTransferencia));
                    JOptionPane.showMessageDialog(this.main, "Se recibio desordenado", "NACK del Cliente!", JOptionPane.INFORMATION_MESSAGE);
                    */
                } else {
                    
                    if(mensaje.equals("red")) {
                    /*
                    Thread.sleep(Integer.parseInt(tasaTransferencia));
                    JOptionPane.showMessageDialog(this.main, "Se perdio la trama", "NACK del Cliente!", JOptionPane.INFORMATION_MESSAGE);
                      */  
                    }else{
                      main.setAck(true);
                      
                      
                  //  Thread.sleep(Integer.parseInt(tasaTransferencia));
                  //   JOptionPane.showMessageDialog(this.main, "Se recibio mensaje con exito", "ACK del Cliente!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            }
           
        } //Fin try
        catch (IOException ioException) {
            main.mostrarMensaje("Error escribiendo Mensaje");
        } //Fin catch  

    } //Fin methodo enviarDatos

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
