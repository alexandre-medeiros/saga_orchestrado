import java.util.HashMap;
import java.util.Map;

class MyClass{
	
	private int hash;
	
	@Override
	public int hashCode() {
		if(hash == 0) {
			hash = super.hashCode();
		}
		
		return hash;
	}
}

public class StringStuding {
	public static void main(String[] args) {
		MyClass s1 = new MyClass();
		Map<MyClass, Integer> map = new HashMap<>();
		
		map.put(s1, 1);
		int i = map.get(s1);
		
		System.out.println(i);

	}
}
