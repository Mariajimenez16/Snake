import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * Juego de Snake (Gusano que come manzanas)
 * El jugador controla un gusano que crece al comer manzanas
 * El juego termina si el gusano choca consigo mismo o con los bordes
 */
public class Juegosnake extends JPanel implements ActionListener, KeyListener {
    
    // Constantes del juego
    private static final int ANCHO_VENTANA = 600;  // Ancho de la ventana en píxeles
    private static final int ALTO_VENTANA = 600;   // Alto de la ventana en píxeles
    private static final int TAMANO_CELDA = 20;    // Tamaño de cada celda del gusano/manzana
    private static final int DELAY = 100;          // Velocidad del juego en milisegundos
    
    // Variables del juego
    private ArrayList<Point> gusano;  // Lista de puntos que forman el cuerpo del gusano
    private Point manzana;            // Posición de la manzana
    private String direccion;         // Dirección actual: "ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"
    private boolean juegoActivo;      // Indica si el juego está en curso
    private Timer timer;              // Temporizador para actualizar el juego
    private Random random;            // Generador de números aleatorios para posicionar manzanas
    private int puntuacion;           // Puntuación del jugador
    
    /**
     * Constructor: Inicializa el juego
     */
    public Juegosnake() {
        // Configurar el panel
        setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        // Inicializar variables
        gusano = new ArrayList<>();
        random = new Random();
        
        // Iniciar el juego
        iniciarJuego();
        
        // Crear y empezar el temporizador
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    /**
     * Inicializa o reinicia el juego
     */
    private void iniciarJuego() {
        // Limpiar el gusano y crear uno nuevo en el centro
        gusano.clear();
        int centroX = ANCHO_VENTANA / 2 / TAMANO_CELDA;
        int centroY = ALTO_VENTANA / 2 / TAMANO_CELDA;
        
        // El gusano empieza con 3 segmentos
        gusano.add(new Point(centroX, centroY));
        gusano.add(new Point(centroX - 1, centroY));
        gusano.add(new Point(centroX - 2, centroY));
        
        // Dirección inicial hacia la derecha
        direccion = "DERECHA";
        
        // Generar la primera manzana
        generarManzana();
        
        // Activar el juego
        juegoActivo = true;
        puntuacion = 0;
    }
    
    /**
     * Genera una nueva manzana en una posición aleatoria
     * que no esté ocupada por el gusano
     */
    private void generarManzana() {
        int maxX = ANCHO_VENTANA / TAMANO_CELDA;
        int maxY = ALTO_VENTANA / TAMANO_CELDA;
        
        // Intentar generar manzana hasta encontrar posición libre
        do {
            int x = random.nextInt(maxX);
            int y = random.nextInt(maxY);
            manzana = new Point(x, y);
        } while (gusano.contains(manzana));
    }
    
    /**
     * Mueve el gusano en la dirección actual
     */
    private void moverGusano() {
        // Obtener la cabeza actual del gusano
        Point cabeza = gusano.get(0);
        Point nuevaCabeza = new Point(cabeza);
        
        // Calcular nueva posición según la dirección
        switch (direccion) {
            case "ARRIBA":
                nuevaCabeza.y--;
                break;
            case "ABAJO":
                nuevaCabeza.y++;
                break;
            case "IZQUIERDA":
                nuevaCabeza.x--;
                break;
            case "DERECHA":
                nuevaCabeza.x++;
                break;
        }
        
        // Verificar colisiones con los bordes
        if (nuevaCabeza.x < 0 || nuevaCabeza.x >= ANCHO_VENTANA / TAMANO_CELDA ||
            nuevaCabeza.y < 0 || nuevaCabeza.y >= ALTO_VENTANA / TAMANO_CELDA) {
            juegoActivo = false;
            return;
        }
        
        // Verificar colisión con el propio cuerpo
        if (gusano.contains(nuevaCabeza)) {
            juegoActivo = false;
            return;
        }
        
        // Agregar nueva cabeza
        gusano.add(0, nuevaCabeza);
        
        // Verificar si comió la manzana
        if (nuevaCabeza.equals(manzana)) {
            puntuacion += 10;
            generarManzana();
        } else {
            // Si no comió, quitar la cola (el gusano se mueve)
            gusano.remove(gusano.size() - 1);
        }
    }
    
    /**
     * Dibuja todos los elementos del juego
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (juegoActivo) {
            // Dibujar el gusano
            g.setColor(Color.GREEN);
            for (int i = 0; i < gusano.size(); i++) {
                Point segmento = gusano.get(i);
                // La cabeza se dibuja más brillante
                if (i == 0) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(segmento.x * TAMANO_CELDA, segmento.y * TAMANO_CELDA, 
                          TAMANO_CELDA, TAMANO_CELDA);
            }
            
            // Dibujar la manzana
            g.setColor(Color.RED);
            g.fillOval(manzana.x * TAMANO_CELDA, manzana.y * TAMANO_CELDA, 
                      TAMANO_CELDA, TAMANO_CELDA);
            
            // Dibujar puntuación
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Puntuación: " + puntuacion, 10, 20);
            g.drawString("Longitud: " + gusano.size(), 10, 40);
            
        } else {
            // Pantalla de Game Over
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String mensaje = "¡Game Over!";
            FontMetrics fm = g.getFontMetrics();
            int x = (ANCHO_VENTANA - fm.stringWidth(mensaje)) / 2;
            g.drawString(mensaje, x, ALTO_VENTANA / 2 - 50);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String puntos = "Puntuación Final: " + puntuacion;
            x = (ANCHO_VENTANA - g.getFontMetrics().stringWidth(puntos)) / 2;
            g.drawString(puntos, x, ALTO_VENTANA / 2);
            
            String reiniciar = "Presiona ESPACIO para jugar de nuevo";
            x = (ANCHO_VENTANA - g.getFontMetrics().stringWidth(reiniciar)) / 2;
            g.drawString(reiniciar, x, ALTO_VENTANA / 2 + 50);
        }
    }
    
    /**
     * Método llamado por el Timer en cada intervalo
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (juegoActivo) {
            moverGusano();
        }
        repaint(); // Redibujar la pantalla
    }
    
    /**
     * Detecta cuando se presiona una tecla
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (juegoActivo) {
            // Cambiar dirección según la tecla presionada
            // No permitir ir en dirección opuesta directamente
            switch (tecla) {
                case KeyEvent.VK_UP:
                    if (!direccion.equals("ABAJO")) {
                        direccion = "ARRIBA";
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (!direccion.equals("ARRIBA")) {
                        direccion = "ABAJO";
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (!direccion.equals("DERECHA")) {
                        direccion = "IZQUIERDA";
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!direccion.equals("IZQUIERDA")) {
                        direccion = "DERECHA";
                    }
                    break;
            }
        } else {
            // Si el juego terminó, ESPACIO reinicia
            if (tecla == KeyEvent.VK_SPACE) {
                iniciarJuego();
            }
        }
    }
    
    // Métodos requeridos por KeyListener (no los usamos pero son obligatorios)
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    /**
     * Método principal - Punto de entrada del programa
     */
    public static void main(String[] args) {
        // Crear la ventana del juego
        JFrame ventana = new JFrame("Juego del Gusano - Snake");
        Juegosnake juego = new Juegosnake();
        
        ventana.add(juego);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null); // Centrar ventana
        ventana.setResizable(false);
        ventana.setVisible(true);
    }
}