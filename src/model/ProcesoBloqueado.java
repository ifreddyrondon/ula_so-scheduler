package model;

import java.util.Vector;

public class ProcesoBloqueado extends Thread {

	private Proceso p;
	private int delay;
	private Vector<Proceso> colaListos;

	public ProcesoBloqueado(Proceso _p, int _delay, Vector<Proceso> _colaListos) {
		p = _p;
		delay = _delay;
		colaListos = _colaListos;
	}

	public void run() {
			long tiempoBloqueado = p.tBloqueadoIO;
			for (int i = (int) tiempoBloqueado; i > 0; i--) {
				try {
					sleep(delay);
					if (p.tBloqueadoReferencia < tiempoBloqueado && p.tBloqueadoIO > 0) {
						p.tBloqueadoReferencia++;
						p.tBloqueadoIO--;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			p.bloqueado = false;
			if (p.bloqueado == false && p.tBloqueadoIO == 0) {
				colaListos.add(p);
			}
			
			this.interrupt();
	}

	/**
	 * @return the p
	 */
	public Proceso getP() {
		return p;
	}

}
