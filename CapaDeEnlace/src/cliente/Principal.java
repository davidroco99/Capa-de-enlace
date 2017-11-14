package cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**Clase que se encarga de correr los threads de enviar y recibir texto
 * y de crear la interfaz grafica.
 * 
 * @author Fede
 */
public class Principal extends JFrame{
    public JTextField campoTexto; //Para mostrar mensajes de los usuarios
    public JTextArea areaTexto; //Para ingresar mensaje a enviar
    private static ServerSocket servidor; //
    private static Socket cliente; //Socket para conectarse con el cliente
    private static final String ip = "127.0.0.1";
    //private static final String ip = JOptionPane.showInputDialog(Principal.main, "IP Host 1 ", "IP Host 1", JOptionPane.INFORMATION_MESSAGE); //ip a la cual se conecta
    public static Principal main; 
    private String tasaTransferencia;
    private boolean ack;
    private String nombreCliente;
    private Integer tamanioMensaje;
    private String nombreServidor;
    public Thread envio;
    public Thread recepcion;
    
    
    public String getTasaTransferencia() {
        return tasaTransferencia;
    }

    public void setTasaTransferencia(String tasaTransferencia) {
        this.tasaTransferencia = tasaTransferencia;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Integer getTamanioMensaje() {
        return tamanioMensaje;
    }

    public void setTamanioMensaje(Integer tamanioMensaje) {
        this.tamanioMensaje = tamanioMensaje;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }
    
    
    public Principal(){
        super("REDES 2017  Host:      Cliente"); //Establece titulo al Frame
 
        campoTexto = new JTextField(); //crea el campo para texto
        campoTexto.setEditable(true); //No permite que sea editable el campo de texto
        add(campoTexto, BorderLayout.NORTH); //Coloca el campo de texto en la parte superior
        
        
        areaTexto = new JTextArea(); //Crear displayArea
        areaTexto.setEditable(false);
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        areaTexto.setBackground(Color.darkGray); //Pone de color gris al displayArea
        areaTexto.setForeground(Color.WHITE); //pinta azul la letra en el displayArea
        campoTexto.setForeground(Color.red); //pinta toja la letra del mensaje a enviar
       
        
        //Crea menu Archivo y submenu Salir, ademas agrega el submenu al menu
        JMenu menuArchivo = new JMenu("Archivo"); 
        JMenuItem salir = new JMenuItem("Salir");
        menuArchivo.add(salir); //Agrega el submenu Salir al menu menuArchivo
        
        JMenuBar barra = new JMenuBar(); //Crea la barra de menus
        setJMenuBar(barra); //Agrega barra de menus a la aplicacion
        barra.add(menuArchivo); //agrega menuArchivo a la barra de menus
        
        //Accion que se realiza cuando se presiona el submenu Salir
        salir.addActionListener(new ActionListener() { //clase interna anonima
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); //Sale de la aplicacion
                }
        });
        
        setSize(600, 440); //Establecer tamano a ventana
        setVisible(true); //Pone visible la ventana
    }
    
    //Para mostrar texto en displayArea
    public void mostrarMensaje(String mensaje) {
        areaTexto.append(mensaje + "\n");
    } 
    public void habilitarTexto(boolean editable) {
        campoTexto.setEditable(editable);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        Principal main = new Principal(); //Instanciacion de la clase Principalchat
        main.setLocationRelativeTo(null);   //Centrar el JFrame
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //habilita cerrar la ventana
        ExecutorService executor = Executors.newFixedThreadPool(2); //Para correr los threads
 
        try {
            main.mostrarMensaje("Buscando Servidor ...");
            cliente = new Socket(InetAddress.getByName(ip), 11111); //comunicarme con el servidor
            main.mostrarMensaje("Conectado a :" + cliente.getInetAddress().getHostName());
            
            main.setNombreCliente(cliente.getInetAddress().getHostAddress());
            main.setNombreServidor(cliente.getLocalAddress().getHostAddress());
                    
            
            main.habilitarTexto(true); //habilita el texto
            
            //Ejecucion de los Threads
            executor.execute(new ThreadRecepcion(cliente, main));
            
            
            executor.execute(ThreadEnvio.getInstance(cliente, main)); 
            
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } //Fin del catch //Fin del catch //Fin del catch //Fin del catch
        finally {
        }
        executor.shutdown();
    }
}
