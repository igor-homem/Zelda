package world;

public class Camera {
	
	public static int x = 3;
	public static int y = 3;
	
	public static int clamp(int Atual, int Min, int Max) {
		if(Atual < Min) {
			Atual = Min;
		}
		
		if(Atual > Max) {
			Atual = Max;
		}
		
		return Atual;
	}
	
}
