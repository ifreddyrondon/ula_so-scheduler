package model;

import java.io.*;

/**
 * @author Freddy Rondon
 * 
 *         Proceso: Tipo de dato Proceso que representa un proceso a programar.
 *         Y mantendra todo su estado interno
 */

public class Proceso {

	/**
	 * Numero de identificacion del procesos (PID). Debe ser unico. Va desde [
	 * 0 - sizeof(long) ]
	 */
	long PID = 0;

	/**
	 * Guarda el valor del siguiente PID a usar, No es un recolector de
	 * basura. Solo toma el siguiente numero disponible, es la misma variable
	 * en TODAS las instancias de los procesos Va desde [ 0 - sizeof(long) ]
	 */
	static long siguientePID = 0;

	// ************TIEMPOS*********************************

	/**
	 * CPU burst Time: Cantidad de tiempo que un proceso utiliza el CPU
	 */
	long tBurst = 0; // [0 - 100]

	/**
	 * Guarda el estado inicial del burst. Para mostrar un total restante
	 */
	long tInitBurst = 0;

	/**
	 * Retraso de la llegada. Cuanto tiempo despues de la llegada del proceso
	 * anterior llego este proceso? Diferencia horaria en segundos
	 */
	long tRetraso = 0; // [0 - 95]

	/**
	 * Tiempo que un proceso dura bloqueado por I/O
	 */
	long tBloqueadoIO = 0;
	
	/**
	 * Tiempo que le queda a un proceso para desbloquearse
	 */
	long tBloqueadoReferencia = 0;
	
	/**
	 * tiempo bloqueado de respaldo por si se resetean los procesos
	 */
	long tBloquedoRespaldo = 0;

	/**
	 * Tiempo real en el que el proceso llego, sera puesto por el planificador
	 */
	long tLlegada = 0;

	/**
	 * Tiempo que el proceso comienza la ejecucion Sera inicializado por el
	 * planificador
	 */
	long tInicio = 0;

	/**
	 * Tiempo que el proceso termina la ejecucion. Sera puesto por el
	 * planificador
	 */
	long tTermina = 0;

	/**
	 * Total del tiempo que el proceso toma esperando. Medicion desde el
	 * tiempo que llega hasta q termina
	 */
	long tTotalEspera = 0;

	/**
	 * Medicion del tiempo despues de la llegada que toma para comenzar la
	 * ejecucion
	 */
	long tRespuesta = 0;

	/**
	 * Tiempo total de vida del proceso, si esta listo o esperando o
	 * corriendo, tiempo total que dura en las garras del planificador
	 */
	long tTotalVida = 0;

	/**
	 * Manera de saber si un proceso ha llegado
	 */
	boolean llegado = false;

	/**
	 * Manera de saber si un proceso ha comenzado a ejecutarse
	 */
	boolean comenzado = false;

	/**
	 * Manera de saber si un proceso ha terminado
	 */
	boolean finalizado = false;

	/**
	 * Manera de saber si un proceso esta preparado para ejecutarce este ciclo
	 */
	boolean activo = false;

	/**
	 * Manera de saber si un proceso esta bloqueado
	 */
	public boolean bloqueado = false;

	/**
	 * Constructor por defecto. Genera aleatoriamente un proceso y llena sus
	 * atributos usando limites especificos sobre ellos
	 */
	public Proceso() {
		siguientePID++;
		PID = siguientePID;
		tBurst = (long) (Math.random() * 99 + 1);
		tInitBurst = tBurst;
		tRetraso = (long) (Math.random() * 40 + 1);
		tBloqueadoIO = (long) (Math.random() * 10 + 1);
		tBloquedoRespaldo = tBloqueadoIO;
	}

	/**
	 * Constructor parametrico. Ninguna generacion aleatoria Se asume que
	 * todos los valores seran pasados por parametros Este método es útil para
	 * la construcción de una cola de proceso en el que los datos provienen de
	 * un archivo u otra fuente como funciones probabilisticas.
	 * 
	 * @param _burst
	 *              El burst time de un proceso.
	 * @param _retraso
	 *              El retraso en el proceso de llegada medido a partir de la
	 *              llegada del proceso anterior.
	 * @param _bloqueado
	 * 		     El tiempo que pasa bloqueado esperando por I/O
	 */
	public Proceso(long _burst, long _retraso, long _bloqueado) {
		siguientePID++;
		PID = siguientePID;
		tBurst = _burst;
		tInitBurst = tBurst;
		tRetraso = _retraso;
		tBloqueadoIO = _bloqueado;
		tBloquedoRespaldo = tBloqueadoIO;
	}

	/**
	 * Ir a través de los movimientos de la ejecución de un ciclo de un
	 * proceso. Utiliza la hora actual para comprobar si se han producido
	 * algunos acontecimientos (ejm. llegado, comenzado, finalizado) y
	 * establece el estado de esos eventos booleanos.
	 */
	public synchronized void ejecutando(long tiempoActual) {

		activo = true;

		if (tiempoActual == tLlegada) {
			llegado = true;
		}

		if (tBurst == tInitBurst) {
			comenzado = true;
			tInicio = tiempoActual;
			tRespuesta = tInicio - tLlegada;
			
			if(tBloqueadoIO > 0){
				bloqueado = true;
			}
		}

		if (bloqueado == false) {
			tBurst--;
		}
		
		tTotalVida++;

		if (tBurst == 0) {
			finalizado = true;
			tTermina = tiempoActual;
		}
	}

	/**
	 * El inverso de ejecutando. Ir a través de los movimientos de la espera
	 * de tiempos del CPU. Utiliza la hora actual para comprobar si esta es la
	 * hora de llegada.
	 */
	public synchronized void esperando(long tiempoActual) {
		activo = false;
		tTotalVida++;
		tTotalEspera++;
		if (tiempoActual == tLlegada) {
			llegado = true;
		}
	}

	/**
	 * Restaura un proceso a su estado original. Para volver a ejecutar un
	 * conjunto de datos bajo otras circunstancias
	 */
	public void restaurar() {
		tBurst = tInitBurst;
		tTotalVida = 0;
		tBloqueadoReferencia = 0;
		tBloqueadoIO = tBloquedoRespaldo;
		tRespuesta = 0;
		tInicio = 0;
		tTotalEspera = 0;
		activo = false;
		comenzado = false;
		finalizado = false;
		llegado = false;
	}

	/**
	 * Muestra el estado de un proceso en el terminal
	 */
	public void print() {
		System.out.println("PID: " + PID + "\nTiempo Burst: " + tBurst
				+ "Tiempo inicial Burst  : " + tInitBurst + "\n"
				+ "Tiempo bloqueado: " + tBloqueadoReferencia + "\n"
				+ "Tiempo bloqueado restante: " + tBloqueadoIO + "\n"
				+ "Tiempo Retraso   : " + tRetraso + "\n"
				+ "Tiempo LLegada : " + tLlegada + "\n"
				+ "Tiempo Inicio   : " + tInicio + "\n"
				+ "Tiempo Final  : " + tTermina + "\n"
				+ "Tiempo Espera    : " + tTotalEspera + "\n"
				+ "Tiempo Respuesta: " + tRespuesta);
	}
	
	/**
	 * Estado de un proceso
	 * @return
	 * 			Retorna un String con el estado de un proceso
	 */
	public String printString() {
		return "PID: " + PID + "\nTiempo Burst: " + tBurst + "\n" 
				+ "Tiempo inicial Burst  : " + tInitBurst + "\n"
				+ "Tiempo bloqueado: " + tBloqueadoReferencia + "\n"
				+ "Tiempo bloqueado restante: " + tBloqueadoIO + "\n"
				+ "Tiempo Retraso   : " + tRetraso + "\n"
				+ "Tiempo LLegada : " + tLlegada + "\n"
				+ "Tiempo Inicio   : " + tInicio + "\n"
				+ "Tiempo Final  : " + tTermina + "\n"
				+ "Tiempo Espera    : " + tTotalEspera + "\n"
				+ "Tiempo Respuesta: " + tRespuesta;
	}

	/**
	 * Muestra el estado en una linea. Para formatos tabulares. Hay que
	 * encontrar la manera de obtener el formato de tabla.
	 */
	public void println() {
		System.out.println("PID " + PID + " tBurst " + tBurst
				+ " tBloqueado " + tBloqueadoIO + " tLlega " + tLlegada
				+ " tIni " + tInicio + " tFin " + tTermina + " tEspe "
				+ tTotalEspera + " tResp " + tRespuesta);
	}

	/**
	 * Imprime valores en el terminal separados por , (coma)
	 */
	public void printCSV() {
		System.out.println(PID + "," + tInitBurst + "," + tBloqueadoIO
				+ "," + tLlegada + "," + tInicio + "," + tTermina + ","
				+ tTotalEspera + "," + tRespuesta + "," + tTotalVida);

	}

	/**
	 * Imprime valores separados por , (coma) a un objeto PrintWriter
	 */
	public void printCSV(PrintWriter pw) {
		pw.println(PID + "," + tInitBurst + "," + tBloqueadoIO + ","
				+ tLlegada + "," + tInicio + "," + tTermina + ","
				+ tTotalEspera + "," + tRespuesta + "," + tTotalVida);
	}

	/**
	 * Obtiene el Valor de respuesta.
	 * 
	 * @return Valor de respuesta.
	 */
	public long getTiempoRespuesta() {
		return tRespuesta;
	}

	/**
	 * Obtiene el Valor del tiempo total de espera.
	 * 
	 * @return Valor de tiempoTotalEspera.
	 */
	public long getTiempoEspera() {
		return tTotalEspera;
	}

	/**
	 * Obtiene el Valor de tiempo de finalizacion.
	 * 
	 * @return Valor de termina.
	 */
	public long getTiempoFinalizacion() {
		return tTermina;
	}

	/**
	 * Obtiene el Valor de inicio.
	 * 
	 * @return Valor de inicio.
	 */
	public long getTiempoInicio() {
		return tInicio;
	}

	/**
	 * Obtiene el Valor de llegada.
	 * 
	 * @return Valor de llegada.
	 */
	public long getTiempoLlegada() {
		return tLlegada;
	}

	/**
	 * Ajusta el valor de llegada.
	 * 
	 * @param v
	 *              Valor para asignar a llegada.
	 */
	public void setTiempoLlegada(long v) {
		this.tLlegada = v;
	}
	
	/**
	 * Obtiene el Valor del tiempo bloqueado.
	 * 
	 * @return Valor de tiempo de bloqueo.
	 */
	public long getTiempoBloqueado() {
		return tBloqueadoIO;
	}

	/**
	 * Ajusta el valor del tiempo bloqueado.
	 * 
	 * @param v
	 *              Valor para asignar el tiempo de bloqueo.
	 */
	public void setTiempoBloqueo(long b) {
		this.tBloqueadoIO = b;
	}

	/**
	 * Obtiene el Valor de retraso.
	 * 
	 * @return Valor de retraso.
	 */
	public long getTiempoRetraso() {
		return tRetraso;
	}

	/**
	 * Obtiene el Valor de burst.
	 * 
	 * @return Valor de burst.
	 */
	public long getTiempoBurst() {
		return tBurst;
	}

	/**
	 * Obtiene el valor incial de burst de el proceso.
	 * 
	 * @return Valor de initBurst.
	 */
	public long getTiempoInitBurst() {
		return tInitBurst;
	}

	/**
	 * Obtiene el Valor de PID.
	 * 
	 * @return Valor de PID.
	 */
	public long getPID() {
		return PID;
	}

	/**
	 * Obtiene el Valor de lifetime
	 * 
	 * @return tiempo actual de vida en la cola del planificador.
	 */
	public long getTiempoVida() {
		return tTotalVida;
	};

	/**
	 * Obtiene el Valor de activo.
	 * 
	 * @return Valor de activo.
	 */
	public boolean isActivo() {
		return activo;
	}

	/**
	 * Obtiene el Valor de finalizado.
	 * 
	 * @return Valor de finalizado.
	 */
	public boolean isFinalizado() {
		return finalizado;
	}

	/**
	 * Obtiene el Valor de comenzado.
	 * 
	 * @return Valor de comenzado.
	 */
	public boolean isComenzado() {
		return comenzado;
	}

	/**
	 * Obtiene el Valor de llegado.
	 * 
	 * @return Valor de llegado.
	 */
	public boolean isLlegado() {
		return llegado;
	}
	
	/**
	 * Obtiene el Valor de bloqueado.
	 * 
	 * @return Valor de bloqueado.
	 */
	public boolean isBloqueado() {
		return bloqueado;
	}
}
