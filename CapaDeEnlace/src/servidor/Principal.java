package servidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static Socket conexion; //Socket para conectarse con el cliente
    private static final String ip = JOptionPane.showInputDialog(Principal.main, "IP Host 1 ", "IP Host 1", JOptionPane.INFORMATION_MESSAGE); //ip a la cual se conecta
    private String tasaTransferencia;
    private boolean ack;
    private String nombreCliente;
    private Integer tamanioMensaje;
    private String nombreServidor; 

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public int getTamanioMensaje() {
        return tamanioMensaje;
    }

    public void setTamanioMensaje(int tamanioMensaje) {
        this.tamanioMensaje = tamanioMensaje;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }
           
    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public static Principal main; 
    
    public Principal(){
        super("REDES 2017  Host Servidor"); //Establece titulo al Frame
        
        campoTexto = new JTextField(); //crea el campo para texto
        campoTexto.setEditable(false); //No permite que sea editable el campo de texto
        add(campoTexto, BorderLayout.NORTH); //Coloca el campo de texto en la parte superior
        
        
        areaTexto = new JTextArea(); //Crear displayArea
        areaTexto.setEditable(false);
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        areaTexto.setBackground(Color.DARK_GRAY); //Pone de color gris al areaTexto
        areaTexto.setForeground(Color.WHITE); //pinta negro la letra en el areaTexto
        campoTexto.setForeground(Color.blue); //pinta azul la letra del mensaje a enviar
        
        
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
        ExecutorService executor = Executors.newCachedThreadPool(); //Para correr los threads
 
        try {
            //main.mostrarMensaje("No se encuentra Servidor");
            servidor = new ServerSocket(11111, 100); 
            main.mostrarMensaje("Esperando Cliente ...");

            //Bucle infinito para esperar conexiones de los clientes
            while (true){
                try {
                    conexion = servidor.accept(); //Permite al servidor aceptar conexiones        

                    //main.mostrarMensaje("Conexion Establecida");
                    main.mostrarMensaje("Conectado a : " + conexion.getInetAddress().getHostName());
                    main.setNombreCliente(conexion.getInetAddress().getHostAddress());
                    main.setNombreServidor(conexion.getLocalAddress().getHostAddress());
                    
                    main.habilitarTexto(true); //permite escribir texto para enviar

                    //Ejecucion de los threads
                    
                    executor.execute(new ThreadRecepcion(conexion, main)); //client
                    executor.execute(new ThreadEnvio(conexion, main));
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } //Fin del catch //Fin del catch
        finally {
        }
        executor.shutdown();
    }
}
