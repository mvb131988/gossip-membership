package root;

public class LamportTimestampOperator {

	private int max;
	
	public LamportTimestampOperator(int max) {
		this.max = max;
	}
	
	public int next(int current) {
		if (current < max) {
			return current + 1;
		}
		return -max;
	}
	
	/**
	 * lt1 < lt2 => -1
	 * lt1 > lt2 => 1
	 * lt1 == lt2 => 0
	 * 
	 * @param lt1
	 * @param lt2
	 * @return
	 */
	public int compare(int lt1, int lt2) {
		if (max - 11 < lt1 && lt1 < max + 1) {
			if (lt2 < 0) {
				return -1;
			} else {
				compareInternally(lt1, lt2);
			}
		}
		if (-max - 1 < lt1 && lt1 < -max + 11) {
			if (lt2 > 0) {
				return 1;
			} else {
				compareInternally(lt1, lt2);
			}
		}
		return compareInternally(lt1, lt2);
	}
	
	private int compareInternally(int lt1, int lt2) {
		if(lt1 < lt2) {
			return -1;
		}
		if(lt1 > lt2) {
			return 1;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		LamportTimestampOperator lto = new LamportTimestampOperator(100);
		//expected -1
		int res = lto.compare(101, -98);
		System.out.println(res);
	}
	
}
