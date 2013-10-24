package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Freddy Rondon
 * 
 *         PlanificadorCPU corre una simulacion con uno de los siguientes 4
 *         algoritmos (FCFS,SRT,ROUNDROBIN,PSJF). Puede ser configurado para
 *         ejecutarse de forma autom‡tica toda la simulaci—n de una sola vez o
 *         el programador puede incrementar en un paso a paso.
 */

public class PlanificadorCPU {

	/**
	 * Constantes que especifican cada tipo de algoritmo
	 */
	public static final int FCFS = 1;
	public static final int SRT = 2;
	public static final int PSJF = 3;
	public static final int ROUNDROBIN = 4;

	/**
	 * Numero de procesos aleatorios por defecto
	 */
	static final int NUM_PROC_DEF = 50;

	/**
	 * Tiempo transcurrido
	 */
	long tiempoActual = 0;

	/**
	 * Tiempo de inactividad transcurrdio
	 */
	long tiempoIdle = 0;

	/**
	 * Tiempo transcurrido que el CPU estuvo ocupado
	 */
	long tiempoOcupado = 0;

	/**
	 * Porcion de tiempo para Round Robin
	 */
	long quantum = 10;

	/**
	 * Cuenta atr‡s de cu‡ndo interrumpir un proceso, porque su quantum
	 * termino
	 */
	long quantumCounter = quantum;

	/**
	 * Solo Round Robin, esta variable mantiene un registro del nœmero de
	 * quantum consecutivos que un proceso ha consumido
	 */
	long turnCounter = 0;

	/**
	 * Cantidad de procesos preparados para ejecucion
	 */
	int procesosIn = 0;

	/**
	 * Cantidad de procesos que se han ejecutado hasta el final
	 */
	int procesosOut = 0;

	/**
	 * Si se desea utilizar premption del SJF y algoritmos de prioridad
	 */
	boolean preemptive = true;

	/**
	 * Algoritmo por defecto a utilizar
	 */
	int algoritmo = FCFS;

	/**
	 * FPS Velocidad 
	 */
	int fps = 0;
	
	/**
	 * Coleccion con todos los procesos involucrados en la simulacion
	 */
	Vector<Proceso> todosProcesos = new Vector<Proceso>(NUM_PROC_DEF);

	/**
	 * Coleccion de todos los procesos que seran usados
	 */
	Vector<Proceso> colaProcesos = new Vector<Proceso>(NUM_PROC_DEF);

	/**
	 * Coleccion de todos los procesos que han llegado y requieren CPU
	 */
	Vector<Proceso> colaListos = new Vector<Proceso>(NUM_PROC_DEF);

	/**
	 * Referencia al proceso activo. El cpu cambia esta refencia a diferentes
	 * procesos en la cola de listos usando su respectivo algoritmo a traves
	 * de un criterio.
	 */
	Proceso procesoActivo = null;

	/**
	 * index del vector en colaListos
	 */
	int indexProcesoActivo = 0;
	
	/**
	 * Flag para verificar si fue pausada la ejecucion
	 */
	private Boolean pausada = false;

	/**
	 * Variables para almacenar las estad’sticas recogidas en el tiempo de
	 * espera, respuesta y turnaround
	 */
	int minEspera = 0, maxEspera = 0;
	double mediaEspera = 0.0, desviacionEstandarEspera = 0.0;

	int minRespuesta = 0, maxRespuesta = 0;
	double mediaRespuesta = 0.0, desviacionEstandarRespuesta = 0.0;

	int minTurn = 0, maxTurn = 0;
	double mediaTurn = 0.0, desviacionEstandarTurn = 0.0;

	/**
	 * Constructor por defecto que construye procesos generados aleatoriamente
	 * con la cantidad de procesos en DEF_PROC_COUNT y los cargar en la cola
	 * de trabajo
	 */
	public PlanificadorCPU() {
		construirColaRamdon();
	}

	/**
	 * Vaciar y rellenar el PlanificadorCPU
	 */
	public void construirColaRamdon() {
		procesoActivo = null;
		colaProcesos.clear();
		todosProcesos.clear();
		Proceso p;
		for (int i = 0; i < NUM_PROC_DEF; i++) {
			p = new Proceso();
			todosProcesos.add(p);
		}
		cargarProcesosCola(todosProcesos);
	}

	/**
	 * Constructor que recibe su vector de procesos a cargar personalizados
	 * 
	 * @param ap
	 *              Vector con los procesos ya generados a partir de un
	 *              criterio
	 */
	public PlanificadorCPU(Vector<Proceso> ap) {
		procesoActivo = null;
		todosProcesos = ap;
		cargarProcesosCola(ap);
	}

	/**
	 * Constructor que lee los datos de los procesos de un archivo
	 * 
	 * @param filename
	 *              String que contiene el nombre del archivo
	 */
	PlanificadorCPU(String filename) {
		procesoActivo = null;
		Proceso proc = null;
		BufferedReader br = null;
		long burst = 0, retraso = 0, bloqueado = 0;
		try {
			String s = null;
			br = new BufferedReader(new FileReader(filename));

			while ((s = br.readLine()) != null) {

				StringTokenizer st = new StringTokenizer(s);
				burst = Long.parseLong(st.nextToken());
				retraso = Long.parseLong(st.nextToken());
				bloqueado = Long.parseLong(st.nextToken());
				proc = new Proceso(burst, retraso, bloqueado);
				todosProcesos.add(proc);
			}

		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		}
		cargarProcesosCola(todosProcesos);
	}

	/**
	 * Constructor que lee los datos de los procesos de un archivo
	 * 
	 * @param filename
	 *              Archivo Objeto que contiene las especificaciones de los
	 *              procesos
	 */
	public PlanificadorCPU(File filename) {
		procesoActivo = null;
		Proceso proc = null;
		BufferedReader br = null;
		long burst = 0, retraso = 0, bloqueado = 0;

		try {
			String s = null;
			br = new BufferedReader(new FileReader(filename));

			while ((s = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s);
				burst = Long.parseLong(st.nextToken());
				retraso = Long.parseLong(st.nextToken());
				bloqueado = Long.parseLong(st.nextToken());
				proc = new Proceso(burst, retraso, bloqueado);
				todosProcesos.add(proc);
			}

		} catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
		}
		cargarProcesosCola(todosProcesos);
	}

	/**
	 * Carga todos los procesos en la cola de procesos y configura los tiempos
	 * de llegada
	 * 
	 * @param jobs
	 */
	public void cargarProcesosCola(Vector<Proceso> jobs) {
		Proceso p;
		long tiempoLlegada = 0;
		for (int i = 0; i < jobs.size(); i++) {
			p = (Proceso) jobs.get(i);
			tiempoLlegada += p.getTiempoRetraso();
			p.setTiempoLlegada(tiempoLlegada);
			colaProcesos.add(p);
		}
	}

	/**
	 * Utilice el planificador apropiado para elegir el siguiente proceso. A
	 * continuaci—n, enviaremos el proceso.
	 */
	void Planificador() {
		switch (algoritmo) {
		case FCFS:
			RunFCFS(colaListos);
			break;
		case SRT:
			RunSRT(colaListos);
			break;
		case PSJF:
			RunPSJF(colaListos);
			break;
		case ROUNDROBIN:
			RunRoundRobin(colaListos);
			break;
		default:
			System.out.println("Ningun algoritmo de planificacion valido");
			break;
		}
		Despacho();
	}

	/**
	 * Ejecuta el proceso activo y esperar el resto de ellos
	 */
	void Despacho() {
		Proceso p = null;

		procesoActivo.ejecutando(tiempoActual);

		
		if (procesoActivo.bloqueado == true) {
			// Sacar de la cola de listos
			colaListos.removeElement(procesoActivo);
			// Ejecutamos el hilo de ProcesoBloqueado
			ProcesoBloqueado pb = new ProcesoBloqueado(procesoActivo, fps, colaListos);
			pb.start();
		}
		for (int i = 0; i < colaListos.size(); ++i) {
			p = (Proceso) colaListos.get(i);
			if (p.getPID() != procesoActivo.getPID()) {
				p.esperando(tiempoActual);
			}
		}
	}

	/**
	 * Corre el algoritmo de planificacion FCFS.
	 */
	void RunFCFS(Vector<Proceso> jq) {

		try {
			if (tiempoOcupado == 0 || procesoActivo.getTiempoBurst() == 0 || procesoActivo.bloqueado) {
				procesoActivo = encontrarProcesoLlegaPrimero(jq);
				indexProcesoActivo = jq.indexOf(procesoActivo);
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Corre el algoritmo de planificacion SRT
	 */
	void RunSRT(Vector<Proceso> jq) {

		try {
			if (tiempoOcupado == 0 || procesoActivo.isFinalizado() || procesoActivo.bloqueado) {
				procesoActivo = encontrarProcesoMasPequeno(jq);
				indexProcesoActivo = jq.indexOf(procesoActivo);
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Corre el algoritmo de planificacion PSJF
	 */
	void RunPSJF(Vector<Proceso> jq) {

		try {
			if (tiempoOcupado == 0 || procesoActivo.isFinalizado()
					|| preemptive == true || procesoActivo.bloqueado) {
				procesoActivo = encontrarProcesoMasPequeno(jq);
				indexProcesoActivo = jq.indexOf(procesoActivo);
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Corre el algoritmo de planificacion RoundRobin
	 */
	void RunRoundRobin(Vector<Proceso> jq) {

		try {
			if (tiempoOcupado == 0 || procesoActivo.isFinalizado()
					|| quantumCounter == 0 || procesoActivo.bloqueado) {
				procesoActivo = encontrarProximoProcesoRR(jq);
				indexProcesoActivo = jq.indexOf(procesoActivo);

				quantumCounter = quantum;
			}
			quantumCounter--;
		} catch (NullPointerException e) {
		}

	}

	/**
	 * RR: Obtiene el siguiente proceso que debemos correr
	 */
	Proceso encontrarProximoProcesoRR(Vector<Proceso> que) {
		Proceso proximoProceso = null;
		int index = 0;

		if (indexProcesoActivo >= (que.size() - 1))
			index = 0;
		else if (procesoActivo != null && procesoActivo.isFinalizado()) {
			index = indexProcesoActivo;
		} else {
			index = (indexProcesoActivo + 1);
		}

		proximoProceso = (Proceso) que.get(index);

		return proximoProceso;
	}

	/**
	 * SRT y PSJF: Obtiene el trabajo m‡s peque–o en la cola
	 */
	Proceso encontrarProcesoMasPequeno(Vector<Proceso> que) {
		Proceso p = null, shortest = null;
		long time = 0, shorttime = 0;

		for (int i = 0; i < que.size(); ++i) {
			p = (Proceso) que.get(i);
			time = p.getTiempoBurst();
			if ((time < shorttime) || (i == 0)) {
				shorttime = time;
				shortest = p;
			}
		}
		return shortest;
	}

	/**
	 * FCFS: Obtener el proceso que lleg— primero
	 */
	Proceso encontrarProcesoLlegaPrimero(Vector<Proceso> que) {
		Proceso p = null, earliest = null;
		long tiempo = 0, tiempoLlegada = 0;

		for (int i = 0; i < que.size(); ++i) {
			p = (Proceso) que.get(i);
			tiempo = p.getTiempoLlegada();
			if ((tiempo < tiempoLlegada) || (i == 0)) {
				tiempoLlegada = tiempo;
				earliest = p;
			}
		}
		return earliest;
	}

	/**
	 * Loop a travŽs de la cola de procesos para agarrar las estad’sticas
	 * importantes
	 */
	private void recoleccionEstadisticas() {
		int allWaited = 0, allResponded = 0, allTurned = 0;
		int sDevWaited = 0, sDevWaitedSquared = 0;
		int sDevTurned = 0, sDevTurnedSquared = 0;
		int sDevResponded = 0, sDevRespondedSquared = 0;
		int startedCount = 0, finishedCount = 0;
		Proceso p = null;
		int i = 0;

		for (i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			if (p.isComenzado()) {
				startedCount++;
				int responded = (int) p.getTiempoRespuesta();
				allResponded += responded;
				sDevResponded += responded;
				sDevRespondedSquared += responded * responded;
				if (responded < minRespuesta || i == 0) {
					minRespuesta = responded;
				} else if (responded > maxRespuesta || i == 0) {
					maxRespuesta = responded;
				}
			}
		}

		if (startedCount > 0) {
			mediaRespuesta = ((double) allResponded)
					/ ((double) startedCount);
			if (startedCount > 1) {
				double sdev = (double) sDevRespondedSquared;
				sdev -= (double) (sDevResponded * sDevResponded)
						/ (double) startedCount;
				sdev /= (double) (startedCount - 1);
				desviacionEstandarRespuesta = Math.sqrt(sdev);
			} else {
				desviacionEstandarRespuesta = 0.0;
			}

		} else {
			mediaRespuesta = 0.0;
			desviacionEstandarRespuesta = 0.0;
		}

		for (i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);

			if (p.isFinalizado()) {
				finishedCount++;
				int waited = (int) p.getTiempoEspera();
				int turned = (int) p.getTiempoVida();
				allWaited += waited;
				sDevWaited += waited;
				sDevWaitedSquared += waited * waited;
				allTurned += turned;
				sDevTurned += turned;
				sDevTurnedSquared += turned * turned;

				if (waited < minEspera || i == 0) {
					minEspera = waited;
				} else if (waited > maxEspera || i == 0) {
					maxEspera = waited;
				}

				if (turned < minTurn || i == 0) {
					minTurn = turned;
				} else if (turned > maxTurn || i == 0) {
					maxTurn = turned;
				}

			}
		}

		if (finishedCount > 0) {
			mediaEspera = (double) allWaited / (double) finishedCount;
			mediaTurn = (double) allTurned / (double) finishedCount;

			if (finishedCount > 1) {
				double sdev = (double) sDevWaitedSquared;
				sdev -= (double) (sDevWaited * sDevWaited)
						/ (double) finishedCount;
				sdev /= (double) (finishedCount - 1);
				desviacionEstandarEspera = Math.sqrt(sdev);
				sdev = 0.0;
				sdev = (double) sDevTurnedSquared;
				sdev -= (double) (sDevTurned * sDevTurned)
						/ (double) finishedCount;
				sdev /= (double) (finishedCount - 1);
				desviacionEstandarTurn = Math.sqrt(sdev);
			} else {
				desviacionEstandarEspera = 0.0;
				desviacionEstandarTurn = 0.0;
			}
		} else {
			mediaEspera = 0.0;
			mediaTurn = 0.0;
		}

	}

	/**
	 * Verificar por nuevos procesos.
	 */
	void cargarColaListos() {
		Proceso p;
		for (int i = 0; i < colaProcesos.size(); i++) {
			p = (Proceso) colaProcesos.get(i);
			if (p.getTiempoLlegada() == tiempoActual) {
				colaListos.add(p);
				procesosIn++;
			}
		}

	}

	/**
	 * Remueve procesos finalizados.
	 */
	void purgarColaListos() {
		Proceso p;
		for (int i = 0; i < colaListos.size(); i++) {
			p = (Proceso) colaListos.get(i);
			if (p.isFinalizado() == true) {
				colaListos.remove(i);
				procesosOut++;
			}
		}
	}

	/**
	 * Deshazte de trabajos que se han realizado.
	 */
	void purgarColaProcesos() {
		Proceso p;
		for (int i = 0; i < colaProcesos.size(); i++) {
			p = (Proceso) colaProcesos.get(i);
			if (p.isFinalizado() == true) {
				colaProcesos.remove(i);
			}
		}
	}

	/**
	 * Vaciar en el terminal
	 */
	public void print() {
		Proceso p;
		for (int i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			p.print();
			System.out.println("---------------");
		}
	}

	/** Vaciar cola de listos en el terminal. */
	public void printColaListos() {
		Proceso p;
		for (int i = 0; i < colaListos.size(); i++) {
			p = (Proceso) colaListos.get(i);
			p.print();
			System.out.println("---------------");
		}
	}

	/**
	 * Vaciar en el terminal algo tipo tabla
	 */
	public void printTable() {
		Proceso p;
		for (int i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			p.println();
		}
	}

	/**
	 * Vaciar en el terminal algo tipo formato CSV.
	 */
	public void printCSV() {
		Proceso p;
		System.out.println(getNombreAlgoritmo() + ","
				+ (getPreemption() ? "Preemptive" : ""));
		System.out.println("\"PID\"," + "\"Burst\"," + "\"Prioridad\","
				+ "\"Llegada\"," + "\"Comienzo\"," + "\"Final\","
				+ "\"Espera\"," + "\"Respuesta\"," + "\"Turnaround\"");

		for (int i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			p.printCSV();
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setGroupingUsed(false);

		System.out.println(",,,,,,,,");
		System.out.println(",,,,," + "Min," + minEspera + ","
				+ minRespuesta + "," + minTurn);
		System.out.println(",,,,," + "Media," + nf.format(mediaEspera)
				+ "," + nf.format(mediaRespuesta) + ","
				+ nf.format(mediaTurn));
		System.out.println(",,,,," + "Max," + maxEspera + ","
				+ maxRespuesta + "," + maxTurn);
		System.out.println(",,,,," + "DevEstandar,"
				+ nf.format(desviacionEstandarEspera) + ","
				+ nf.format(desviacionEstandarRespuesta) + ","
				+ nf.format(desviacionEstandarTurn));
	}

	/**
	 * Vaciar en un objeto PrintWriter algo tipo formato CSV
	 */
	public void printCSV(PrintWriter pw) {
		Proceso p;
		pw.println(getNombreAlgoritmo() + ","
				+ (getPreemption() ? "Preemptive" : ""));
		pw.println("\"PID\"," + "\"Burst\"," + "\"Prioridad\","
				+ "\"Llegada\"," + "\"Comienzo\"," + "\"Final\","
				+ "\"Espera\"," + "\"Respuesta\"," + "\"Turnaround\"");

		for (int i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			p.printCSV(pw);
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setGroupingUsed(false);

		pw.println(",,,,,,,,");
		pw.println(",,,,," + "Min," + minEspera + "," + minRespuesta + ","
				+ minTurn);
		pw.println(",,,,," + "Media," + nf.format(mediaEspera) + ","
				+ nf.format(mediaRespuesta) + ","
				+ nf.format(mediaTurn));
		pw.println(",,,,," + "Max," + maxEspera + "," + maxRespuesta + ","
				+ maxTurn);
		pw.println(",,,,," + "DevEstandar,"
				+ nf.format(desviacionEstandarEspera) + ","
				+ nf.format(desviacionEstandarRespuesta) + ","
				+ nf.format(desviacionEstandarTurn));
	}

	/**
	 * Obtener el valor de preemptive.
	 * 
	 * @return Valor de preemptive.
	 */
	public boolean getPreemption() {
		return preemptive;
	}

	/**
	 * Asignar el valor de preemptive.
	 * 
	 * @param v
	 *              Valor para asignar a preemptive.
	 */
	public void setPreemption(boolean v) {
		this.preemptive = v;
	}

	/**
	 * Asignar el valor de algoritmo.
	 * 
	 * @param algo
	 *              El algoritmo a usar para esta simulacion.
	 */
	public void setAlgoritmo(int algo) {
		algoritmo = algo;
	}

	/**
	 * Obtener el valor de algoritmo.
	 * 
	 * @return Valor de algoritmo.
	 */
	public int getAlgoritmo() {
		return algoritmo;
	}

	/**
	 * Obtener el numero de ciclos idle del cpu.
	 * 
	 * @return Numero de ciclos idle del cpu.
	 */
	public long getTiempoIdle() {
		return tiempoIdle;
	}

	/**
	 * Obtener el tiempo total de corrida de la simulacion.
	 * 
	 * @return tiempo total de corrida.
	 */
	public long getTiempoActual() {
		return tiempoActual;
	}

	/**
	 * Obtener el tiempo total que el cpu fue usado.
	 * 
	 * @return Ciclos de uso del cpu.
	 */
	public long getTiempoOcupado() {
		return tiempoOcupado;
	}

	/**
	 * Obtener el valor del quantum.
	 * 
	 * @return Valor del quantum.
	 */
	public long getQuantum() {
		return quantum;
	}

	/**
	 * Asignar el valor del quantum.
	 * 
	 * @param v
	 *              Valor para asignar al quantum.
	 */
	public void setQuantum(long v) {
		this.quantum = v;
	}

	/**
	 * Obtener el numero de procesos completados.
	 * 
	 * @return Valor de procsOut.
	 */
	public long getProcsOut() {
		return procesosOut;
	}

	/**
	 * Obtener el numero de procesos recibidos.
	 * 
	 * @return Valor de procsIn.
	 */
	public long getProcsIn() {
		return procesosIn;
	}

	/**
	 * Obtener la carga del sistema.
	 * 
	 * @return La carga actual del sistema, que son los procesos de entrada
	 *         sobre los de salida.
	 */
	public double getLoad() {
		return ((double) procesosIn / (double) procesosOut);
	}

	/**
	 * Obtener el proceso que se est‡ ejecutando activamente
	 */
	public Proceso getProcesoActivo() {
		return procesoActivo;
	}

	/**
	 * Ejecutar toda la simulaci—n en un bucle while.
	 */
	public void Simular() {
		while (proximoCiclo())
			;
	}

	/**
	 * Corre solo un ciclo de la simulacion. Esto representa una unidad de
	 * tiempo
	 * 
	 * @return un boolean que es true si mas ciclos quedan por ejecutar.
	 */
	public boolean proximoCiclo() {
		boolean masCiclos = false;
		if (colaProcesos.isEmpty()) {
			masCiclos = false;
		} else {
			cargarColaListos();
			masCiclos = true;
			if (colaListos.isEmpty()) {
				tiempoIdle++;
			} else {
				Planificador();
				tiempoOcupado++;
				cleanUp();
			}
			tiempoActual++;
		}
		recoleccionEstadisticas();
		return masCiclos;
	}

	/**
	 * Purga las colas de ejecucion
	 */
	void cleanUp() {
		purgarColaProcesos();
		purgarColaListos();
	}

	/**
	 * Restaura variables de tiempo y estadisticas a sus valores por defecto.
	 * Tambien restaura todos los procesos a su estado original y recarga las
	 * colas. Deja la configuracion del algoritmo de planificacion y otras
	 * variables de estado.
	 */
	public void restaurar() {
		Proceso p;

		procesoActivo = null;
		tiempoActual = 0;
		tiempoOcupado = 0;
		tiempoIdle = 0;
		procesosIn = 0;
		procesosOut = 0;
		quantum = 10;
		quantumCounter = quantum;
		turnCounter = 0;

		minEspera = 0;
		mediaEspera = 0;
		maxEspera = 0;
		desviacionEstandarEspera = 0;

		minRespuesta = 0;
		mediaRespuesta = 0;
		maxRespuesta = 0;
		desviacionEstandarRespuesta = 0;

		minTurn = 0;
		mediaTurn = 0;
		maxTurn = 0;
		desviacionEstandarTurn = 0;

		for (int i = 0; i < todosProcesos.size(); i++) {
			p = (Proceso) todosProcesos.get(i);
			p.restaurar();
		}
		colaProcesos.clear();
		colaListos.clear();
		cargarProcesosCola(todosProcesos);

	}

	/**
	 * Obtiene todos los procesos
	 * 
	 * @return Vector de todos los procesos
	 */
	public Vector<Proceso> getTodosProcesos() {
		return todosProcesos;
	}

	/**
	 * Obtiene la media del tiempo de espera de los procesos
	 * 
	 * @return un double con la media del tiempo de espera
	 */
	public double getMeanWait() {
		return mediaEspera;
	}

	/**
	 * Obtiene el minimo del tiempo de espera de los procesos
	 * 
	 * @return un entero con el minimo del tiempo de espera
	 */
	public int getMinWait() {
		return minEspera;
	}

	/**
	 * Obtiene el maximo del tiempo de espera de los procesos
	 * 
	 * @return un entero con el minimo del tiempo de espera
	 */
	public int getMaxWait() {
		return maxEspera;
	}

	/**
	 * Obtiene la desviacion estandar del tiempo de espera de los procesos
	 * 
	 * @return un double con la desviacion estandar del tiempo de espera
	 */
	public double getStdDevWait() {
		return desviacionEstandarEspera;
	}

	/**
	 * Obtiene la media del tiempo de respuesta de los procesos
	 * 
	 * @return un double con la media del tiempo de respuesta
	 */
	public double getMeanResponse() {
		return mediaRespuesta;
	}

	/**
	 * Obtiene el minimo del tiempo de respuesta de los procesos
	 * 
	 * @return un entero con el minimo del tiempo de respuesta
	 */
	public int getMinResponse() {
		return minRespuesta;
	}

	/**
	 * Obtiene el maximo del tiempo de respuesta de los procesos
	 * 
	 * @return un entero con el maximo del tiempo de respuesta
	 */
	public int getMaxResponse() {
		return maxRespuesta;
	}

	/**
	 * Obtiene la desviacion estandar del tiempo de respuesta de los procesos
	 * 
	 * @return un double con la desviacion estandar del tiempo de respuesta
	 */
	public double getStdDevResponse() {
		return desviacionEstandarRespuesta;
	}

	/**
	 * Obtiene la media del tiempo de turn around de los procesos
	 * 
	 * @return un double con la media del tiempo de turn around
	 */
	public double getMeanTurn() {
		return mediaTurn;
	}

	/**
	 * Obtiene el minimo del tiempo de turn around de los procesos
	 * 
	 * @return un entero con el minimo del tiempo de turn around
	 */
	public int getMinTurn() {
		return minTurn;
	}

	/**
	 * Obtiene el maximo del tiempo de turn around de los procesos
	 * 
	 * @return un entero con el maximo del tiempo de turn around
	 */
	public int getMaxTurn() {
		return maxTurn;
	}

	/**
	 * Obtiene la desviacion estandar del tiempo de turn around de los
	 * procesos
	 * 
	 * @return un double con la desviacion estandar del tiempo de turn around
	 */
	public double getStdDevTurn() {
		return desviacionEstandarTurn;
	}

	/**
	 * Obtiene un string con el nombre del algoritmo actual
	 * 
	 * @return String que tiene el nombre actual del algoritmo que esta
	 *         corriendo
	 */
	public String getNombreAlgoritmo() {
		String s = "";
		switch (algoritmo) {
		case FCFS:
			s = "First come first serve";
			break;
		case SRT:
			s = "Shortest remaining time";
			break;
		case PSJF:
			s = "Preemptive Shortest Job First";
			break;
		case ROUNDROBIN:
			s = "Round Robin";
			break;
		default:
			break;
		}
		return s;
	}

	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * @param fps the fps to set
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}

	/**
	 * @return the pausada
	 */
	public Boolean getPausada() {
		return pausada;
	}

	/**
	 * @param pausada the pausada to set
	 */
	public void setPausada(Boolean pausada) {
		this.pausada = pausada;
	}

}
