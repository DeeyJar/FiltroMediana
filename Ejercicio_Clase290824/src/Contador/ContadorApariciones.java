package Contador;

public class ContadorApariciones {
	
	public static void iterativo (int[] num, int min, int max) {
		if (min > max) {
			System.out.println("El minimo no debe ser mayor al maximo");
			return;
		}
		
		int cont = 0;
		
		for (int i = 0; i < num.length; i++) {
			if (num[i] >= min && num[i] <= max) cont ++;
		}
		
		System.out.println("Cant apariciones: " + cont);
	}
	
	private static int ofuscatedCalc1(int[] a, int min, int max, int n) {
		if (n == a.length)
			return 0;

		if (a[n] >= min && a[n] <= max) {
			return 1 + ofuscatedCalc1(a, min, max, n + 1);
		} else {
			return 0 + ofuscatedCalc1(a, min, max, n + 1);
		}

	}

	private static int ofuscatedCalc2(int[] a, int min, int max, int vecLength) {

		if(vecLength == 0 || min > max) {
			return 0;
		}
		
		if(a[vecLength] >= min && a[vecLength] <= max) {
			
			
		} else {
		if (a[vecLength] >= max) {
			return 0 + ofuscatedCalc2(a,min,max,vecLength/2);
		}
		if (a[vecLength] <= min) {
			return 0 + ofuscatedCalc2(a,min,max,(vecLength*3)/4);
		}
		}
	

		return 0;
	}
	
	public static int Calc(int[] a, int min, int max) {
		int res = ofuscatedCalc1(a, min, max, 0);
		return res;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] b = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int min = 5;
		int max = 7;
		System.out.println(Calc(b, min, max));
	}

}
