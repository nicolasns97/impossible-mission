package com.sapienza.jimpossiblemission.controller;
/**
 * Classe astratta che implementa un game loop a FPS fissi utilizzando il pattern Template Method.
 * <p>
 * Fornisce la struttura base per eseguire operazioni periodiche a una frequenza costante,
 * come ad esempio l'aggiornamento della logica di gioco o il rendering grafico.
 * La logica specifica da eseguire ad ogni frame viene delegata al metodo
 * {@link #onFrame()}, che deve essere implementato dalle sottoclassi.
 * </p>
 *
 * <h2>Funzionamento</h2>
 * <ul>
 *   <li>Il ciclo principale viene eseguito nel metodo {@link #run()}, calcolando la durata
 *       di ciascun frame in base agli FPS passati al costruttore.</li>
 *   <li>Il metodo {@link #start()} avvia il thread di gioco, mentre {@link #stop()} lo termina
 *       in modo sicuro, attendendo la conclusione del thread.</li>
 *   <li>All’interno del loop, viene chiamato {@link #onFrame()} ogni volta che è trascorso
 *       il tempo sufficiente per un nuovo frame.</li>
 *   <li>Una chiamata a {@code Thread.sleep(1)} riduce il consumo della CPU, prevenendo il busy-wait
 *       quando non ci sono operazioni immediate da svolgere.</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * La variabile {@code running} è {@code volatile} per garantire la corretta visibilità del suo
 * stato tra thread diversi. Il metodo {@link #stop()} ripristina lo stato di interruzione del
 * thread se viene catturata un'eccezione {@link InterruptedException}.
 *
 * <h2>Utilizzo</h2>
 * <pre>{@code
 * public class GameController extends AbstractGameController {
 *     public GameController(int fps) {
 *         super(fps);
 *     }
 *
 *     @Override
 *     protected void onFrame() {
 *         updateGameLogic();
 *         renderGraphics();
 *     }
 * }
 *
 * GameController controller = new GameController(60);
 * controller.start();
 * }</pre>
 *
 * @author Nicola Sansonetti
 */
public abstract class AbstractGameController implements Runnable {
    private Thread gameThread;
    private volatile boolean running;
    private final int FPS;

    public AbstractGameController(int FPS) {
        if (FPS <= 0) {
            throw new IllegalArgumentException("FPS deve essere maggiore di 0");
        }
        this.FPS = FPS;
    }

    public void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "Game Loop");
        gameThread.start();
    }

    public void stop() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public final void run() {
        final double nanosecondsPerFrame = 1_000_000_000.0 / FPS;
        double frameAccumulator = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long currentTime = System.nanoTime();
            frameAccumulator += (currentTime - lastTime) / nanosecondsPerFrame;
            lastTime = currentTime;

            if (frameAccumulator >= 1) {
                onFrame();
                frameAccumulator--;
            }

            try {
                Thread.sleep(1); // evita busy-wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Hook generico chiamato ad ogni frame.
     * Chi estende la classe decide cosa fare (update, render, logging, ecc.).
     */
    protected abstract void onFrame();
}