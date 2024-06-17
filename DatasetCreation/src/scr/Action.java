package scr;

public class Action 
{
	/* 
	 * Questa classe rappresenta le azioni che possono essere eseguite all'interno delle simulazioni di gara.
	 * Il metodo toString fornisce una stringa formattata dei valori delle azioni, mentre il metodo 
	 * limitValues assicura che questi valori rimangano all'interno dei loro range validi.
	 */

	public double accelerate = 0; // 0..1
	public double brake = 0; // 0..1
	public double clutch = 0; // 0..1
	public int gear = 0; // -1..6
	public double steering = 0; // -1..1
	public boolean restartRace = false;
	public int focus = 360;	// ML Angolo di messa a fuoco desiderato in gradi [-90; 90],
							// impostare 360 se non si desidera alcuna lettura della messa a fuoco!
	public String keyPressed_x = "N";
	public String keyPressed_y = "N";

	public String toString() 
	{
		limitValues();
		return "(accel " + accelerate + ") " + "(brake " + brake + ") " + "(clutch " + clutch + ") " + "(gear " + gear
				+ ") " + "(steer " + steering + ") " + "(meta " + (restartRace ? 1 : 0) + ") " + "(focus " + focus // ML
				+ ")" + "(keyPressed_x " + keyPressed_x + ") " + "(keyPressed_y " + keyPressed_y + ") ";
	}

	public void limitValues()
	{
		accelerate = Math.max(0, Math.min(1, accelerate));
		brake = Math.max(0, Math.min(1, brake));
		clutch = Math.max(0, Math.min(1, clutch));
		steering = Math.max(-1, Math.min(1, steering));
		gear = Math.max(-1, Math.min(6, gear));

	}
}
